package com.cat.coin.coincatmanager.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cat.coin.coincatmanager.controller.vo.PdfConvertVo;
import com.cat.coin.coincatmanager.controller.vo.PdfHistoryPageVo;
import com.cat.coin.coincatmanager.domain.pojo.*;
import com.cat.coin.coincatmanager.mapper.UserMapper;
import com.cat.coin.coincatmanager.service.PdfConvertService;
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
@RequestMapping("/pdf")
@EnableAsync
public class PdfController {
    private static final String UPLOAD_DIR = "uploads/";

    @Autowired
    private PdfConvertService pdfConvertService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisCacheUtils redisCacheUtils;

    @PostMapping("/convert")
    public Pdf pdfConvert(PdfConvertVo pdfConvertVo) throws Exception {
        Pdf pdf = pdfConvertService.pdfConvert(pdfConvertVo);
        pdfConvertService.pdfConvertSchedule(pdfConvertVo,pdf,pdfConvertVo.getFile().getInputStream());
        return pdf;
    }
    @GetMapping("/history")
    public PageResult<Pdf> getHistory(PdfHistoryPageVo pdfHistoryPageVo){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JSONObject user = JSONObject.parseObject(JSON.toJSONString(authentication.getPrincipal()));
        Integer userId = user.getJSONObject("user").getInteger("id");
        pdfHistoryPageVo.setCreator(userId);
        List<Pdf> historyByPage = pdfConvertService.getHistoryByPage(pdfHistoryPageVo);
        return new PageResult<>(historyByPage,new PageInfo(historyByPage).getTotal());
    }

    @GetMapping("/downloadOldFile")
    public ResponseEntity<Resource> downloadOldFile(@RequestParam("id") String id,@RequestParam("token") String token, HttpServletResponse response) throws IOException {
        return pdfConvertService.downloadOldFile(id,response);
    }

    @GetMapping("/downloadNewFile")
    public ResponseEntity<Resource> downloadNewFile(@RequestParam("id") String id,@RequestParam("token")String token,HttpServletResponse response) throws IOException {
        return pdfConvertService.downloadNewFile(id,response);
    }

    @GetMapping("/downloadOldFileUrl")
    public AjaxResult downloadOldFileUrl(String id,String token){
        return AjaxResult.success("http://www.icoincat.cn/api/pdf/downloadOldFile?id=" + id + "&token=" + token);
    }

    @GetMapping("/downloadNewFileUrl")
    public AjaxResult downloadNewFileUrl(String id,String token){

        return AjaxResult.success("http://www.icoincat.cn/api/pdf/downloadNewFile?id=" + id + "&token=" + token);
    }

    @GetMapping("/delete")
    public void delete(String id){
        pdfConvertService.delete(id);
    }
}
