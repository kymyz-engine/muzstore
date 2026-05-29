package com.axelor.apps.store.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemDTO {
    public Long id;
    public ProductDTO product;
    public Integer quantity;
    public BigDecimal subtotal;
}
