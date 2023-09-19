package com.example.mall.filecenter.controller;

import com.example.mall.common.model.result.Result;
import com.example.mall.filecenter.service.UploadFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/file")
public class MinioController {
    private final UploadFile uploadFile;

    @Autowired
    public MinioController(UploadFile uploadFile) {
        this.uploadFile = uploadFile;
    }

    @PostMapping("/upload/simpleFile")
    public Result uploadSimpleFile(@RequestParam("file") MultipartFile file) throws IOException {
        return uploadFile.uploadSimpleFile(file);
    }
    @PostMapping("/upload/multiSimpleFile")
    public Result uploadMultiSimpleFile(@RequestParam("file") MultipartFile[] files) throws IOException, InterruptedException {
        if (files== null || files.length <= 0){
            return Result.ok();
        }
        return uploadFile.uploadMultiSimpleFile(files);
    }
}
