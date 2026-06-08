package com.axelor.apps.store.service.impl;

import com.axelor.apps.store.db.Cart;
import com.axelor.apps.store.db.CartItem;
import com.axelor.apps.store.db.StoreOrder;
import com.axelor.apps.store.db.StoreOrderItem;
import com.axelor.apps.store.db.repo.CartRepository;
import com.axelor.apps.store.db.repo.StoreOrderItemRepository;
import com.axelor.apps.store.db.repo.StoreOrderRepository;
import com.axelor.apps.store.dto.OrderCreateDTO;
import com.axelor.apps.store.dto.StoreOrderDTO;
import com.axelor.apps.store.mappers.Mappers;
import com.axelor.apps.store.service.OrderService;
import com.axelor.auth.db.User;
import com.axelor.db.Query;
import com.google.inject.Inject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrderServiceImpl implements OrderService {

    private final StoreOrderRepository orderRepo;
    private final StoreOrderItemRepository orderItemRepo;
    private final CartRepository cartRepo;

    @Inject
    public OrderServiceImpl(StoreOrderRepository orderRepo, StoreOrderItemRepository orderItemRepo, CartRepository cartRepo) {
        this.orderRepo = orderRepo;
        this.orderItemRepo = orderItemRepo;
        this.cartRepo = cartRepo;
    }

    @Override
    public StoreOrderDTO createOrder(User user, OrderCreateDTO dto) {
        Cart cart = Query.of(Cart.class)
                .filter("self.user = :user AND self.status = 'active'")
                .bind("user", user)
                .fetchOne();

        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new IllegalStateException("Корзина пуста");
        }

        StoreOrder order = new StoreOrder();
        order.setUser(user);
        order.setPhone(dto.phone);
        order.setEmail(dto.email);
        order.setAddress(dto.address);
        order.setDeliveryComment(dto.comment);
        order.setDeliveryType(dto.deliveryType);
        order.setStatus("pending");

        // переносим позиции из корзины
        BigDecimal total = BigDecimal.ZERO;
        List<StoreOrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cart.getItems()) {
            StoreOrderItem item = new StoreOrderItem();
            item.setStoreOrder(order);
            item.setProduct(cartItem.getProduct());
            item.setQuantity(cartItem.getQuantity());
            item.setPrice(cartItem.getProduct().getPrice());
            item.setSubtotal(
                    cartItem.getProduct().getPrice()
                            .multiply(BigDecimal.valueOf(cartItem.getQuantity()))
            );
            orderItems.add(item);
            total = total.add(item.getSubtotal());
        }

        order.setTotalPrice(total);
        orderRepo.save(order);
        orderItems.forEach(orderItemRepo::save);

        // помечаем корзину как оформленную
        cart.setStatus("ordered");
        cartRepo.save(cart);

        return Mappers.toOrderDto(order);
    }

    @Override
    public List<StoreOrderDTO> getOrders(User user) {
        return Query.of(StoreOrder.class)
                .filter("self.user = :user")
                .bind("user", user)
                .order("-id")
                .fetch()
                .stream()
                .map(Mappers::toOrderDto)
                .collect(Collectors.toList());
    }
}
