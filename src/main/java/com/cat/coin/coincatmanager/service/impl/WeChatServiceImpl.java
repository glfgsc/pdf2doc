package com.cat.coin.coincatmanager.service.impl;

import com.alibaba.fastjson.JSON;
import com.cat.coin.coincatmanager.controller.vo.WeChatQrCodeVo;
import com.cat.coin.coincatmanager.service.WeChatService;
import com.cat.coin.coincatmanager.utils.HttpClientUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service("wechatService")
public class WeChatServiceImpl implements WeChatService {
    @Value("${wx.mp.app-id}")
    private String APP_ID;

    @Value("${wx.mp.secret}")
    private String APP_SECRET;
    private String wxuuid;

    @Override
    public WeChatQrCodeVo getQrCode() {
        String QRUrl = null;
        String ticketRes = null;
        WeChatQrCodeVo weChatQrCodeVo = new WeChatQrCodeVo();
        try {
            // 第一步：发送请求获取access_token
            String getAccessTokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential" +
                    "&appid=" + APP_ID +
                    "&secret=" + APP_SECRET;
            String accessTokenRes = HttpClientUtils.doGet(getAccessTokenUrl);
            String accessToken = (String) JSON.parseObject(accessTokenRes).get("access_token"); // 获取到access_token
            System.out.println(accessToken);
            //生成uuid
            wxuuid = UUID.randomUUID().toString();
            // 第二步：通过access_token和一些参数发送post请求获取二维码Ticket
            String getTicketUrl = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=" + accessToken;
            // 封装参数
            Map<String, Object> ticketInfo = new HashMap<>();
            ticketInfo.put("expire_seconds", 604800); // 二维码超时时间
            ticketInfo.put("action_name", "QR_STR_SCENE");
            // 放入uuid
            ticketInfo.put("action_info", new HashMap<String, HashMap>() {{
                        put("scene", new HashMap<String, String>() {{
                                    put("scene_str", wxuuid);
                                }}
                        );
                    }}
            );
            String ticketJsonInfo = JSON.toJSON(ticketInfo).toString();
            ticketRes = HttpClientUtils.doPostJson(getTicketUrl, ticketJsonInfo);
            String ticket = (String) JSON.parseObject(ticketRes).get("ticket");

            // 第三步：通过ticket获取二维码url
            String encodeTicket = URLEncoder.encode(ticket, "utf-8"); // 编码ticket
            String getQRUrl = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + encodeTicket;
            QRUrl = getQRUrl; // 二维码url
            weChatQrCodeVo.setUuid(wxuuid);
            weChatQrCodeVo.setQrUrl(QRUrl);;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return weChatQrCodeVo;
    }
}
