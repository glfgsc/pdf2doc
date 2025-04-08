package com.cat.coin.coincatmanager.controller;


import com.cat.coin.coincatmanager.controller.vo.WeChatQrCodeVo;
import com.cat.coin.coincatmanager.service.WeChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.script.DigestUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

@RestController
@Validated
@RequestMapping("/wechat")
public class WeChatController {

    @Autowired
    private WeChatService weChatService;

    @GetMapping("/checkSignature")
    public void validate( @RequestParam("signature") String signature,
                          @RequestParam("timestamp") String timestamp,
                          @RequestParam("nonce") String nonce,
                          @RequestParam("echostr") String echostr,HttpServletResponse response) {
        //换成自己的token
        String token = "glfgsc";
        // 将 token、timestamp、nonce 三个参数进行字典序排序
        String[] arr = new String[]{token, timestamp, nonce};
        Arrays.sort(arr);
        // 将三个参数字符串拼接成一个字符串进行 sha1 加密
        StringBuilder sb = new StringBuilder();
        for (String str : arr) {
            sb.append(str);
        }
        String encrypted = DigestUtils.sha1DigestAsHex(sb.toString());

        // 将加密后的字符串与 signature 进行比较
        if (encrypted.equals(signature)) {
            // 如果一致，返回 echostr 参数的值
            renderToView(echostr, response);
        } else {
            // 如果不一致，返回 "Invalid request" 字符串
            renderToView("Invalid request", response);
        }
    }

    @GetMapping("/qrcode")
    public WeChatQrCodeVo getQrCode(){
        return weChatService.getQrCode();
    }

    public static void renderToView(String text, HttpServletResponse response) {

        try {
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().write(text);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
