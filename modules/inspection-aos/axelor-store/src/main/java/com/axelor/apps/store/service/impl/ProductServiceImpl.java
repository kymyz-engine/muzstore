package com.axelor.apps.store.service.impl;

import com.axelor.apps.base.db.Product;
import com.axelor.apps.store.db.Category;
import com.axelor.apps.store.db.StoreProduct;
import com.axelor.apps.store.db.repo.CategoryRepository;
import com.axelor.apps.store.db.repo.StoreProductRepository;
import com.axelor.apps.store.service.ProductService;
import com.axelor.apps.store.service.StoreService;
import com.axelor.db.Query;
import com.google.inject.Inject;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductServiceImpl implements ProductService {

    private final CategoryRepository categoryRepository;
    private final StoreProductRepository productRepository;

    @Inject
    public ProductServiceImpl(CategoryRepository categoryRepository, StoreProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    public List<StoreProduct> getProductsList(String category, String brand, String priceMin, String priceMax, String sortBy) {
        StringBuilder query = new StringBuilder("1 = 1");
        Map<String, Object> params = new HashMap<>();

        if (category != null && !category.isBlank()) {
            query.append(" AND category.slug = :category");
            params.put("category", category);
        }

        if (brand != null && !brand.isBlank()) {
            query.append(" AND brand.name = :brand");
            params.put("brand", brand);
        }

        if (priceMin != null && !priceMin.isBlank()) {
            query.append(" AND price >= :priceMin");
            params.put("priceMin", new BigDecimal(priceMin));
        }

        if (priceMax != null && !priceMax.isBlank()) {
            query.append(" AND price <= :priceMax");
            params.put("priceMax", new BigDecimal(priceMax));
        }

        Query<StoreProduct> q = Query.of(StoreProduct.class)
                .filter(query.toString());

        params.forEach(q::bind);

        if (sortBy == null || sortBy.isBlank()) {
            q = q.order("-id");
        } else if (sortBy.equals("price-asc")) {
            q = q.order("price");
        } else if (sortBy.equals("price-desc")) {
            q = q.order("-price");
        } else if (sortBy.equals("rating")) {
            q = q.order("-rating");
        } else if (sortBy.equals("name")) {
            q = q.order("name");
        } else {
            q = q.order("-id");
        }

        return q.fetch();
    }

    @Override
    public StoreProduct getProduct(Long id) {
        return productRepository.find(id);
    }
}
