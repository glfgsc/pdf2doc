package com.cat.coin.coincatmanager.controller;

import com.cat.coin.coincatmanager.controller.vo.PdfConvertVo;
import com.cat.coin.coincatmanager.controller.vo.PdfHistoryPageVo;
import com.cat.coin.coincatmanager.controller.vo.PdfHistoryVo;
import com.cat.coin.coincatmanager.domain.pojo.PageResult;
import com.cat.coin.coincatmanager.domain.pojo.Pdf;
import com.cat.coin.coincatmanager.service.PdfConvertService;
import com.cat.coin.coincatmanager.utils.HttpContextUtils;
import com.cat.coin.coincatmanager.utils.SpringContextUtils;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
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
    public void downloadOldFile(String id) throws IOException {
        pdfConvertService.downloadOldFile(id);
    }

    @GetMapping("/downloadNewFile")
    public void downloadNewFile(String id) throws IOException {
        pdfConvertService.downloadNewFile(id);
    }

    @GetMapping("/delete")
    public void delete(String id){
        pdfConvertService.delete(id);
    }
}
