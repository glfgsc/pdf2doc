package com.cat.coin.coincatmanager.service.impl;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aspose.pdf.License;
import com.aspose.words.FontSettings;
import com.cat.coin.coincatmanager.controller.vo.PdfConvertVo;
import com.cat.coin.coincatmanager.controller.vo.PdfHistoryPageVo;
import com.cat.coin.coincatmanager.domain.pojo.PageParam;
import com.cat.coin.coincatmanager.domain.pojo.Pdf;
import com.cat.coin.coincatmanager.domain.pojo.SecurityUser;
import com.cat.coin.coincatmanager.domain.pojo.User;
import com.cat.coin.coincatmanager.mapper.PdfConvertHistoryMapper;
import com.cat.coin.coincatmanager.mapper.UserMapper;
import com.cat.coin.coincatmanager.service.PdfConvertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.core.io.ResourceLoader;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.io.*;
import com.github.pagehelper.PageHelper;
import org.apache.commons.io.FileUtils;

@Service
public class PdfConvertServiceImpl implements PdfConvertService {
    @Autowired
    private ResourceLoader resourceLoader;

    @Value("${file.path}")
    private String filePath;

    @Value("${license.path}")
    private String licensePath;

    @Autowired
    private PdfConvertHistoryMapper pdfConvertHistoryMapper;

    @Autowired
    private UserMapper userMapper;

    @Async
    @Override
    public void pdfConvertSchedule(PdfConvertVo pdfConvertVo,Pdf pdf,InputStream inputStream) throws Exception {
        String fileName = pdfConvertVo.getFile().getOriginalFilename();
        //保存时的文件名
        String dateName = UUID.randomUUID() + "-" + fileName;

        //保存文件的绝对路径
        String oldUrl = this.filePath +  File.separator + "uploads" ;
        File pFile = new File(oldUrl);
        if(!pFile.exists()){
            pFile.mkdirs();
        }
        File oldFile = new File(pFile,dateName);
        try{
            //写入源文件
            FileUtils.copyInputStreamToFile(inputStream,oldFile);
            pdf.setOldPath(oldUrl+File.separator+dateName);
            pdf.setStatus(1);
            pdfConvertHistoryMapper.updateById(pdf);
        }catch (Exception e){
            pdf.setOldPath(null);
            pdf.setStatus(3);
            pdfConvertHistoryMapper.updateById(pdf);
        }
        try{
            //读取配置文件
            InputStream is = new FileInputStream(new File(licensePath));//license文件的位置
            License license = new License();
            license.setLicense(is);
            //写入转换后文件

            String[] splits = fileName.split("\\.");
            if("pdf".equals(splits[1])){
                String newUrl = this.filePath +  File.separator + "converts" + File.separator + UUID.randomUUID() + "-" + splits[0]   + ".docx";
                FileOutputStream os = new FileOutputStream(newUrl);
                com.aspose.pdf.Document doc = new com.aspose.pdf.Document(oldUrl+File.separator+dateName);//加载源文件数据
                FontSettings font = new FontSettings();
                font.setFontsFolder("/usr/share/fonts/chinese", true);
                doc.save(os, com.aspose.pdf.SaveFormat.DocX);//设置转换文件类型并转换
                pdf.setNewPath(newUrl);
                pdf.setStatus(2);
                pdfConvertHistoryMapper.updateById(pdf);
                doc.close();
                is.close();
                os.close();
            }else if("docx".equals(splits[1])){
                String newUrl = this.filePath +  File.separator + "converts" + File.separator + UUID.randomUUID() + "-" + splits[0]   + ".pdf";
                FileOutputStream os = new FileOutputStream(newUrl);
                com.aspose.words.Document doc = new com.aspose.words.Document(oldUrl+File.separator+dateName);//加载源文件数据
                FontSettings font = new FontSettings();
                font.setFontsFolder("/usr/share/fonts/chinese", true);
                doc.save(os, com.aspose.words.SaveFormat.PDF);//设置转换文件类型并转换
                pdf.setNewPath(newUrl);
                pdf.setStatus(2);
                pdfConvertHistoryMapper.updateById(pdf);
                is.close();
                os.close();
            }
        }catch(Exception e){
            e.printStackTrace();
            pdf.setNewPath(null);
            pdf.setStatus(4);
            pdfConvertHistoryMapper.updateById(pdf);
        }

    }

    @Override
    public Pdf pdfConvert(PdfConvertVo pdfConvertVo) throws Exception {
            String fileName = pdfConvertVo.getFile().getOriginalFilename();
            Pdf pdf = new Pdf();
            pdf.setId(UUID.randomUUID().toString());
            pdf.setName(fileName);
            pdf.setStatus(0);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            JSONObject user = JSONObject.parseObject(JSON.toJSONString(authentication.getPrincipal()));
            Integer userId = user.getJSONObject("user").getInteger("id");
            pdf.setCreator(userId);
            pdfConvertHistoryMapper.insert(pdf);
            return pdfConvertHistoryMapper.selectById(pdf.getId());
    }

    @Override
    public List<Pdf> getHistory() {
        return pdfConvertHistoryMapper.selectAll();
    }

    @Override
    public ResponseEntity<Resource> downloadOldFile(String id,HttpServletResponse response) throws IOException {
        Pdf pdf = pdfConvertHistoryMapper.selectById(id);
        File file = new File(pdf.getOldPath());
        //文件名编码，防止中文乱码
        String filename = URLEncoder.encode(pdf.getOldPath().split("/")[pdf.getOldPath().split("/").length-1], "UTF-8");
        Resource resource = resourceLoader.getResource("file:" + file.getAbsolutePath());
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache");
        headers.add("Content-Type","application/octet-stream");
        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Resource> downloadNewFile(String id, HttpServletResponse response) throws IOException {
        Pdf pdf = pdfConvertHistoryMapper.selectById(id);
        File file = new File(pdf.getNewPath());
        //文件名编码，防止中文乱码
        String filename = URLEncoder.encode(pdf.getNewPath().split("/")[pdf.getNewPath().split("/").length-1], "UTF-8");
        Resource resource = resourceLoader.getResource("file:" + file.getAbsolutePath());
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache");
        headers.add("Content-Type","application/octet-stream");
        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }

    @Override
    public void delete(String id) {
        Pdf pdf = pdfConvertHistoryMapper.selectById(id);
        File oldFile = new File(pdf.getOldPath());
        File newFile = new File(pdf.getNewPath());
        oldFile.delete();
        newFile.delete();
        pdfConvertHistoryMapper.delete(id);
    }

    @Override
    public List<Pdf> getHistoryByPage(PdfHistoryPageVo pdfHistoryPageVo) {
        if(pdfHistoryPageVo.getPageSize() != PageParam.PAGE_SIZE_NONE){
            PageHelper.startPage(pdfHistoryPageVo.getPageNum(), pdfHistoryPageVo.getPageSize());
        }
        return pdfConvertHistoryMapper.select(pdfHistoryPageVo.getCreator());
    }

    @Override
    public void updateById(Pdf pdf) {
        pdfConvertHistoryMapper.updateById(pdf);
    }
}
