package com.best.pagephoto.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Duration;
import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class Cleaner {
    private final String path;
    private final int timeInMinutes;

    @Scheduled(cron = "0 0/30 * * * ?")
    public void clean() {
        try (var files = Files.walk(Path.of(path))) {
            files.skip(1)
                    .filter(s -> {
                        BasicFileAttributes attr = null;
                        try {
                            attr = Files.readAttributes(s, BasicFileAttributes.class);
                        } catch (IOException e) {
                            log.error("Issue with filtering files for removal", e);
                        }
                        var fileTime = attr.creationTime();
                        var x = Duration.between(fileTime.toInstant(), Instant.now()).toMinutes();
                        return x >= timeInMinutes;
                    }).forEach(s -> {
                try {
                    Files.delete(s);
                    log.info("file has been removed: {}", s);
                } catch (IOException e) {
                    log.error("Issue with filtering files for removal", e);
                }
            });

        } catch (IOException e) {
            log.error("Exception while trying to remove old files", e);
        }

    }
}
