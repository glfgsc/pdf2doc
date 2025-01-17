package com.cat.coin.coincatmanager.framework;


import com.cat.coin.coincatmanager.domain.pojo.Page;
import com.cat.coin.coincatmanager.utils.Resp;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

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
            return Resp.success("");
        }
        if (body instanceof Resp<?> || !selectedContentType.equals(MediaType.APPLICATION_JSON)) {
            return body;
        }else if(body instanceof Page<?>){
            return Resp.success(body);
        }
        return Resp.success(body);
    }
}
