package com.cat.coin.coincatmanager.service.impl;
//import org.apache.pdfbox.pdmodel.PDDocument;
//import org.apache.pdfbox.text.PDFTextStripper;
//import org.apache.poi.xwpf.usermodel.XWPFDocument;
//import org.apache.poi.xwpf.usermodel.XWPFParagraph;
//import org.apache.poi.xwpf.usermodel.XWPFRun;
import com.cat.coin.coincatmanager.controller.vo.PdfConvertVo;
import com.cat.coin.coincatmanager.domain.pojo.Pdf;
import com.cat.coin.coincatmanager.mapper.PdfConvertHistoryMapper;
import com.cat.coin.coincatmanager.service.PdfConvertService;
import com.cat.coin.coincatmanager.utils.HttpContextUtils;
import com.cat.coin.coincatmanager.utils.SpringContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spire.pdf.FileFormat;
import com.spire.pdf.PdfDocument;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.io.*;

@Service
public class PdfConvertServiceImpl implements PdfConvertService {
    private static final String CONVERT_DIR = "converts/";

    @Autowired
    private PdfConvertHistoryMapper pdfConvertHistoryMapper;

    @Override
    public void pdfConvert(PdfConvertVo pdfConvertVo){
        String fileName = pdfConvertVo.getFile().getOriginalFilename();
        //保存时的文件名
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar calendar = Calendar.getInstance();
        String dateName = df.format(calendar.getTime())+pdfConvertVo.getFile().getOriginalFilename();

        //保存文件的绝对路径
        WebApplicationContext webApplicationContext = (WebApplicationContext) SpringContextUtils.applicationContext;
        ServletContext servletContext = webApplicationContext.getServletContext();
        String realPath = servletContext.getRealPath("/");
        String filePath = realPath + "WEB-INF"+File.separator + "classes" + File.separator +"static" + File.separator + "resource" + File.separator+dateName;
        File newFile = new File(filePath);
        // 保存文件
        try {
            //上传文件
            pdfConvertVo.getFile().transferTo(newFile);
            //数据库存储的相对路径
            String projectPath = servletContext.getContextPath();
            HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
            String contextPath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+projectPath;
            String oldUrl = contextPath + "/resource/uploads"+ dateName;

            PdfDocument doc = new PdfDocument();
            doc.loadFromBytes(pdfConvertVo.getFile().getBytes());
            String[] splits = fileName.split("\\.");
            String newUrl = contextPath + "/resource/uploads/"+ splits[0] + "_" + UUID.randomUUID() + ".docx";
            doc.saveToFile(newUrl, FileFormat.DOCX);
            doc.close();
            Pdf pdf = new Pdf();
            pdf.setName(fileName);
            pdf.setNewPath(newUrl);
            pdf.setOldPath(oldUrl);
            pdfConvertHistoryMapper.insert(pdf);
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public List<Pdf> getHistory() {
        return pdfConvertHistoryMapper.selectAll();
    }
}
