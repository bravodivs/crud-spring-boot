package com.example.demo;

import com.example.demo.service.ProductsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class testProductService {
    @Autowired
    private ProductsService productsService;

    @Test
    public void contextLoads() throws Exception{
        assertThat(productsService).isNotNull();
    }

}
