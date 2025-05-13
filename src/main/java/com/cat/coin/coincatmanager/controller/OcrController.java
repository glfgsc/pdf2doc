package com.cat.coin.coincatmanager.controller;

import com.cat.coin.coincatmanager.controller.vo.DocumentConvertVo;
import com.cat.coin.coincatmanager.domain.pojo.AjaxResult;
import com.cat.coin.coincatmanager.domain.pojo.Document;
import com.cat.coin.coincatmanager.service.OcrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@Validated
@RequestMapping("/ocr")
public class OcrController {
    @Autowired
    private OcrService ocrService;

    @PostMapping("/upload")
    public AjaxResult upload(@RequestParam("file") MultipartFile file) throws IOException {
        return AjaxResult.success((ocrService.upload(file)));
    }

    @PostMapping("/text")
    public AjaxResult imageToText(@RequestParam("file") MultipartFile file) throws Exception {
        return AjaxResult.success(ocrService.extractText(file));
    }
}
