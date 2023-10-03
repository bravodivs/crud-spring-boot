package com.example.demo;

import com.example.demo.repository.ProductRepository;
import com.example.demo.service.ProductsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
class DemoApplicationTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
    }

    @Test
    public void testGetProduct() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/show_products"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

//    public void testGetProductById() throws Exception {
//
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/show_products/" + id))
//                .andExpect(MockMvcResultMatchers.)
//    }

	@Test
	public void testSaveProduct() throws Exception{
		String body = "{\"key\":\"value\"}";
		mockMvc.perform(MockMvcRequestBuilders.post("/add")
						.contentType(MediaType.APPLICATION_JSON).content(body))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

}
