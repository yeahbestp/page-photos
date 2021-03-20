package com.best.pagephoto.controller;

import com.best.pagephoto.model.PageModel;
import com.best.pagephoto.service.ArchiveService;
import com.best.pagephoto.service.PhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Controller
@RequestMapping(value = "/")
public class PhotoPage {
    private static final Pattern PATTERN = Pattern.compile(";");

    private final PhotoService photoService;
    private final ArchiveService archiveService;

    @GetMapping
    public String getMainPage(Model model) {
        model.addAttribute("pageModel", new PageModel());
        return "index";
    }

    @PostMapping
    public ResponseEntity<Resource> getPhotos(@ModelAttribute PageModel page) {
        ByteArrayResource resource;
        var pages = getPages(page.getPage());
        var path = Path.of(photoService.getStoragePath().toString() + ".zip");
        pages.forEach(photoService::createScreenshot);
        archiveService.archive(photoService.getStoragePath());

        try {
            resource = new ByteArrayResource(Files.readAllBytes(path));
        } catch (IOException e) {
            throw new IllegalArgumentException("Reading path bytes issue");
        }
        HttpHeaders header = setHeader(path.getFileName());

        return ResponseEntity.ok()
                .headers(header)
                .contentLength(path.toFile().length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }

    private HttpHeaders setHeader(Path path) {
        var header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=" + path);
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");
        return header;
    }

    private List<String> getPages(String content) {
        Predicate<String> isEmpty = String::isEmpty;
        return PATTERN
                .splitAsStream(content)
                .map(String::trim)
                .filter(isEmpty.negate())
                .collect(Collectors.toList());
    }
}
