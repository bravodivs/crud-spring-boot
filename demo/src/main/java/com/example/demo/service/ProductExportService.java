package com.example.demo.service;

import com.example.demo.exception.CustomException;
import com.example.demo.model.ProductDto;
import com.example.demo.util.CsvExporterUtil;
import com.example.demo.util.JsonExporterUtil;
import com.example.demo.util.PdfExporter;
import com.example.demo.util.XmlExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.demo.constants.ProductConstants.*;

@Service
public class ProductExportService {

    private final Logger logger = LoggerFactory.getLogger(ProductExportService.class);
    private File file;
    private InputStreamResource inputStreamResource;
    @Autowired
    private XmlExporter xmlExporter;
    @Value("${file.defaultExportName}")
    private String defaultFileNameTemplate;
    @Value("${file.dateFormat}")
    private String dateFormat;
    private String fileExtension;

    /*
    return the reponse entity which will have the file and inputstreamresource.
    delete the file here after sending response.
    based on type, call method, which will call apt util method for exporting.
     */
    public ResponseEntity<Object> exportHandler(List<ProductDto> productDtoList, String fileType) {
        ResponseEntity<Object> responseEntity;
        switch (fileType.toLowerCase()) {
            case CSV -> {
                responseEntity = exportCsv(prepareCsvFormatMatrix(productDtoList));
                file.deleteOnExit();
            }
            case JSON -> {
                responseEntity = exportJson(productDtoList);
                file.deleteOnExit();
            }
            case PDF -> {
                responseEntity = exportPdf(productDtoList);
                file.deleteOnExit();
            }
            case XML -> {
                responseEntity = exportXml(productDtoList);
                file.deleteOnExit();
            }
            default -> {
                logger.error("Invalid file type found in product export service");
                throw new CustomException(String.format("File type %s not valid", fileType), HttpStatus.BAD_REQUEST);
            }
        }
        logger.info("Set response entity returned");
        return responseEntity;
    }

    public ResponseEntity<Object> exportCsv(List<String[]> dataArray) {
        this.fileExtension=".csv";
        file = CsvExporterUtil.export(dataArray, getExportFileName());
        HttpHeaders httpHeaders = prepareHeaderResponse(CSV);
        return new ResponseEntity<>(inputStreamResource, httpHeaders, HttpStatus.OK);
    }

    public ResponseEntity<Object> exportJson(List<ProductDto> productDtoList) {
        this.fileExtension=".json";
        file = JsonExporterUtil.export(productDtoList, getExportFileName());
        HttpHeaders httpHeaders = prepareHeaderResponse(JSON);
        return new ResponseEntity<>(inputStreamResource, httpHeaders, HttpStatus.OK);
    }

    public ResponseEntity<Object> exportXml(List<ProductDto> productDtoList) {
        file = xmlExporter.export(productDtoList);
        HttpHeaders httpHeaders = prepareHeaderResponse(XML);
        return new ResponseEntity<>(inputStreamResource, httpHeaders, HttpStatus.OK);
    }

    public ResponseEntity<Object> exportPdf(List<ProductDto> productDtoList) {
        file = new PdfExporter().export(productDtoList);
        HttpHeaders httpHeaders = prepareHeaderResponse(PDF);
        return new ResponseEntity<>(inputStreamResource, httpHeaders, HttpStatus.OK);
    }

    private List<String[]> prepareCsvFormatMatrix(List<ProductDto> dataList) {
        Field[] fields = ProductDto.class.getDeclaredFields();
        List<String> csvHeaders = Arrays.stream(fields)
                .map(Field::getName)
                .map(String::toUpperCase)
                .toList();

        List<String[]> matrix = new ArrayList<>();
        matrix.add(csvHeaders.toArray(new String[0]));

        dataList.forEach(productDto -> {
            String[] data = {
                    productDto.getId(),
                    productDto.getName(),
                    productDto.getDescription(),
                    String.valueOf(productDto.getQuantity()),
                    String.valueOf(productDto.getPrice()),
                    String.join(", ", productDto.getImages()),
                    String.valueOf(productDto.getCreatedAt()),
                    String.valueOf(productDto.getModifiedAt())
            };
            matrix.add(data);
        });
        return matrix;
    }

    private HttpHeaders prepareHeaderResponse(String fileType) {
        HttpHeaders httpHeaders = new HttpHeaders();
        try {
            inputStreamResource = new InputStreamResource(new FileInputStream(file));
            httpHeaders.add(CONTENT_DISPOSITION, String.format("attachment; filename=\"%s\"", file.getName()));
            switch (fileType.toLowerCase()) {
                case CSV -> httpHeaders.setContentType(MediaType.parseMediaType("text/csv"));
                case JSON -> httpHeaders.setContentType(MediaType.parseMediaType("application/json"));
                case XML -> httpHeaders.setContentType(MediaType.parseMediaType("application/xml"));
                case PDF -> httpHeaders.setContentType(MediaType.parseMediaType("application/pdf"));
                default -> logger.error("No file type provided");
            }
            httpHeaders.setContentType(MediaType.parseMediaType("text/csv"));
        } catch (Exception e) {
            throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        logger.info("Set header {}",httpHeaders);
        return httpHeaders;
    }

    public String getExportFileName() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern(dateFormat));
        return defaultFileNameTemplate + timestamp + fileExtension;
    }
}