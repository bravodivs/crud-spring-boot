package com.example.demo.util;

import com.example.demo.exception.CustomException;
import com.opencsv.CSVWriter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class CsvExporterUtilTest {
    @Mock
    CSVWriter csvWriter;

    @InjectMocks
    CsvExporterUtil csvExporterUtil;

    private List<String[]> dataArray;
    @BeforeEach
    void setUp() {
        // Initialize test data
        dataArray = new ArrayList<>();
        dataArray.add(new String[]{"header1", "header2"});
        dataArray.add(new String[]{"data1", "data2"});
    }

    @Test
    void export_Successful() {
        // Arrange
//        when(csvWriter.writeAll(dataArray));


        // Act
        CsvExporterUtil.export(dataArray,"csv");

        // Assert
        // Verify that writeAll method of CSVWriter is called with the correct data
        verify(csvWriter, times(1)).writeAll(dataArray);
    }

    @Test
    void export_Failure() throws IOException {
        // Arrange
//        when(csvWriter.writeAll(any())).thenThrow(new IOException("Error writing to CSV file"));

        // Act & Assert
        // Verify that CustomException is thrown with the correct status code and message
        CustomException exception = Assertions.assertThrows(
                CustomException.class,
                () -> CsvExporterUtil.export(dataArray, "Csv")
        );

        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());
        Assertions.assertEquals("Error writing to CSV file", exception.getMessage());
    }

}