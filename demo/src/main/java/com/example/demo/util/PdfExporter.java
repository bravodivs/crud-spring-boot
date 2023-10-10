package com.example.demo.util;

import com.example.demo.exception.CustomException;
import com.example.demo.model.ProductDto;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PdfExporter {
    private static final Logger logger = LoggerFactory.getLogger(PdfExporter.class);
    private List<ProductDto> productDtoList;
    private File file;

    private void writeTableHeader(PdfPTable table) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(Color.gray);

        Font font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setColor(Color.WHITE);

        cell.setPhrase(new Phrase("ID", font));

        table.addCell(cell);

        cell.setPhrase(new Phrase("Name", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Description", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Price", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Quantity", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Images", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Created At", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Modified At", font));
        table.addCell(cell);
    }

    private void writeTableData(PdfPTable table) {
        for (ProductDto productDto : productDtoList) {
            table.addCell(String.valueOf(productDto.getId()));
            table.addCell(productDto.getName());
            table.addCell(productDto.getDescription());
            table.addCell(String.valueOf(productDto.getPrice()));
            table.addCell(String.valueOf(productDto.getQuantity()));
            table.addCell(String.valueOf(productDto.getImages()));
            table.addCell(String.valueOf(productDto.getCreatedAt()));
            table.addCell(String.valueOf(productDto.getModifiedAt()));
        }
    }

    public File export(List<ProductDto> productDtoList) throws DocumentException {
        this.productDtoList = productDtoList;
        String filename = getExportFileName();
        file = new File(filename);
        try {
            if (file.createNewFile()) {
                logger.info("File created with name {}", filename);
            }
        } catch (Exception e) {
            throw new CustomException(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        try (Document document = new Document(PageSize.A4.rotate())) {
            FileOutputStream outputStream = new FileOutputStream(file);
            PdfWriter.getInstance(document, outputStream);

            document.open();
            Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            font.setSize(16);
            font.setColor(Color.BLUE);

            Paragraph p = new Paragraph("List of products", font);
            p.setAlignment(Element.ALIGN_CENTER);

            document.add(p);

            PdfPTable table = new PdfPTable(8);
            table.setWidthPercentage(100f);
            table.setWidths(new float[]{1.5f, 2.0f, 3.0f, 1.5f, 1.5f, 1.3f, 2.0f, 2.0f});
            table.setSpacingBefore(10);

            writeTableHeader(table);
            writeTableData(table);

            document.add(table);
        } catch (Exception e) {
            throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return file;
    }

    public String getExportFileName() {
        String dateFormat = "yyyy_MM_dd_HH-mm-ss";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern(dateFormat));
        String defaultFileNameTemplate = "products-export-";
        return defaultFileNameTemplate + timestamp + ".pdf";
    }
}
