package com.best.pagephoto.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ArchiveService {

    public void archive(Path path) {
        Collection<File> filesToArchive = getAllFiles(path);
        var zippedFile = new File(path.toString() + ".zip");
        try(ArchiveOutputStream o = new ZipArchiveOutputStream(zippedFile)) {
            for (File f : filesToArchive) {
                ArchiveEntry entry = o.createArchiveEntry(f, f.getName());
                o.putArchiveEntry(entry);
                if (f.isFile()) {
                    try (InputStream i = Files.newInputStream(f.toPath())) {
                        IOUtils.copy(i, o);
                    }
                }
                o.closeArchiveEntry();
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
