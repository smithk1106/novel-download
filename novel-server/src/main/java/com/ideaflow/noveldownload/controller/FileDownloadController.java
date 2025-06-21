package com.ideaflow.noveldownload.controller;

import com.ideaflow.noveldownload.entity.NovelEntity;
import com.ideaflow.noveldownload.mapper.NovelMapper;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.Objects;

@RestController
public class FileDownloadController {

    @jakarta.annotation.Resource
    private NovelMapper novelMapper;


    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String id) throws IOException {
        NovelEntity novelEntity = novelMapper.selectById(id);
        if (Objects.isNull(novelEntity)|| !StringUtils.hasText(novelEntity.getDownloadUrl())) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // 设置文件路径
        String filePath =  System.getProperty("user.dir") + File.separator+novelEntity.getDownloadUrl();
        File file = new File(filePath);

        if (!file.exists()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        FileSystemResource resource = new FileSystemResource(file);
        String originalFilename = file.getName();
        try {
            originalFilename = URLEncoder.encode(originalFilename, "UTF-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            // 处理编码异常
            originalFilename = "download";
        }
        String mimeType = Files.probeContentType(file.toPath());
        if (mimeType == null) {
            mimeType = MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE; // 默认二进制流
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + originalFilename + "\"")
                .header(HttpHeaders.CONTENT_TYPE, mimeType)
                .body(resource);
    }

}
