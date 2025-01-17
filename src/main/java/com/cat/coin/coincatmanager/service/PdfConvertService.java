package com.cat.coin.coincatmanager.service;

import com.cat.coin.coincatmanager.controller.vo.PdfConvertVo;
import com.cat.coin.coincatmanager.domain.pojo.Pdf;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface PdfConvertService {
    void pdfConvert(PdfConvertVo pdfConvertVo) throws IOException;
    List<Pdf> getHistory();
}
