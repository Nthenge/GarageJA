package com.eclectics.Garage.controller;

import com.eclectics.Garage.service.CarOwnerService;
import com.eclectics.Garage.service.GarageService;
import com.eclectics.Garage.service.MechanicService;
import com.eclectics.Garage.service.OSSService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/garage/files")
public class OSSController {

    private final OSSService ossService;
    private final CarOwnerService carOwnerService;
    private final GarageService garageService;
    private final MechanicService mechanicService;

    public OSSController(OSSService ossService, CarOwnerService carOwnerService, GarageService garageService, MechanicService mechanicService) {
        this.ossService = ossService;
        this.carOwnerService = carOwnerService;
        this.garageService = garageService;
        this.mechanicService = mechanicService;
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

    @GetMapping("/carOwner/files/{uniqueId}")
    public ResponseEntity<String> getProfilePicture(@PathVariable Integer uniqueId) {

        Optional<String> url = carOwnerService.getProfilePictureUrlByUniqueId(uniqueId, 15);

        return url.<ResponseEntity<String>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/garage/files/{uniqueId}")
    public ResponseEntity<String> getGarageFiles(@PathVariable Long uniqueId) {

        Optional<String> url = garageService.getGarageUrlByUniqueId(uniqueId, 15);

        return url.<ResponseEntity<String>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/mechanic/files/{uniqueId}")
    public ResponseEntity<String> getMechanicFiles(@PathVariable Integer uniqueId) {

        Optional<String> url = mechanicService.getMechanicFilesUrlByNationalId(uniqueId, 15);

        return url.<ResponseEntity<String>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete")
    public String deleteFile(@RequestParam("fileName") String fileName) {
        ossService.deleteFile("uploads/" + fileName);
        return "File deleted successfully!";
    }
}
