package com.generator.barcode.controller;

import com.generator.barcode.service.BarcodeService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;

@RestController
@RequestMapping("/barcodes")
public class BarcodeController {


    private final BarcodeService barcodeService;

    public BarcodeController(BarcodeService barcodeService) {
        this.barcodeService = barcodeService;
    }

    @PostMapping(value = "/zxing",consumes = {"multipart/form-data"}, produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] generateBarcode(@RequestPart("file") MultipartFile excel) {
        return barcodeService.generateBarcode(excel);
    }


    @PostMapping(path = "/file/upload",consumes = {"multipart/form-data"})

    @ResponseStatus(HttpStatus.CREATED)
    public String createCustomerWithExcelFile(@RequestPart("file") MultipartFile excel) {
        return barcodeService.exportBarcodeFromExcelFile(excel);
    }

}
