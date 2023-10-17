package com.example.demo.service;

import com.example.demo.exception.CustomException;
import com.example.demo.model.ProductDto;
import com.example.demo.util.CustomImportMappingStrategy;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import static com.example.demo.constants.ProductConstants.CSV_CONTENT_TYPE;

@Service
public class ProductImportService {
    private final Logger logger = LoggerFactory.getLogger(ProductImportService.class);
    private Path destinationPath;
    @Value("${file.upload-dir}")
    @Setter
    private String uploadDirectory;

    private void importFile(MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.isEmpty())
            throw new CustomException("File not found", HttpStatus.NOT_FOUND);

        if (!Objects.equals(multipartFile.getContentType(), CSV_CONTENT_TYPE))
            throw new CustomException("Wrong file format content type", HttpStatus.BAD_REQUEST);

        if (!StringUtils.endsWithIgnoreCase(multipartFile.getOriginalFilename(), ".csv"))
            throw new CustomException("Wrong file format", HttpStatus.BAD_REQUEST);

        String originalFilename = multipartFile.getOriginalFilename();

        try {
            destinationPath = Paths.get(uploadDirectory, originalFilename);
            logger.info("File name {}", originalFilename);
            multipartFile.transferTo(destinationPath);
            logger.info("File transferred to location {}", destinationPath);
        } catch (IOException ex) {
            throw new CustomException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public List<ProductDto> readCsv(MultipartFile multipartFile) {
        importFile(multipartFile);

        CustomImportMappingStrategy<ProductDto> mappingStrategy = new CustomImportMappingStrategy<>();
        mappingStrategy.setType(ProductDto.class);
        List<ProductDto> productDtoList;

        try {
            productDtoList = new CsvToBeanBuilder<ProductDto>(new FileReader(destinationPath.toFile()))
                    .withType(ProductDto.class)
                    .withQuoteChar('"')
                    .withMappingStrategy(mappingStrategy)
                    .build()
                    .parse();
        } catch (Exception e) {
            throw new CustomException(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
        }

        File file = new File(destinationPath.toUri());
        file.deleteOnExit();
        logger.info("File deleted");

        return productDtoList;
    }
}
