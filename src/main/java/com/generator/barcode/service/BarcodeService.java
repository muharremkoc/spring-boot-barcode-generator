package com.generator.barcode.service;

import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;

public interface BarcodeService  {

    byte[] generateBarcode(MultipartFile excel);

    String exportBarcodeFromExcelFile(MultipartFile excel);


}
