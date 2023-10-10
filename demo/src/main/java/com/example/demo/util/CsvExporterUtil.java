package com.example.demo.util;

import com.example.demo.exception.CustomException;
import com.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;

import java.io.IOException;

import java.util.List;

@Component
public class CsvExporterUtil {
    private static final Logger logger = LoggerFactory.getLogger(CsvExporterUtil.class);

    private CsvExporterUtil(){}
    public static File export(List<String[]> dataArray, String filename) {
        File file = new File(filename);
        try (FileWriter outputFile = new FileWriter(file);
             CSVWriter writer = new CSVWriter(outputFile);
        ) {
            if (file.createNewFile()) {
                logger.info("File created with name {}", filename);
            }
            writer.writeAll(dataArray);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw new CustomException(String.format("Error occurred while writing the csv file- %s", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return file;
    }
}
