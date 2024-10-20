package com.product.product_service.service;


import com.product.product_service.dto.ProductRequest;
import com.product.product_service.dto.ProductResponse;
import com.product.product_service.model.Product;
import com.product.product_service.repository.ProductRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
public class ProductService {

    @Autowired
    private ProductRepo productRepo;

    public void createProduct(ProductRequest productRequest){
        Product product = Product.builder().name(productRequest.getName())
                .description(productRequest.getDescription()).price(productRequest.getPrice()).build();

        product  = productRepo.save(product);
        log.info("Product added successfully with Id:{}", product.getId());
    }

    public List<ProductResponse> getAllProducts(){
        List<Product> products = productRepo.findAll();
        List<ProductResponse> productResponses =  products.stream().map(this::mapProductToProductResponse).toList();
        return productResponses;
    }

    private ProductResponse mapProductToProductResponse(Product product){
        ProductResponse productResponse = ProductResponse.builder().id(product.getId())
                .name(product.getName()).description(product.getDescription()).price(product.getPrice()).build();
        return productResponse;
    }
}
 