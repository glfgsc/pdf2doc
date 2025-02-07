package com.cat.coin.coincatmanager.mapper;

import com.cat.coin.coincatmanager.controller.vo.PdfHistoryPageVo;
import com.cat.coin.coincatmanager.domain.pojo.Pdf;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PdfConvertHistoryMapper {
    void insert(Pdf pdf);
    List<Pdf> selectAll();

    List<Pdf> select(int creator);

    Pdf selectById(String id);

    void delete(String id);

    Pdf selectByOldPath(String oldPath);

    void updateById(Pdf pdf);
}
