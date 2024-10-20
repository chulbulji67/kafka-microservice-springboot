package com.product.product_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.product_service.controller.ProductController;
import com.product.product_service.dto.ProductResponse;
import com.product.product_service.service.ProductService;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {

    @Autowired
    MockMvc mockMvc;

//    @Autowired
//    RequestBuilder requestBuilder;

//    @InjectMocks
//    private ProductController productController;
//
    @MockBean
    private ProductService productService;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getAllProduct() throws Exception {
        ProductResponse productResponse1 =  new ProductResponse(
             1,"Iphone 16","Newly Launched",2000.0);
        ProductResponse productResponse2 =  new ProductResponse(
                1,"Iphone 15","Good in mobiles",2000.0);
        final List<ProductResponse> productResponses = Arrays.asList(productResponse1, productResponse2);

        when(productService.getAllProducts()).thenReturn(productResponses);
         mockMvc.perform(MockMvcRequestBuilders.get("/api/products")

                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk()).andExpect(content().json("[{id:1, name:'Iphone 16', description: 'Newly Launched', price: 2000}, {id:1, name:'Iphone 15', description: 'Good in mobiles', price: 2000}]"));



    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
 