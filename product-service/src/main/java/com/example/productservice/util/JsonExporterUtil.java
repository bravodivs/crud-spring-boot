package com.example.productservice.util;

import com.example.productservice.exception.CustomException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.io.File;
import java.text.SimpleDateFormat;

public class JsonExporterUtil {
    private static final Logger logger = LoggerFactory.getLogger(JsonExporterUtil.class);

    private JsonExporterUtil() {
    }

    public static File export(Object dataList, String filename) {
        File file = new File(filename);
        try {
            if (file.createNewFile()) {
                logger.info("File created with name {}", filename);
            }
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
            objectMapper.writeValue(file, dataList);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return file;
    }
}
