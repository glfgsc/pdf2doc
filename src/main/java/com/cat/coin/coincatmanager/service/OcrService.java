package com.cat.coin.coincatmanager.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public interface OcrService {
    public String extractText(MultipartFile file) throws IOException;
}
