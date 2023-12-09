package com.pigeoff.menu.util;

import com.pigeoff.menu.database.ProductEntity;
import com.pigeoff.menu.database.RecipeEntity;

import java.util.List;

public class Export {
    public int version;
    public List<ProductEntity> products;
    public List<RecipeEntity> recipes;

    public Export(int version, List<ProductEntity> products, List<RecipeEntity> recipes) {
        this.version = version;
        this.products = products;
        this.recipes = recipes;
    }
}