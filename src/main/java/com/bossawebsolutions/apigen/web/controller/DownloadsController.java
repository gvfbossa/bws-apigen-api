package com.bossawebsolutions.apigen.web.controller;

import java.io.IOException;
import java.util.Set;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/downloads")
public class DownloadsController {

    private static final Set<String> ALLOWED_FILES = Set.of(
            "bws-apigen-cli-windows-install.exe",
            "bws-apigen-cli-linux-install.deb"
    );

    @GetMapping("/{fileName}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable String fileName
    ) throws IOException {

        if (!ALLOWED_FILES.contains(fileName)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new ClassPathResource(
                "assets/downloads/" + fileName
        );

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();

        headers.add(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + fileName + "\""
        );

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

}