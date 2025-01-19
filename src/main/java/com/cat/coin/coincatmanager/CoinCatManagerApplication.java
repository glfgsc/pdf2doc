package com.cat.coin.coincatmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class CoinCatManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoinCatManagerApplication.class, args);
    }

}
