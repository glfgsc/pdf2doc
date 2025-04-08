package com.cat.coin.coincatmanager.utils;

import java.util.Random;

public class VerificationCodeUtils {
    /**
     * 生成随机验证码
     *
     * @param length 验证码长度
     * @return 验证码
     */
    public static String generateCode(int length) {
        StringBuilder s = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int n = random.nextInt(10);
            s.append(n);
        }
        return s.toString();
    }
}