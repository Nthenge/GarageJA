package com.eclectics.Garage.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URL;
import java.util.Date;

@Service
public class OSSService {

    private final OSS ossClient;

    @Value("${alibaba.cloud.oss.bucket-name}")
    private String bucketName;

    @Value("${alibaba.cloud.oss.endpoint}")
    private String endpoint;

    public OSSService(OSS ossClient) {
        this.ossClient = ossClient;
    }

    public String uploadFile(String objectName, InputStream inputStream) {
        ossClient.putObject(bucketName, objectName, inputStream);
        return "https://" + bucketName + "." + endpoint.replace("https://", "") + "/" + objectName;
    }

    public String generatePresignedUrl(String objectName, int expiryMinutes) {
        Date expiration = new Date(System.currentTimeMillis() + expiryMinutes * 60 * 1000);
        URL url = ossClient.generatePresignedUrl(bucketName, objectName, expiration);
        return url.toString();
    }

    public String generateUploadUrl(String objectName, int expiryMinutes) {
        Date expiration = new Date(System.currentTimeMillis() + expiryMinutes * 60 * 1000);
        GeneratePresignedUrlRequest request =
                new GeneratePresignedUrlRequest(bucketName, objectName, HttpMethod.PUT);
        request.setExpiration(expiration);
        request.setContentType("image/jpeg");

        URL url = ossClient.generatePresignedUrl(request);
        return url.toString();
    }


    public void deleteFile(String objectName) {
        ossClient.deleteObject(bucketName, objectName);
    }
}
