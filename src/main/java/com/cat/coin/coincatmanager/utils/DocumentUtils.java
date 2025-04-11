package com.cat.coin.coincatmanager.utils;

import com.aspose.pdf.SaveFormat;
import com.cat.coin.coincatmanager.domain.enums.FileType;

public class DocumentUtils {
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
        }else if(fileType == FileType.PDF){
            return SaveFormat.Pdf;
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
        }else if(fileType == FileType.DOC){
            return com.aspose.words.SaveFormat.DOC;
        }else if(fileType == FileType.DOCX){
            return com.aspose.words.SaveFormat.DOCX;
        }
        return -1;
    }

    public static int getAsposeExcelFormatType(FileType fileType){
        if(fileType == FileType.SVG){
            return com.aspose.cells.SaveFormat.SVG;
        }else if(fileType == FileType.CSV){
            return com.aspose.cells.SaveFormat.CSV;
        }else if(fileType == FileType.XLSX){
            return com.aspose.cells.SaveFormat.XLSX;
        }else if(fileType == FileType.XLS){
            return com.aspose.cells.SaveFormat.EXCEL_97_TO_2003;
        }else if(fileType == FileType.XPS){
            return com.aspose.cells.SaveFormat.XPS;
        }
        return -1;
    }
}
