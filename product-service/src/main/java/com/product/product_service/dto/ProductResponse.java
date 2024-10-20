package com.product.product_service.dto;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ProductResponse {
    private int id;

    private String name;
    private String description;

    private double price;
}
 