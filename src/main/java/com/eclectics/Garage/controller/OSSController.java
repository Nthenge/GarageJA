package com.eclectics.Garage.controller;

import com.eclectics.Garage.service.OSSService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/garage/files")
public class OSSController {

    private final OSSService ossService;

    public OSSController(OSSService ossService) {
        this.ossService = ossService;
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        String objectName = "uploads/" + file.getOriginalFilename(); // research on weather to generate unique names
        return ossService.uploadFile(objectName, file.getInputStream());
    }

    // this is for downloading image or viewing
    @GetMapping("/url")
    public String getPresignedDownloadUrl(@RequestParam("fileName") String fileName) {
        return ossService.generatePresignedUrl("uploads/" + fileName, 10);
    }

    // this is for frontend to upload directly to alibaba
    @GetMapping("/upload-url")
    public String getPresignedUploadUrl(@RequestParam("fileName") String fileName) {
        return ossService.generateUploadUrl("uploads/" + fileName, 10);
    }

    @DeleteMapping("/delete")
    public String deleteFile(@RequestParam("fileName") String fileName) {
        ossService.deleteFile("uploads/" + fileName);
        return "File deleted successfully!";
    }
}
