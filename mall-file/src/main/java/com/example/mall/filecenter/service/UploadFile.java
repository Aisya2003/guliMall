package com.example.mall.filecenter.service;

import com.example.mall.common.model.result.Result;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UploadFile {
    /**
     * 上传简单文件
     * @param file
     * @return
     */
    Result uploadSimpleFile(MultipartFile file) throws IOException;

    /**
     * 上传多个简单文件
     * @param files
     * @return
     */
    Result uploadMultiSimpleFile(MultipartFile[] files) throws IOException, InterruptedException;
}
