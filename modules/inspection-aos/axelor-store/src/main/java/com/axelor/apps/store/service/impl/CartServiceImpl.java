package com.axelor.apps.store.service.impl;

import com.axelor.apps.store.db.Cart;
import com.axelor.apps.store.db.CartItem;
import com.axelor.apps.store.db.StoreProduct;
import com.axelor.apps.store.db.repo.CartItemRepository;
import com.axelor.apps.store.db.repo.CartRepository;
import com.axelor.apps.store.db.repo.StoreProductRepository;
import com.axelor.apps.store.dto.CartDTO;
import com.axelor.apps.store.mappers.Mappers;
import com.axelor.apps.store.service.CartService;
import com.axelor.auth.db.User;
import com.axelor.db.Query;
import com.google.inject.Inject;

public class CartServiceImpl implements CartService {
    private final CartRepository repo;
    private final CartItemRepository itemRepo;
    private final StoreProductRepository productRepo;

    @Inject
    public CartServiceImpl(CartRepository repo, CartItemRepository itemRepo, StoreProductRepository productRepo) {
        this.repo = repo;
        this.itemRepo = itemRepo;
        this.productRepo = productRepo;
    }

    @Override
    public CartDTO getCart(User user) {
        Cart cart = getOrCreateCart(user);
        return Mappers.toCartDto(cart);
    }

    @Override
    public CartDTO addItem(User user, Long productId, Integer quantity) {
        Cart cart = getOrCreateCart(user);

        StoreProduct product = productRepo.find(productId);
        if (product == null) {
            throw new IllegalArgumentException("Товар не найден: " + productId);
        }

        // если товар уже в корзине — увеличиваем количество
        CartItem existing = Query.of(CartItem.class)
                .filter("self.cart = :cart AND self.product = :product")
                .bind("cart", cart)
                .bind("product", product)
                .fetchOne();

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + quantity);
            itemRepo.save(existing);
        } else {
            CartItem item = new CartItem();
            item.setCart(cart);
            item.setProduct(product);
            item.setQuantity(quantity);
            itemRepo.save(item);
        }

        return Mappers.toCartDto(repo.find(cart.getId()));
    }

    @Override
    public CartDTO updateQuantity(User user, Long itemId, Integer quantity) {
        CartItem item = itemRepo.find(itemId);

        if (item == null || !item.getCart().getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Позиция не найдена");
        }

        if (quantity <= 0) {
            itemRepo.remove(item);
        } else {
            item.setQuantity(quantity);
            itemRepo.save(item);
        }

        Cart cart = getOrCreateCart(user);
        return Mappers.toCartDto(repo.find(cart.getId()));
    }

    @Override
    public CartDTO removeItem(User user, Long itemId) {
        CartItem item = itemRepo.find(itemId);

        if (item == null || !item.getCart().getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Позиция не найдена");
        }

        itemRepo.remove(item);

        Cart cart = getOrCreateCart(user);
        return Mappers.toCartDto(repo.find(cart.getId()));
    }

    @Override
    public void clearCart(User user) {
        Cart cart = getOrCreateCart(user);

        if (cart.getItems() != null) {
            cart.getItems().forEach(itemRepo::remove);
        }

        repo.save(cart);
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
