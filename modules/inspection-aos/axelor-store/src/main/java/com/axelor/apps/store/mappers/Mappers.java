package com.axelor.apps.store.mappers;

import com.axelor.apps.store.db.Cart;
import com.axelor.apps.store.db.CartItem;
import com.axelor.apps.store.db.ProductSpec;
import com.axelor.apps.store.db.StoreProduct;
import com.axelor.apps.store.dto.CartDTO;
import com.axelor.apps.store.dto.CartItemDTO;
import com.axelor.apps.store.dto.ProductDTO;

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
}
