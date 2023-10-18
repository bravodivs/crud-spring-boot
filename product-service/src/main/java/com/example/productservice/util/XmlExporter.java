package com.example.productservice.util;

import com.example.productservice.exception.CustomException;
import com.example.productservice.model.ProductDto;
import com.example.productservice.model.ProductDtoListWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class XmlExporter {
    private final Logger logger = LoggerFactory.getLogger(XmlExporter.class);
    @Value("${file.defaultExportName}")
    private String defaultFileNameTemplate;
    @Value("${file.dateFormat}")
    private String dateFormat;

    public File export(List<ProductDto> productDtoList) {
        String filename = getExportFileName();
        File file = new File(filename);

        try {
            if (file.createNewFile()) {
                logger.info("File created with name {}", filename);
            }

            JAXBContext context = JAXBContext.newInstance(ProductDtoListWrapper.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            ProductDtoListWrapper wrapper = new ProductDtoListWrapper();
            wrapper.setProductDtoList(productDtoList);
            marshaller.marshal(wrapper, new FileOutputStream(file));
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return file;
    }

    public String getExportFileName() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern(dateFormat));
        return defaultFileNameTemplate + timestamp + ".xml";
    }
}
