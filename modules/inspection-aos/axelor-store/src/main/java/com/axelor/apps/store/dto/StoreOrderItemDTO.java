package com.axelor.apps.store.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreOrderItemDTO {
    public Long id;
    public String productName;
    public String productBrand;
    public Integer quantity;
    public BigDecimal price;
    public BigDecimal subtotal;
}
