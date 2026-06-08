package com.axelor.apps.store.service;

import com.axelor.apps.store.dto.CartDTO;
import com.axelor.auth.db.User;

public interface CartService {
    CartDTO getCart(User user);
    CartDTO addItem(User user, Long productId, Integer quantity);
    CartDTO updateQuantity(User user, Long itemId, Integer quantity);
    CartDTO removeItem(User user, Long itemId);
    void clearCart(User user);
}
