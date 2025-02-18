package com.cat.coin.coincatmanager.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cat.coin.coincatmanager.controller.vo.DocumentConvertVo;
import com.cat.coin.coincatmanager.controller.vo.DocumentHistoryPageVo;
import com.cat.coin.coincatmanager.domain.pojo.*;
import com.cat.coin.coincatmanager.mapper.UserMapper;
import com.cat.coin.coincatmanager.service.DocumentConvertService;
import com.cat.coin.coincatmanager.utils.RedisCacheUtils;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@Validated
@RequestMapping("/document")
@EnableAsync
public class DocumentController {
    private static final String UPLOAD_DIR = "uploads/";

    @Autowired
    private DocumentConvertService documentConvertService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisCacheUtils redisCacheUtils;

    @PostMapping("/convert")
    public Document documentConvert(DocumentConvertVo documentConvertVo) throws Exception {
        Document document = documentConvertService.documentConvert(documentConvertVo);
        documentConvertService.documentConvertSchedule(documentConvertVo,document,documentConvertVo.getFile().getInputStream());
        return document;
    }
    @GetMapping("/history")
    public PageResult<Document> getHistory(DocumentHistoryPageVo pdfHistoryPageVo){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JSONObject user = JSONObject.parseObject(JSON.toJSONString(authentication.getPrincipal()));
        Integer userId = user.getJSONObject("user").getInteger("id");
        pdfHistoryPageVo.setCreator(userId);
        List<Document> historyByPage = documentConvertService.getHistoryByPage(pdfHistoryPageVo);
        return new PageResult<>(historyByPage,new PageInfo(historyByPage).getTotal());
    }

    @GetMapping("/downloadOldFile")
    public ResponseEntity<Resource> downloadOldFile(@RequestParam("id") String id,@RequestParam("token") String token, HttpServletResponse response) throws IOException {
        return documentConvertService.downloadOldFile(id,response);
    }

    @GetMapping("/downloadNewFile")
    public ResponseEntity<Resource> downloadNewFile(@RequestParam("id") String id,@RequestParam("token")String token,HttpServletResponse response) throws IOException {
        return documentConvertService.downloadNewFile(id,response);
    }

    @GetMapping("/downloadOldFileUrl")
    public AjaxResult downloadOldFileUrl(String id,String token){
        return AjaxResult.success("https://www.icoincat.cn/api/document/downloadOldFile?id=" + id + "&token=" + token);
    }

    @GetMapping("/downloadNewFileUrl")
    public AjaxResult downloadNewFileUrl(String id,String token){

        return AjaxResult.success("https://www.icoincat.cn/api/document/downloadNewFile?id=" + id + "&token=" + token);
    }

    @GetMapping("/delete")
    public void delete(String id){
        documentConvertService.delete(id);
    }
}
