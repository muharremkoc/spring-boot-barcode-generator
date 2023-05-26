package com.generator.barcode.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.generator.barcode.model.User;
import com.google.zxing.*;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.oned.EAN13Writer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
@Slf4j
public class BarcodeServiceImpl implements BarcodeService{



    @Override
    public byte[] generateBarcode(MultipartFile excel) {
/*        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            hints.put(EncodeHintType.MARGIN, 1);

            BitMatrix bitMatrix = new MultiFormatWriter().encode(barcodeText, BarcodeFormat.CODE_128, 250, 90, hints);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

            return outputStream.toByteArray();
        } catch (Exception e) {
            // Handle exception appropriately
            e.printStackTrace();
            return null;
        }*/
        try {
            List<User> userList = readCustomerCompaniesFromExcelFile(excel);
/*            Hashtable<EncodeHintType,ErrorCorrectionLevel> hintMap = new Hashtable<>();
            hintMap.put(EncodeHintType.ERROR_CORRECTION,ErrorCorrectionLevel.H);*/
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.CHARACTER_SET, 12);

            Writer writer = new Code128Writer();
            BitMatrix bitMatrix = writer.encode(userList.get(0).toString(),BarcodeFormat.CODE_128,200,200,hints);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix,"png",outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String exportBarcodeFromExcelFile(MultipartFile excel) {
        List<User> userList = readCustomerCompaniesFromExcelFile(excel);

        try {

            userList.forEach(user -> {

                Map<EncodeHintType, Object> hints = new HashMap<>();
                hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
                hints.put(EncodeHintType.MARGIN, 1);

                BitMatrix bitMatrix = null;

                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    String jsonText = objectMapper.writeValueAsString(user);
                    bitMatrix = new MultiFormatWriter().encode(user.toString(), BarcodeFormat.PDF_417, 200, 100);
                } catch (WriterException e) {
                    throw new RuntimeException(e);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }

                try {
                    Path path = Paths.get("src/main/resources/barcodes/"+user.getId()+".png");
                    MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            return "Barcodes Create success";
        } catch (Exception e) {
            // Handle exception appropriately
            e.printStackTrace();
            return "Barcodes Create success";
        }
    }

    private List<User> readCustomerCompaniesFromExcelFile(MultipartFile file) {
        List<User> users = new ArrayList<>();
        DataFormatter dataFormatter = new DataFormatter();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            XSSFSheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                XSSFRow row = sheet.getRow(i);
                int id = Integer.parseInt(dataFormatter.formatCellValue(row.getCell(0)));
                String firstName = String.valueOf(row.getCell(1));
                String lastName = String.valueOf(row.getCell(2));
                User user = User.builder()
                        .id(id)
                        .firstName(firstName)
                        .lastName(lastName)
                        .build();

                users.add(user);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return users;
    }

}
