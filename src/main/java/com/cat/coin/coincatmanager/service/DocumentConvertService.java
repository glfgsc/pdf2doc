package com.cat.coin.coincatmanager.service;

import com.cat.coin.coincatmanager.controller.vo.DocumentConvertVo;
import com.cat.coin.coincatmanager.controller.vo.DocumentHistoryPageVo;
import com.cat.coin.coincatmanager.domain.pojo.Document;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

public interface DocumentConvertService {
    Document documentConvert(DocumentConvertVo documentConvertVo) throws Exception;
    void documentConvertSchedule(DocumentConvertVo pdfConvertVo, Document pdf, InputStream inputStream) throws Exception;

    List<Document> getHistory();

    ResponseEntity<Resource> downloadOldFile(String id, HttpServletResponse response) throws IOException;
    ResponseEntity<Resource> downloadNewFile(String id,HttpServletResponse response) throws IOException;
    void delete(String id);
    List<Document> getHistoryByPage(DocumentHistoryPageVo pdfHistoryPageVo);
    void updateById(Document pdf);
}
