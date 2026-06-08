package com.axelor.apps.store.mappers;

import com.axelor.apps.store.db.*;
import com.axelor.apps.store.dto.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

public class Mappers {
    public static ProductDTO toProductDto(StoreProduct sp) {
        ProductDTO dto = new ProductDTO();
        dto.setId(sp.getId());
        dto.setName(sp.getName());
        dto.setBrand(sp.getBrand() != null ? sp.getBrand().getName() : null);
        dto.setCategory(sp.getCategory() != null ? sp.getCategory().getSlug() : null);
        dto.setPrice(sp.getPrice());
        dto.setOldPrice(sp.getOldPrice());
        dto.setImage(sp.getImage());
        dto.setRating(sp.getRating());
        dto.setReviews(sp.getReviewCount());
        dto.setInStock(sp.getInStock() != null);
        dto.setBadge(sp.getBadge());
        dto.setDescription(sp.getDescription());
        dto.setSpecs(sp.getSpecs() == null ? new HashMap<>()
                : sp.getSpecs().stream()
                .collect(Collectors.toMap(
                        ProductSpec::getSpecKey,
                        ProductSpec::getSpecValue)));

        return dto;
    }

    public static CategoryDTO toCategoryDto(Category c) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(c.getSlug());
        dto.setName(c.getName());
        dto.setIcon(c.getIcon());
        dto.setCount(c.getProducts() != null ? c.getProducts().size() : 0);
        return dto;
    }

    public static CartItemDTO toCartItemDto(CartItem item) {
        CartItemDTO dto = new CartItemDTO();
        dto.id       = item.getId();
        dto.product  = toProductDto(item.getProduct());
        dto.quantity = item.getQuantity();
        dto.subtotal = item.getProduct().getPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity()));
        return dto;
    }

    public static CartDTO toCartDto(Cart cart) {
        CartDTO dto = new CartDTO();
        dto.id    = cart.getId();
        dto.items = cart.getItems() == null
                ? new ArrayList<>()
                : cart.getItems().stream()
                .map(Mappers::toCartItemDto)
                .collect(Collectors.toList());
        dto.totalPrice = dto.items.stream()
                .map(i -> i.subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return dto;
    }


    public static StoreOrderDTO toOrderDto(StoreOrder order) {
        StoreOrderDTO dto = new StoreOrderDTO();
        dto.id = order.getId();
        dto.status = order.getStatus();
        dto.phone = order.getPhone();
        dto.email = order.getEmail();
        dto.address = order.getAddress();
        dto.deliveryType = order.getDeliveryType();
        dto.totalPrice = order.getTotalPrice();
        dto.items = order.getItems() == null
                ? new ArrayList<>()
                : order.getItems().stream()
                .map(Mappers::toOrderItemDto)
                .collect(Collectors.toList());
        return dto;
    }

    public static StoreOrderItemDTO toOrderItemDto(StoreOrderItem item) {
        StoreOrderItemDTO dto = new StoreOrderItemDTO();
        dto.id           = item.getId();
        dto.productName  = item.getProduct().getName();
        dto.productBrand = item.getProduct().getBrand() != null ? item.getProduct().getBrand().getName() : null;
        dto.quantity     = item.getQuantity();
        dto.price        = item.getPrice();
        dto.subtotal     = item.getSubtotal();
        return dto;
    }
}
