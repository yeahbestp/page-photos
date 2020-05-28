package com.best.pagephoto.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.function.Supplier;

import static com.assertthat.selenium_shutterbug.core.Shutterbug.shootPage;
import static com.assertthat.selenium_shutterbug.utils.web.ScrollStrategy.WHOLE_PAGE;

@Service
@Slf4j
@RequiredArgsConstructor
@RequestScope
public class PhotoService {
    private final String path;
    private final Supplier<WebDriver> webDriverSupplier;
    private final RegexPage regexPage;
    private WebDriver webDriver;
    @Getter
    private Path storagePath;

    @PostConstruct
    public void initWebDriver() {
        var individualDirectory = String.valueOf(Instant.now().toEpochMilli());
        this.webDriver = webDriverSupplier.get();
        this.storagePath = createIndividualPath(individualDirectory);
    }

    public void createScreenshot(String page) {
        var fileName = regexPage.getPageName(page)
                .orElseThrow(IllegalArgumentException::new);
        log.info("going to {}", page);
        webDriver.get(page);
        log.debug("taking photo for {}", fileName);
        shootPage(webDriver, WHOLE_PAGE, 500, true)
                .withName(fileName)
                .save(storagePath.toString());
    }

    @SneakyThrows(IOException.class)
    private Path createIndividualPath(String individualDirectory) {
        var newDirectory = Path.of(path + individualDirectory);
        Files.createDirectories(newDirectory);
        return newDirectory;
    }

    @PreDestroy
    void quitDriver() {
        webDriver.close();
    }

}
