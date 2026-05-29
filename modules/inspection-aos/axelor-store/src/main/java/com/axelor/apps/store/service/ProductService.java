package com.axelor.apps.store.service;

import com.axelor.apps.store.db.Category;
import com.axelor.apps.store.db.StoreProduct;

import java.util.List;

public interface ProductService {
    List<StoreProduct> getProductsList(String category, String brand, String priceMin, String priceMax, String sortBy);
    StoreProduct getProduct(Long id);
}
