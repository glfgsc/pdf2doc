package com.cat.coin.coincatmanager.controller;

import com.cat.coin.coincatmanager.controller.vo.PdfConvertVo;
import com.cat.coin.coincatmanager.controller.vo.PdfHistoryPageVo;
import com.cat.coin.coincatmanager.controller.vo.PdfHistoryVo;
import com.cat.coin.coincatmanager.domain.pojo.AjaxResult;
import com.cat.coin.coincatmanager.domain.pojo.PageResult;
import com.cat.coin.coincatmanager.domain.pojo.Pdf;
import com.cat.coin.coincatmanager.service.PdfConvertService;
import com.cat.coin.coincatmanager.utils.HttpContextUtils;
import com.cat.coin.coincatmanager.utils.SpringContextUtils;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

@RestController
@Validated
@RequestMapping("/pdf")
@EnableAsync
public class PdfController {
    private static final String UPLOAD_DIR = "uploads/";

    @Autowired
    private PdfConvertService pdfConvertService;

    @PostMapping("/convert")
    public Pdf pdfConvert(PdfConvertVo pdfConvertVo) throws Exception {
        Pdf pdf = pdfConvertService.pdfConvert(pdfConvertVo);
        pdfConvertService.pdfConvertSchedule(pdfConvertVo,pdf,pdfConvertVo.getFile().getInputStream());
        return pdf;
    }
    @GetMapping("/history")
    public PageResult<Pdf> getHistory(PdfHistoryPageVo pdfHistoryPageVo){
        List<Pdf> historyByPage = pdfConvertService.getHistoryByPage(pdfHistoryPageVo);
        return new PageResult<>(historyByPage,new PageInfo(historyByPage).getTotal());
    }

    @GetMapping("/downloadOldFile")
    public ResponseEntity<Resource> downloadOldFile(String id, HttpServletResponse response) throws IOException {
        return pdfConvertService.downloadOldFile(id,response);
    }

    @GetMapping("/downloadNewFile")
    public ResponseEntity<Resource> downloadNewFile(String id,HttpServletResponse response) throws IOException {
        return pdfConvertService.downloadNewFile(id,response);
    }

    @GetMapping("/downloadOldFileUrl")
    public AjaxResult downloadOldFileUrl(String id){
        return AjaxResult.success("http://8.130.116.117:8081/pdf/downloadOldFile?id=" + id);
    }

    @GetMapping("/downloadNewFileUrl")
    public AjaxResult downloadNewFileUrl(String id){
        return AjaxResult.success("http://8.130.116.117:8081/pdf/downloadNewFile?id=" + id);
    }

    @GetMapping("/delete")
    public void delete(String id){
        pdfConvertService.delete(id);
    }
}
