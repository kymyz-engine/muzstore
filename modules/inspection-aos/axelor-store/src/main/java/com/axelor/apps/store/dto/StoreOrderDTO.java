package com.axelor.apps.store.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreOrderDTO {
    public Long id;
    public String status;
    public String firstName;
    public String lastName;
    public String phone;
    public String email;
    public String city;
    public String district;
    public String address;
    public String deliveryType;
    public BigDecimal totalPrice;
    public List<StoreOrderItemDTO> items;
}
