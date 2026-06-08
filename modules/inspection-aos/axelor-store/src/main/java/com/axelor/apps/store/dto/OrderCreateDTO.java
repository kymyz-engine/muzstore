package com.axelor.apps.store.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCreateDTO {
    public String name;
    public String phone;
    public String email;
    public String address;
    public String comment;
    public String deliveryType;
}
