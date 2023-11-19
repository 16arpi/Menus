package com.pigeoff.menu.models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pigeoff.menu.database.ProductEntity;
import com.pigeoff.menu.database.RecipeEntity;

import java.util.List;

public class RecipeViewModel extends RecipesViewModel {
    private CombinedLiveData recipeWithProducts;
    public RecipeViewModel(long id, @NonNull Application application) {
        super(application);

        this.products = productDAO.getAll();

        LiveData<RecipeEntity> recipe = id > 0 ? recipeDAO.select(id) : new MutableLiveData<>(new RecipeEntity());

        this.recipeWithProducts = new CombinedLiveData<>(
                recipe,
                products,
                CombinedLiveData.CombinedObject::new);
    }

    public CombinedLiveData<
            LiveData<RecipeEntity>,
            LiveData<List<ProductEntity>>,
            CombinedLiveData.CombinedObject<
                    RecipeEntity,
                    List<ProductEntity>
                    >
            > getRecipe() {
        return recipeWithProducts;
    }
}
