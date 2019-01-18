package com.mizuho.marketdata;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class MarketdataHubLauncher extends SpringBootServletInitializer {

    public static void main(String[] args) {
        new MarketdataHubLauncher()
                .configure(new SpringApplicationBuilder(MarketdataHubLauncher.class))
                .run(args);
    }

}
