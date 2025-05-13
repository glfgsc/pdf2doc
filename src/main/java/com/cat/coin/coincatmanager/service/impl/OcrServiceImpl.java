package com.cat.coin.coincatmanager.service.impl;

import com.cat.coin.coincatmanager.config.LocalFileMappingConfigurer;
import com.cat.coin.coincatmanager.controller.vo.DocumentConvertVo;
import com.cat.coin.coincatmanager.domain.pojo.Document;
import com.cat.coin.coincatmanager.service.OcrService;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service("OcrService")
public class OcrServiceImpl implements OcrService {
    @Value("${ocr-upload.path}")
    private String uploadPath;

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

    @Override
    public String upload(MultipartFile file) throws IOException {
        String url = "https://www.icoincat.cn/api";
        String originName = file.getOriginalFilename();
        String[] split = originName.split("\\.");
        String fileName =  UUID.randomUUID() + "." + split[1];
        String path = LocalFileMappingConfigurer.LOCAL_FILE_MAPPING_PREFIX + File.separator + fileName;
        if (path.contains("\\")) {
            path = path.replace("\\", "/");
        }
        FileCopyUtils.copy(file.getBytes(), new File(uploadPath + fileName));
        return String.format("%s/%s", url, path);
    }
}
