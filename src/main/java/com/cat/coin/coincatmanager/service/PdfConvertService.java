package com.cat.coin.coincatmanager.service;

import com.cat.coin.coincatmanager.controller.vo.PdfConvertVo;
import com.cat.coin.coincatmanager.controller.vo.PdfHistoryPageVo;
import com.cat.coin.coincatmanager.domain.pojo.Pdf;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

public interface PdfConvertService {
    Pdf pdfConvert(PdfConvertVo pdfConvertVo) throws Exception;
    void pdfConvertSchedule(PdfConvertVo pdfConvertVo, Pdf pdf, InputStream inputStream) throws Exception;

    List<Pdf> getHistory();

    ResponseEntity<Resource> downloadOldFile(String id, HttpServletResponse response) throws IOException;
    ResponseEntity<Resource> downloadNewFile(String id,HttpServletResponse response) throws IOException;
    void delete(String id);
    List<Pdf> getHistoryByPage(PdfHistoryPageVo pdfHistoryPageVo);
    void updateById(Pdf pdf);
}
