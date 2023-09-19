package com.example.mall.filecenter.service.impl;

import com.example.mall.common.model.exception.MinIOException;
import com.example.mall.common.model.result.Result;
import com.example.mall.filecenter.config.MinioProperties;
import com.example.mall.filecenter.service.UploadFile;
import com.example.mall.filecenter.util.UploadUtils;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
@Slf4j
@Service
public class UploadFileImpl implements UploadFile {
    private final MinioClient minioClient;
    private final MinioProperties properties;
    private final ExecutorService threadPool = Executors.newFixedThreadPool(3);


    @Autowired
    public UploadFileImpl(MinioClient minioClient, MinioProperties properties) {
        this.minioClient = minioClient;
        this.properties = properties;
    }

    @Override
    public Result uploadSimpleFile(MultipartFile file) throws IOException {
        UploadUtils uploadUtils = new UploadUtils(minioClient, properties);
        String url = uploadUtils.uploadSimpleFiles(
                file,
                properties.getSimpleFileBucket()
        );
        return Result.ok(url);
    }

    @Override
    public Result uploadMultiSimpleFile(MultipartFile[] files) throws InterruptedException {
        List<String> url = new ArrayList<>();
        CountDownLatch countDownLatch = new CountDownLatch(files.length);
        for (MultipartFile file : files) {
            threadPool.submit(() -> {
                Result result = null;
                try {
                    result = this.uploadSimpleFile(file);
                    url.add(result.getData().toString());
                    countDownLatch.countDown();

                } catch (Exception e) {
                    countDownLatch.countDown();
                    throw new MinIOException("上传多个文件处发生错误", e);
                }
            });
        }
        countDownLatch.await();
        return Result.ok(url);
    }

}
