package com.cat.coin.coincatmanager.mapper;

import com.cat.coin.coincatmanager.controller.vo.DocumentHistoryPageVo;
import com.cat.coin.coincatmanager.domain.pojo.Document;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DocumentConvertHistoryMapper {
    void insert(Document pdf);
    List<Document> selectAll();

    List<Document> select(DocumentHistoryPageVo documentHistoryPageVo);

    Document selectById(String id);

    void delete(String id);

    Document selectByOldPath(String oldPath);

    void updateById(Document pdf);
}
