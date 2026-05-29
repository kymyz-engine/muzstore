package com.axelor.apps.store.service.impl;

import com.axelor.apps.store.db.Category;
import com.axelor.apps.store.db.repo.CategoryRepository;
import com.axelor.apps.store.service.StoreService;
import com.google.inject.Inject;

import java.util.List;

public class StoreServiceImpl implements StoreService {

    private final CategoryRepository categoryRepository;

    @Inject
    public StoreServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> getCategoriesList() {
        List<Category> categories = categoryRepository.all().fetch();
        return categories;
    }
}
