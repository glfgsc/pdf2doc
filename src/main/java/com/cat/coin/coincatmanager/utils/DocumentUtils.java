package com.cat.coin.coincatmanager.utils;

import com.aspose.cells.License;
import com.aspose.pdf.SaveFormat;
import com.cat.coin.coincatmanager.domain.enums.FileType;
import org.springframework.beans.factory.annotation.Value;

import java.io.InputStream;

public class DocumentUtils {
    @Value("${cells.license.path}")
    private static String cellsLicensePath;
    public static int getAsposePdfFormatType(FileType fileType){
        if(fileType == FileType.DOCX){
            return SaveFormat.DocX;
        }else if(fileType == FileType.DOC){
            return SaveFormat.Doc;
        }else if(fileType == FileType.SVG){
            return SaveFormat.Svg;
        }else if(fileType == FileType.PPTX){
            return SaveFormat.Pptx;
        }else if(fileType == FileType.XLS || fileType == FileType.XLSX){
            return SaveFormat.Excel;
        }else if(fileType == FileType.HTML){
            return SaveFormat.Html;
        }
        return -1;
    }

    public static int getAsposeWordFormatType(FileType fileType){
        if(fileType == FileType.PDF){
            return com.aspose.words.SaveFormat.PDF;
        }else if(fileType == FileType.PNG){
            return com.aspose.words.SaveFormat.PNG;
        }else if(fileType == FileType.JPEG){
            return com.aspose.words.SaveFormat.JPEG;
        }else if(fileType == FileType.SVG){
            return com.aspose.words.SaveFormat.SVG;
        }else if(fileType == FileType.HTML){
            return com.aspose.words.SaveFormat.HTML;
        }else if(fileType == FileType.EPUB){
            return com.aspose.words.SaveFormat.EPUB;
        }else if(fileType == FileType.MHTML){
            return com.aspose.words.SaveFormat.MHTML;
        }else if(fileType == FileType.BMP){
            return com.aspose.words.SaveFormat.BMP;
        }else if(fileType == FileType.EMF){
            return com.aspose.words.SaveFormat.EMF;
        }else if(fileType == FileType.GIF){
            return com.aspose.words.SaveFormat.GIF;
        }else if(fileType == FileType.TIFF){
            return com.aspose.words.SaveFormat.TIFF;
        }else if(fileType == FileType.TXT){
            return com.aspose.words.SaveFormat.TEXT;
        }
        return -1;
    }

    public static boolean authorizeLicense() {
        boolean result = false;
        try {
            InputStream is = com.aspose.cells.License.class.getResourceAsStream(cellsLicensePath);
            License asposeLicense = new License();
            asposeLicense.setLicense(is);
            is.close();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
