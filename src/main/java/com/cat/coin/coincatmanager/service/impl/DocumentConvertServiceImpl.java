package com.cat.coin.coincatmanager.service.impl;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aspose.cells.Workbook;
import com.aspose.pdf.*;
import com.aspose.pdf.devices.*;
import com.aspose.words.CssStyleSheetType;
import com.aspose.words.FontSettings;
import com.cat.coin.coincatmanager.controller.vo.DocumentConvertVo;
import com.cat.coin.coincatmanager.controller.vo.DocumentHistoryPageVo;
import com.cat.coin.coincatmanager.domain.enums.FileType;
import com.cat.coin.coincatmanager.domain.pojo.PageParam;
import com.cat.coin.coincatmanager.domain.pojo.Document;
import com.cat.coin.coincatmanager.mapper.DocumentConvertHistoryMapper;
import com.cat.coin.coincatmanager.mapper.UserMapper;
import com.cat.coin.coincatmanager.service.DocumentConvertService;
import com.cat.coin.coincatmanager.utils.DocumentUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.List;
import java.util.UUID;
import java.io.*;
import com.github.pagehelper.PageHelper;
import org.apache.commons.io.FileUtils;

@Service
public class DocumentConvertServiceImpl implements DocumentConvertService {
    @Autowired
    private ResourceLoader resourceLoader;

    @Value("${file.path}")
    private String filePath;

    @Value("${words.license.path}")
    private String wordsLicensePath;

    @Value("${pdf.license.path}")
    private String pdfLicensePath;

    @Value("${cells.license.path}")
    private String cellsLicensePath;

    @Autowired
    private DocumentConvertHistoryMapper documentConvertHistoryMapper;

    @Autowired
    private UserMapper userMapper;

    @Async("async-executor-guava")
    @Override
    public void documentConvertSchedule(DocumentConvertVo pdfConvertVo, Document document, InputStream inputStream) throws Exception {
        String fileName = pdfConvertVo.getFile().getOriginalFilename();
        //保存时的文件名
        String dateName = UUID.randomUUID() + "-" + fileName;

        //保存文件的绝对路径
        String oldUrl = this.filePath +  File.separator + "uploads" ;
        File pFile = new File(oldUrl);
        if(!pFile.exists()){
            pFile.mkdirs();
        }
        File oldFile = new File(pFile,dateName);
        try{
            //写入源文件
            FileUtils.copyInputStreamToFile(inputStream,oldFile);
            document.setOldPath(oldUrl+File.separator+dateName);
            document.setStatus(1);
            documentConvertHistoryMapper.updateById(document);
        }catch (Exception e){
            document.setOldPath(null);
            document.setStatus(3);
            documentConvertHistoryMapper.updateById(document);
        }
        try{
            //读取配置文件
            //写入转换后文件
            String[] splits = fileName.split("\\.");
            if(FileType.PDF == pdfConvertVo.getSourceType()){
                InputStream is = new FileInputStream(new File(pdfLicensePath));//license文件的位置
                License license = new License();
                license.setLicense(is);
                String newUrl = this.filePath +  File.separator + "converts" + File.separator + UUID.randomUUID() + "-" + splits[0]   + "." + (pdfConvertVo.getTargetType() == FileType.TIFF ? "tif" : pdfConvertVo.getTargetType().toString().toLowerCase());
                FileOutputStream os = new FileOutputStream(newUrl);
                com.aspose.pdf.Document doc = new com.aspose.pdf.Document(oldUrl+File.separator+dateName);//加载源文件数据
                FontSettings font = new FontSettings();
                font.setFontsFolder("/usr/share/fonts/chinese", true);
                Resolution resolution = new Resolution(300);
                if(pdfConvertVo.getTargetType() == FileType.HTML){
                    HtmlSaveOptions htmlsaveOptions = new HtmlSaveOptions();
                    htmlsaveOptions.setFixedLayout(true);
                    htmlsaveOptions.setCompressSvgGraphicsIfAny(false);
                    htmlsaveOptions.setSaveTransparentTexts(true);
                    htmlsaveOptions.setSaveShadowedTextsAsTransparentTexts(true);
                    htmlsaveOptions.setExcludeFontNameList(new String[]{"ArialMT", "SymbolMT"});
                    htmlsaveOptions.setFontSavingMode(HtmlSaveOptions.FontSavingModes.DontSave);
                    htmlsaveOptions.setDefaultFontName("Comic Sans MS");
                    htmlsaveOptions.setUseZOrder(true);
                    htmlsaveOptions
                            .setLettersPositioningMethod(LettersPositioningMethods.UseEmUnitsAndCompensationOfRoundingErrorsInCss);
                    htmlsaveOptions
                            .setPartsEmbeddingMode(HtmlSaveOptions.PartsEmbeddingModes.NoEmbedding);
                    htmlsaveOptions
                            .setRasterImagesSavingMode(HtmlSaveOptions.RasterImagesSavingModes.AsEmbeddedPartsOfPngPageBackground);
                    htmlsaveOptions.setSplitIntoPages(false);
                    doc.save(newUrl, htmlsaveOptions);//设置转换文件类型并转换
                }else if(pdfConvertVo.getTargetType() == FileType.TXT){
                    TextAbsorber ta = new TextAbsorber();
                    ta.visit(doc);
                    BufferedWriter writer = new BufferedWriter(new FileWriter(newUrl));
                    writer.write(ta.getText());
                    writer.close();
                }else if(pdfConvertVo.getTargetType() == FileType.XLSX){
                    ExcelSaveOptions excelSave = new ExcelSaveOptions();
                    excelSave.setFormat(ExcelSaveOptions.ExcelFormat.XLSX);
                    doc.save(newUrl,excelSave);
                }else if(pdfConvertVo.getTargetType() == FileType.JPEG){
                    JpegDevice jpegDevice = new JpegDevice(resolution);
                    convertPDFtoImages(jpegDevice,newUrl,doc);
                }else if(pdfConvertVo.getTargetType() == FileType.PNG){
                    PngDevice pngDevice = new PngDevice(resolution);
                    convertPDFtoImages(pngDevice,newUrl,doc);
                }else if(pdfConvertVo.getTargetType() == FileType.TIFF){
                    TiffSettings tiffSettings = new TiffSettings();
                    tiffSettings.setCompression(CompressionType.None);
                    tiffSettings.setDepth(ColorDepth.Default);
                    tiffSettings.setShape(ShapeType.Landscape);
                    TiffDevice tiffDevice = new TiffDevice(resolution, tiffSettings);
                    tiffDevice.process(doc, 1, 1, new java.io.FileOutputStream(
                            newUrl));
                }else if(pdfConvertVo.getTargetType() == FileType.BMP){
                    BmpDevice bmpDevice = new BmpDevice(resolution);
                    convertPDFtoImages(bmpDevice,newUrl,doc);
                }else if(pdfConvertVo.getTargetType() == FileType.EMF){
                    EmfDevice emfDevice = new EmfDevice(resolution);
                    convertPDFtoImages(emfDevice,newUrl,doc);
                }else if(pdfConvertVo.getTargetType() == FileType.GIF){
                    GifDevice gifDevice = new GifDevice(resolution);
                    convertPDFtoImages(gifDevice,newUrl,doc);
                }else if(pdfConvertVo.getTargetType() == FileType.EPUB){
                    EpubSaveOptions options = new EpubSaveOptions();
                    options.setContentRecognitionMode(EpubSaveOptions.RecognitionMode.Flow);
                    doc.save(newUrl, options);
                    doc.close();
                }else if(pdfConvertVo.getTargetType() == FileType.XPS){
                    XpsSaveOptions saveOptions = new XpsSaveOptions();
                    doc.save(newUrl, saveOptions);
                    doc.close();
                }else{
                    doc.save(os, DocumentUtils.getAsposePdfFormatType(pdfConvertVo.getTargetType()));//设置转换文件类型并转换
                }

                document.setNewPath(newUrl);
                document.setStatus(2);
                documentConvertHistoryMapper.updateById(document);
                doc.close();
                is.close();
                os.close();
            }else if(FileType.DOCX == pdfConvertVo.getSourceType() || FileType.DOC == pdfConvertVo.getSourceType()){
                InputStream is = new FileInputStream(new File(wordsLicensePath));//license文件的位置
                License license = new License();
                license.setLicense(is);
                String newUrl = this.filePath +  File.separator + "converts" + File.separator + UUID.randomUUID() + "-" + splits[0]    + "." + (pdfConvertVo.getTargetType() == FileType.TIFF ? "tif" : pdfConvertVo.getTargetType().toString().toLowerCase());
                FileOutputStream os = new FileOutputStream(newUrl);
                com.aspose.words.Document doc = new com.aspose.words.Document(oldUrl+File.separator+dateName);//加载源文件数据
                FontSettings font = new FontSettings();
                font.setFontsFolder("/usr/share/fonts/chinese", true);
                if(pdfConvertVo.getTargetType() == FileType.HTML){
                    com.aspose.words.HtmlSaveOptions saveOptions = new com.aspose.words.HtmlSaveOptions();
                    saveOptions.setCssStyleSheetType(CssStyleSheetType.EXTERNAL);
                    saveOptions.setExportFontResources(true);
                    saveOptions.setResourceFolder(this.filePath +  File.separator  + "resources");
                    doc.save(newUrl,saveOptions);
                }else if(pdfConvertVo.getTargetType() == FileType.SVG){
                    com.aspose.words.SvgSaveOptions saveOptions = new com.aspose.words.SvgSaveOptions();
                    saveOptions.setResourcesFolder(this.filePath +  File.separator  + "resources");
                    doc.save(newUrl,saveOptions);
                }else{
                    doc.save(os, DocumentUtils.getAsposeWordFormatType(pdfConvertVo.getTargetType()));//设置转换文件类型并转换
                }
                document.setNewPath(newUrl);
                document.setStatus(2);
                documentConvertHistoryMapper.updateById(document);
                is.close();
                os.close();
            }else if(FileType.XLSX == pdfConvertVo.getSourceType() || FileType.XLS == pdfConvertVo.getSourceType()){
                InputStream is = new FileInputStream(new File(cellsLicensePath));//license文件的位置
                License license = new License();
                license.setLicense(is);
                Workbook workbook = new Workbook(oldUrl + File.separator + dateName);
                String newUrl = this.filePath +  File.separator + "converts" + File.separator + UUID.randomUUID() + "-" + splits[0]    + "." + (pdfConvertVo.getTargetType() == FileType.TIFF ? "tif" : pdfConvertVo.getTargetType().toString().toLowerCase());
                FileOutputStream os = new FileOutputStream(newUrl);
                if(pdfConvertVo.getTargetType() == FileType.PDF){
                    com.aspose.cells.PdfSaveOptions pdfSaveOptions = new com.aspose.cells.PdfSaveOptions();
                    pdfSaveOptions.setOnePagePerSheet(true);
                    printSheetPage(workbook);
                    workbook.save(os, pdfSaveOptions);
                }else{
                    workbook.save(os,DocumentUtils.getAsposeExcelFormatType(pdfConvertVo.getTargetType()));
                }
                document.setNewPath(newUrl);
                document.setStatus(2);
                documentConvertHistoryMapper.updateById(document);
                is.close();
                os.close();
            }
        }catch(Exception e){
            e.printStackTrace();
            document.setNewPath(null);
            document.setStatus(4);
            documentConvertHistoryMapper.updateById(document);
        }

    }

    public static void convertPDFtoImages(ImageDevice imageDevice,
                                          String newUrl,
                                          com.aspose.pdf.Document document) throws IOException {
        for (int pageCount = 1; pageCount <= document.getPages().size(); pageCount++) {
            java.io.OutputStream imageStream = new java.io.FileOutputStream(
                    newUrl);
            imageDevice.process(document.getPages().get_Item(pageCount), imageStream);
            imageStream.close();
        }
    }

    private static void printSheetPage(Workbook wb) {
        for (int i = 1; i < wb.getWorksheets().getCount(); i++) {
            wb.getWorksheets().get(i).setVisible(true);
        }
    }


    @Override
    public Document documentConvert(DocumentConvertVo documentConvertVo) throws Exception {
            String fileName = documentConvertVo.getFile().getOriginalFilename();
            Document document = new Document();
            document.setId(UUID.randomUUID().toString());
            document.setName(fileName);
            document.setStatus(0);
            document.setSourceType(documentConvertVo.getSourceType());
            document.setTargetType(documentConvertVo.getTargetType());
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            JSONObject user = JSONObject.parseObject(JSON.toJSONString(authentication.getPrincipal()));
            Integer userId = user.getJSONObject("user").getInteger("id");
            document.setCreator(userId);
            documentConvertHistoryMapper.insert(document);
            return documentConvertHistoryMapper.selectById(document.getId());
    }

    @Override
    public List<Document> getHistory() {
        return documentConvertHistoryMapper.selectAll();
    }

    @Override
    public ResponseEntity<Resource> downloadOldFile(String id,HttpServletResponse response) throws IOException {
        Document pdf = documentConvertHistoryMapper.selectById(id);
        File file = new File(pdf.getOldPath());
        //文件名编码，防止中文乱码
        String filename = URLEncoder.encode(pdf.getOldPath().split("/")[pdf.getOldPath().split("/").length-1], "UTF-8");
        Resource resource = resourceLoader.getResource("file:" + file.getAbsolutePath());
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache");
        headers.add("Content-Type","application/octet-stream");
        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Resource> downloadNewFile(String id, HttpServletResponse response) throws IOException {
        Document pdf = documentConvertHistoryMapper.selectById(id);
        File file = new File(pdf.getNewPath());
        //文件名编码，防止中文乱码
        String filename = URLEncoder.encode(pdf.getNewPath().split("/")[pdf.getNewPath().split("/").length-1], "UTF-8");
        Resource resource = resourceLoader.getResource("file:" + file.getAbsolutePath());
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache");
        headers.add("Content-Type","application/octet-stream");
        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }

    @Override
    public void delete(String id) {
        Document pdf = documentConvertHistoryMapper.selectById(id);
        if(pdf.getOldPath() != null){
            File oldFile = new File(pdf.getOldPath());
            oldFile.delete();
        }
        if(pdf.getNewPath() != null){
            File newFile = new File(pdf.getNewPath());
            newFile.delete();
        }

        documentConvertHistoryMapper.delete(id);
    }

    @Override
    public List<Document> getHistoryByPage(DocumentHistoryPageVo pdfHistoryPageVo) {
        if(pdfHistoryPageVo.getPageSize() != PageParam.PAGE_SIZE_NONE) {
            PageHelper.startPage(pdfHistoryPageVo.getPageNum(), pdfHistoryPageVo.getPageSize());
        }
        return documentConvertHistoryMapper.select(pdfHistoryPageVo);
    }

    @Override
    public void updateById(Document pdf) {
        documentConvertHistoryMapper.updateById(pdf);
    }
}


