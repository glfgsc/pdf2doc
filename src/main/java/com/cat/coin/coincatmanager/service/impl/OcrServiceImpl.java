package com.cat.coin.coincatmanager.service.impl;

import com.cat.coin.coincatmanager.service.OcrService;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Service("OcrService")
public class OcrServiceImpl implements OcrService {
    private final Tesseract tesseract;
    public OcrServiceImpl() {
        tesseract = new Tesseract();
        tesseract.setDatapath("tessdata");
        tesseract.setLanguage("eng+chi_sim");
    }
    @Override
    public String extractText(MultipartFile file) throws IOException {
        try {
            // 转换文件为BufferedImage
            BufferedImage image = ImageIO.read(file.getInputStream());

            return tesseract.doOCR(image);
        } catch (TesseractException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}
