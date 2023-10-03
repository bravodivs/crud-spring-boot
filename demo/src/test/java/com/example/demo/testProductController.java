package com.example.demo;

import com.example.demo.controller.MainController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class testProductController {
    @Autowired
    private MainController productController;

    @Test
    public void contextLoads() throws Exception{
        assertThat(productController).isNotNull();
    }

}
