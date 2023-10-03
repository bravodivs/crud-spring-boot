package com.example.demo.service;

import com.example.demo.exception.CustomException;
import com.example.demo.model.Product;
import com.example.demo.model.ProductListWrapper;
import com.example.demo.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.opencsv.CSVWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


@Service
public class ProductsService {
    private final Logger logger = LoggerFactory.getLogger(ProductsService.class);
    @Autowired
    ProductRepository productRepository;

    private static void drawTableHeader(PDPageContentStream contentStream, float yPosition, float tableWidth,
                                        float tableRowHeight, float cellMargin) throws IOException {
        contentStream.beginText();
        contentStream.newLineAtOffset(cellMargin, yPosition);
        contentStream.showText("Product ID");
        contentStream.newLineAtOffset(tableWidth / 8, 0);
        contentStream.showText("Name");
        contentStream.newLineAtOffset(tableWidth / 8, 0);
        contentStream.showText("Description");
        contentStream.newLineAtOffset(tableWidth / 8, 0);
        contentStream.showText("Price");
        contentStream.newLineAtOffset(tableWidth / 8, 0);
        contentStream.showText("Quantity");
        contentStream.newLineAtOffset(tableWidth / 8, 0);
        contentStream.showText("Images");
        contentStream.newLineAtOffset(tableWidth / 8, 0);
        contentStream.showText("Created At");
        contentStream.newLineAtOffset(tableWidth / 8, 0);
        contentStream.showText("Modified At");

        contentStream.endText();
    }

    private static void drawTableRow(PDPageContentStream contentStream, float yPosition, float tableWidth,
                                     float tableRowHeight, float cellMargin, int cols, Product product) throws IOException {
        float margin = cellMargin;
        contentStream.beginText();
        contentStream.newLineAtOffset(margin, yPosition);
        contentStream.showText(product.getId());
        contentStream.newLineAtOffset(tableWidth / cols, 0);
        contentStream.showText(product.getName());
        contentStream.newLineAtOffset(tableWidth / cols, 0);
        contentStream.showText(product.getDescription());
        contentStream.newLineAtOffset(tableWidth / cols, 0);
        contentStream.showText(String.valueOf(product.getPrice()));
        contentStream.newLineAtOffset(tableWidth / cols, 0);
        contentStream.showText(String.valueOf(product.getQuantity()));
        contentStream.newLineAtOffset(tableWidth / cols, 0);
        contentStream.showText(String.valueOf(product.getImages()));
        contentStream.newLineAtOffset(tableWidth / cols, 0);
        contentStream.showText(String.valueOf(product.getCreatedAt()));
        contentStream.newLineAtOffset(tableWidth / cols, 0);
        contentStream.showText(String.valueOf(product.getModifiedAt()));
        contentStream.endText();
    }

    public List<Product> getAllProducts(String searchKey) {
        if (searchKey.equals("")) {
            List<Product> lst = productRepository.findAll();
            int l = lst.size();
            logger.info("List of products with size {} returned", l);
            return lst;
        } else {
            logger.info("Search key {}", searchKey);
            List<Product> searchedProducts = productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(searchKey, searchKey);
            logger.info("Found products list of size {}", searchedProducts.size());
            return searchedProducts;
        }
    }

    public Product getProductById(String id) {
        return productRepository.findById(id).orElseThrow(() -> {
            logger.error("Product not found with id {}", id);
            return new CustomException(String.format("Product with id %s not found.", id), HttpStatus.NOT_FOUND);
        });
    }

    public Product saveProduct(Product product, boolean update) {
        if (!update && product.getId() != null && getProductById(product.getId()) != null) {
            logger.error("Tried to add product wih duplicate id");
            throw new CustomException(String.format("Product with id %s already exists", product.getId()), HttpStatus.CONFLICT);
        }
        productRepository.save(product);
        logger.info("Product saved");
        return getProductById(product.getId());
    }

    public List<Product> saveAllProducts(List<Product> prodList) {
        prodList.forEach(product -> {
            if (!validateProduct(product)) {
                logger.error("Invalid arguments provided while saving a list of products");
                throw new CustomException("Invalid arguments while saving/updating");

            }
        });
        productRepository.saveAll(prodList);
        logger.info("Product list saved");
        return getAllProducts("");
    }

    public List<Product> deleteProduct(String id) {

        getProductById(id);
        productRepository.deleteById(id);
        logger.info("Product with id {} deleted", id);
        return getAllProducts("");
    }

    public void deleteAllProducts() {
        productRepository.deleteAll();
    }

//    TODO: { create another class to route to required method. pass to methods-response, products array.
//          if product list empty then throw error.
//          if invalid type given then throw error
//          }

    public Product updateProduct(String id, Product product) {
        Product oldProduct = getProductById(id);
        Product newProduct = new Product();

        if (newProduct.getId() != null) newProduct.setId(id);
        else newProduct.setId(oldProduct.getId());

        if (product.getName() != null) newProduct.setName(product.getName());
        else newProduct.setName(oldProduct.getName());

        if (product.getDescription() != null) newProduct.setDescription(product.getDescription());
        else newProduct.setDescription(oldProduct.getDescription());

        if (product.getQuantity() != 0) newProduct.setQuantity(product.getQuantity());
        else newProduct.setQuantity(oldProduct.getQuantity());

        if (product.getPrice() != 0) newProduct.setPrice(product.getPrice());
        else newProduct.setPrice(oldProduct.getPrice());

        if (product.getImages() != null) newProduct.setImages(product.getImages());
        else newProduct.setImages(oldProduct.getImages());

        newProduct.setCreatedAt(oldProduct.getCreatedAt());
        if (validateProduct(newProduct)) saveProduct(newProduct, true);
        else {
            logger.error("Invalid arguments while updating a product!");
            throw new CustomException("Invalid arguments");
        }
        logger.info("Product with id {} updated", id);
        return getProductById(id);
    }

    public boolean validateProduct(Product product) {
        HashMap<String, String> errors = new HashMap<>();
        if (product.getName() == null || product.getName().equals("")) errors.put("Name", "Name cant be empty");
        if (product.getDescription() == null || product.getDescription().equals(""))
            errors.put("Description", "Description cant be empty");
        if (product.getPrice() <= 0) errors.put("Price", "Price cant be empty or negative");
        if (product.getQuantity() <= 0) errors.put("Quantity", "Quantity cant be empty or negative");
        if (product.getImages() == null || product.getImages().isEmpty()) errors.put("Images", "Images cant be empty");
        if (product.getImages() != null) product.getImages().forEach(image -> {
            if (image.isEmpty() || image.isBlank()) errors.put("Image", "Image cant be empty");
        });
        logger.error(String.valueOf(errors));
        return errors.isEmpty();
    }

    public void exportCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=products.csv");
        List<Product> productList = productRepository.findAll();
//        List<Product> productList = Collections.emptyList();
        if (productList.isEmpty()) {
            throw new CustomException("Product list is empty", HttpStatus.NO_CONTENT);
        }
        try (PrintWriter writer = response.getWriter(); CSVWriter csvWriter = new CSVWriter(writer)) {

            ArrayList<String> headers = new ArrayList<>();
            Field[] fields = Product.class.getDeclaredFields();
            for (Field field : fields) {
                headers.add(field.getName().toUpperCase());
            }

            csvWriter.writeNext(headers.toArray(new String[0]));

            for (Product product : productList) {
                String[] data = {product.getId(),
                        product.getName(),
                        product.getDescription(),
                        String.valueOf(product.getQuantity()),
                        String.valueOf(product.getPrice()),
                        String.valueOf(product.getImages()),
                        String.valueOf(product.getCreatedAt()),
                        String.valueOf(product.getModifiedAt())};
                csvWriter.writeNext(data);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error converting the file");
        }
    }

    public String exportJson(HttpServletResponse response) {
        response.setContentType("text/json");
        response.setHeader("Content-Disposition", "attachment; filename=products.json");

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
            return objectMapper.writeValueAsString(getAllProducts(""));
        } catch (Exception e) {
            return "Error converting to json";
        }
    }

    //    TODO: width problem. reconfigure as a table. wrap text in different lines.
    public void exportPdf(HttpServletResponse response) throws IOException {
        response.setContentType("text/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=products.pdf");

        List<Product> products = getAllProducts("");
        String filePath = "products.pdf";
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);

            float margin = 50;
            float yStart = page.getMediaBox().getHeight() - margin;
            float tableWidth = page.getMediaBox().getWidth() - 2 * margin;
            float yPosition = yStart;
            int rows = products.size();
            int cols = 8;
            float rowHeight = 20;
            float tableHeight = rowHeight * rows;
            float tableRowHeight = tableHeight / (float) rows;
            float tableWidthMargin = tableWidth / (float) cols;
            float cellMargin = tableWidthMargin / 5f;

            //  table headers
            drawTableHeader(contentStream, yPosition, tableWidth, tableRowHeight, cellMargin);

            //  table rows with product data
            yPosition -= tableRowHeight;
            for (Product product : products) {
                drawTableRow(contentStream, yPosition, tableWidth, tableRowHeight, cellMargin, cols, product);
                yPosition -= tableRowHeight;
            }
//TODO: not auto downloading
            contentStream.close();
            document.save(filePath);
            logger.info("PDF created successfully");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exportXml(HttpServletResponse response) throws IOException {
        response.setContentType("text/xml");
        response.setHeader("Content-Disposition", "attachment; filename=products.xml");

        String filePath = "products.xml";
        try {
            JAXBContext context = JAXBContext.newInstance(ProductListWrapper.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            ProductListWrapper wrapper = new ProductListWrapper();
            wrapper.setProducts(getAllProducts(""));
            marshaller.marshal(wrapper, new FileOutputStream(filePath));
            logger.info("XML created successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//            public void bulkImport()
}
