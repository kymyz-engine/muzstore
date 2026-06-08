package com.axelor.apps.store.module;

import com.axelor.app.AxelorModule;
import com.axelor.apps.store.service.CartService;
import com.axelor.apps.store.service.OrderService;
import com.axelor.apps.store.service.ProductService;
import com.axelor.apps.store.service.StoreService;
import com.axelor.apps.store.service.impl.CartServiceImpl;
import com.axelor.apps.store.service.impl.OrderServiceImpl;
import com.axelor.apps.store.service.impl.ProductServiceImpl;
import com.axelor.apps.store.service.impl.StoreServiceImpl;

public class StoreModule extends AxelorModule {

    @Override
    protected void configure() {
        bind(StoreService.class).to(StoreServiceImpl.class);
        bind(ProductService.class).to(ProductServiceImpl.class);
        bind(CartService.class).to(CartServiceImpl.class);
        bind(OrderService.class).to(OrderServiceImpl.class);
    }
}
