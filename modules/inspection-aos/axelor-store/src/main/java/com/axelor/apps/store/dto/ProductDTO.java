package com.axelor.apps.store.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {
    private long id;
    private String name;
    private String brand;
    private String category;
    private BigDecimal price;
    private BigDecimal oldPrice;
    private String image;
    public BigDecimal rating;
    public Integer reviews;
    public Boolean inStock;
    private String badge;
    private String description;
    private Map<String, String> specs;
}
