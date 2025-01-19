package com.cat.coin.coincatmanager.framework;

import com.cat.coin.coincatmanager.domain.pojo.AjaxResult;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandler {
    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public AjaxResult exception(Exception e){
        return AjaxResult.error(e.getMessage());
    }
}
