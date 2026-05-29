package com.axelor.apps.store.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDTO {
    public Long id;
    public List<CartItemDTO> items;
    public Integer totalItems;
    public BigDecimal totalPrice;
}
