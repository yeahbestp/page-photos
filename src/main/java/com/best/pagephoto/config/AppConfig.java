package com.best.pagephoto.config;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;

import java.util.function.Supplier;

@Slf4j
@Configuration
public class AppConfig {

    @Value("${chrome.path}")
    private String chromeProperty;

    @Value("${photo.path}")
    private String path;

    @Bean
    public ChromeOptions chromeOptions(){
        log.debug("loading chrome driver options");
        System.setProperty("webdriver.chrome.driver", chromeProperty);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("start-maximized");
        return options;
    }

    @Bean
    @RequestScope
    public Supplier<WebDriver> webDriverSupplier() {
        return () -> new ChromeDriver(chromeOptions());
    }

    @Bean
    public String photoPath() {
        return path;
    }

}
