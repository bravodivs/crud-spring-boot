package com.example.productservice.service;

import com.example.productservice.exception.CustomException;
import com.example.productservice.model.ProductDto;
import com.example.productservice.util.CustomImportMappingStrategy;
import com.opencsv.bean.CsvToBeanBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductImportServiceTest {
    @Mock
    private CustomImportMappingStrategy<ProductDto> mappingStrategy;
    @Mock
    private CsvToBeanBuilder<ProductDto> csvToBeanBuilder;
    @Mock
    private MultipartFile multipartFile;
    @InjectMocks
    private ProductImportService productImportService;
    @BeforeEach
    void setUp() {
        productImportService.setUploadDirectory("C:\\users\\DevanshuVerma\\Documents\\BE\\first demo\\demo\\uploads");
    }

    @Test
    void testReadCsvWithValidFile(){
        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.csv",
                "text/csv", "some,csv,data".getBytes());

        List<ProductDto> productDtoList = productImportService.readCsv(multipartFile);

        assertNotNull(productDtoList);
    }

    @Test
    void testReadCsvWithEmptyFile() {
        when(multipartFile.isEmpty()).thenReturn(true);

        assertThrows(CustomException.class, () -> productImportService.readCsv(multipartFile));
    }

    @Test
    void testReadCsvWithWrongFileFormat() {
        MockMultipartFile multipartFile = new MockMultipartFile("file", "wrong_format.txt",
                "text/plain", "some,wrong,data".getBytes());

        assertThrows(CustomException.class, () -> productImportService.readCsv(multipartFile));
    }

    @Test
    void testReadCsvWithFileReadingError() throws IOException {
        when(multipartFile.getOriginalFilename()).thenReturn("error.csv");
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getContentType()).thenReturn("text/csv");

        // Mocking IOException when transferring the file
        lenient().doThrow(IOException.class).when(multipartFile).transferTo(new File("C:\\users\\DevanshuVerma\\Documents\\BE\\first demo\\demo\\uploads\\error.csv"));

        assertThrows(CustomException.class, () -> productImportService.readCsv(multipartFile));
    }
}

