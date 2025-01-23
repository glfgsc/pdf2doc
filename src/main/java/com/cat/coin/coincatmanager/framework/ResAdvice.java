package com.cat.coin.coincatmanager.framework;


import com.cat.coin.coincatmanager.domain.pojo.AjaxResult;
import com.cat.coin.coincatmanager.domain.pojo.PageResult;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.HashMap;

@ControllerAdvice
public class ResAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                   MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {
        if(body == null){
            return AjaxResult.success();
        }
        if(body instanceof AjaxResult){
            return body;
        }else if(body instanceof PageResult<?>){
            PageResult pageResult = (PageResult)body;
            AjaxResult ajaxResult = AjaxResult.success(pageResult.getList());
            ajaxResult.put("total", pageResult.getTotal());
            return ajaxResult;
        } else if (body instanceof Resource) {
            return body;
        }else{
            return AjaxResult.success(body);
        }
    }
}
