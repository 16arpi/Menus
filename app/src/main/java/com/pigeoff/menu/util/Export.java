package com.pigeoff.menu.util;

import com.pigeoff.menu.database.ProductEntity;
import com.pigeoff.menu.database.RecipeEntity;

import java.util.List;

public class Export {
    public List<ProductEntity> products;
    public List<RecipeEntity> recipes;

    public Export(List<ProductEntity> products, List<RecipeEntity> recipes) {
        this.products = products;
        this.recipes = recipes;
    }
}