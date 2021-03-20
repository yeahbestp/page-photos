package com.best.pagephoto.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ArchiveService {

    public void archive(Path path) {
        var filesToArchive = getAllFiles(path);
        var zippedFile = new File(path.toString() + ".zip");
        try(var zipOutputStream = new ZipArchiveOutputStream(zippedFile)) {
            for (var fileToArchive : filesToArchive) {
                var entry = zipOutputStream.createArchiveEntry(fileToArchive, fileToArchive.getName());
                zipOutputStream.putArchiveEntry(entry);
                if (fileToArchive.isFile()) {
                    try (var inputStream = Files.newInputStream(fileToArchive.toPath())) {
                        IOUtils.copy(inputStream, zipOutputStream);
                    }
                }
                zipOutputStream.closeArchiveEntry();
            }
        }catch (IOException e) {
            log.error("Error while making zip file");
            throw new IllegalArgumentException(e);
        }
    }

    private List<File> getAllFiles(Path path) {
        Optional<List<File>> files;
        try(var x = Files.walk(path)) {
            var listOfFiles = x.map(Path::toFile)
                    .filter(file -> !file.isDirectory())
                    .collect(Collectors.toList());
            files = Optional.of(listOfFiles);
        }catch (Exception e){
            log.info("Exception while checking saved files:", e);
            throw new IllegalArgumentException(e);
        }
        return files.orElseThrow(IllegalArgumentException::new);
    }

}
