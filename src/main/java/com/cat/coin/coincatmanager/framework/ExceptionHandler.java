package com.cat.coin.coincatmanager.framework;

import com.cat.coin.coincatmanager.utils.Resp;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandler {
    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public Resp exception(Exception e){
        return Resp.error(e.getMessage());
    }
}
