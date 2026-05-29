package com.axelor.apps.store.service.impl;

import com.axelor.apps.store.db.Cart;
import com.axelor.apps.store.db.repo.CartRepository;
import com.axelor.apps.store.dto.CartDTO;
import com.axelor.apps.store.mappers.Mappers;
import com.axelor.apps.store.service.CartService;
import com.axelor.auth.db.User;
import com.axelor.db.Query;
import com.google.inject.Inject;

public class CartServiceImpl implements CartService {
    private final CartRepository repo;

    @Inject
    public CartServiceImpl(CartRepository repo) {
        this.repo = repo;
    }

    @Override
    public CartDTO getCart(User user) {
        Cart cart = getOrCreateCart(user);
        return Mappers.toCartDto(cart);
    }


    private Cart getOrCreateCart(User user) {
        Cart cart = Query.of(Cart.class)
                .filter("self.user = :user AND self.status = 'active'")
                .bind("user", user)
                .fetchOne();

        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cart.setStatus("active");
            repo.save(cart);
        }

        return cart;
    }
}
