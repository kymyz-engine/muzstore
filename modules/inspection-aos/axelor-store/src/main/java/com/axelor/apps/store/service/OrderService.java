package com.axelor.apps.store.service;

import com.axelor.apps.store.dto.OrderCreateDTO;
import com.axelor.apps.store.dto.StoreOrderDTO;
import com.axelor.auth.db.User;

import java.util.List;

public interface OrderService {
    StoreOrderDTO createOrder(User user, OrderCreateDTO dto);
    List<StoreOrderDTO> getOrders(User user);
}
