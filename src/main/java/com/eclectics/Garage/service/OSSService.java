package com.eclectics.Garage.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URL;
import java.util.*;

@Service
public class OSSService {

    private final OSS ossClient;

    @Value("${alibaba.cloud.oss.bucket-name}")
    private String bucketName;

    @Value("${alibaba.cloud.oss.endpoint}")
    private String endpoint;

    private final Map<String, String> uploadCache = new LinkedHashMap<>(50, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
            return size() > 50; // Keep only the 50 most recent uploads
        }
    };

    private final Map<String, String> presignedUrlCache = new HashMap<>();
    private final Set<String> deletedFiles = new HashSet<>();
    private final Queue<String> uploadHistory = new LinkedList<>();

    public OSSService(OSS ossClient) {
        this.ossClient = ossClient;
    }

    public String getBucketName() { return bucketName; }

    public String getEndpoint() { return endpoint; }

    public String uploadFile(String objectName, InputStream inputStream) {
        ossClient.putObject(bucketName, objectName, inputStream);
        String fileUrl = "https://" + bucketName + "." + endpoint.replace("https://", "") + "/" + objectName;

        uploadCache.put(objectName, fileUrl);
        uploadHistory.offer(objectName);
        deletedFiles.remove(objectName);

        return fileUrl;
    }

    public String generatePresignedUrl(String objectName, int expiryMinutes) {
        // Check cache first
        if (presignedUrlCache.containsKey(objectName)) {
            return presignedUrlCache.get(objectName);
        }

        Date expiration = new Date(System.currentTimeMillis() + expiryMinutes * 60 * 1000);
        URL url = ossClient.generatePresignedUrl(bucketName, objectName, expiration);

        presignedUrlCache.put(objectName, url.toString());
        return url.toString();
    }

    public String generateUploadUrl(String objectName, int expiryMinutes) {
        Date expiration = new Date(System.currentTimeMillis() + expiryMinutes * 60 * 1000);
        GeneratePresignedUrlRequest request =
                new GeneratePresignedUrlRequest(bucketName, objectName, HttpMethod.PUT);
        request.setExpiration(expiration);
        request.setContentType("image/jpeg");

        URL url = ossClient.generatePresignedUrl(request);

        presignedUrlCache.put(objectName + "_upload", url.toString());
        return url.toString();
    }

    public void deleteFile(String objectName) {
        ossClient.deleteObject(bucketName, objectName);
        deletedFiles.add(objectName);
        uploadCache.remove(objectName);
        presignedUrlCache.remove(objectName);
    }

    public List<String> getRecentlyUploadedFiles() {
        return new ArrayList<>(uploadCache.keySet());
    }

    public List<String> getUploadHistory() {
        return new ArrayList<>(uploadHistory);
    }

    public boolean isFileDeleted(String objectName) {
        return deletedFiles.contains(objectName);
    }

    public int getUploadCacheSize() {
        return uploadCache.size();
    }
}
