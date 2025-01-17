package com.cat.coin.coincatmanager.controller;

import com.cat.coin.coincatmanager.controller.vo.PdfConvertVo;
import com.cat.coin.coincatmanager.controller.vo.PdfHistoryVo;
import com.cat.coin.coincatmanager.domain.pojo.Pdf;
import com.cat.coin.coincatmanager.service.PdfConvertService;
import com.cat.coin.coincatmanager.utils.HttpContextUtils;
import com.cat.coin.coincatmanager.utils.SpringContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
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
public class PdfController {
    private static final String UPLOAD_DIR = "uploads/";

    @Autowired
    private PdfConvertService pdfConvertService;

    @PostMapping("/convert")
    public void pdfConvert(PdfConvertVo pdfConvertVo) throws IOException {
        pdfConvertService.pdfConvert(pdfConvertVo);
    }

    @GetMapping("/history")
    public List<Pdf>  getHistory(){
        return pdfConvertService.getHistory();
    }
}
