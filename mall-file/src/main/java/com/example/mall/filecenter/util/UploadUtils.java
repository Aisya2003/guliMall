package com.example.mall.filecenter.util;

import com.example.mall.common.model.exception.MinIOException;
import com.example.mall.filecenter.config.MinioProperties;
import io.minio.MinioClient;

import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.StringJoiner;
@Slf4j
public class UploadUtils {
    private final MinioClient minioClient;
    private final MinioProperties properties;

    public UploadUtils(MinioClient minioClient, MinioProperties properties) {
        this.minioClient = minioClient;
        this.properties = properties;
    }

    public String uploadSimpleFiles(MultipartFile file, String bucketName) throws IOException {
        PutObjectArgs args = null;
        String objectName = null;
        //确保文件id唯一
        String fileName = file.getOriginalFilename();
        String extension = "";
        try {
            if (fileName != null) {
                extension = fileName.substring(fileName.lastIndexOf("."));
            }
            fileName = DigestUtils.md5DigestAsHex(file.getInputStream()) + extension;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String contentType = file.getContentType();
        //根据contentType放入对应文件夹保存
        objectName = new StringJoiner("/")
                .add(contentType)
                .add(fileName)
                .toString();
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(file.getBytes())) {
            args = PutObjectArgs.builder()
                    .contentType(contentType)
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(inputStream, file.getSize(), -1)
                    .build();
        } catch (IOException e) {
            throw new IOException("将文件转为字节数组异常");
        }


        try {
            log.info("开始上传文件[{}]",objectName);
            minioClient.putObject(args);
            log.info("文件[{}]上传成功",objectName);
        } catch (Exception e) {
            log.info("文件[{}]上传失败",objectName);
            MinIOException.error("上传文件服务器异常");
        }

        return new StringJoiner("/")
                .add(properties.getEndPoint())
                .add(bucketName).add(objectName)
                .toString();
    }

}
