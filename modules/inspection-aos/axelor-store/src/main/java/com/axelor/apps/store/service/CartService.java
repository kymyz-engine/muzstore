package com.axelor.apps.store.service;

import com.axelor.apps.store.dto.CartDTO;
import com.axelor.auth.db.User;

public interface CartService {
    CartDTO getCart(User user);
}
