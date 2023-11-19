package com.pigeoff.menu.models;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pigeoff.menu.MenuApplication;
import com.pigeoff.menu.data.Ingredient;
import com.pigeoff.menu.database.CalendarDAO;
import com.pigeoff.menu.database.GroceryDAO;
import com.pigeoff.menu.database.MenuDatabase;
import com.pigeoff.menu.database.ProductDAO;
import com.pigeoff.menu.database.ProductEntity;
import com.pigeoff.menu.database.RecipeDAO;
import com.pigeoff.menu.database.RecipeEntity;

import java.util.HashMap;
import java.util.List;

public class RecipesViewModel extends AndroidViewModel {

    protected LiveData<List<RecipeEntity>> recipes;
    protected LiveData<List<ProductEntity>> products;
    protected RecipeDAO recipeDAO;
    protected ProductDAO productDAO;
    protected GroceryDAO groceryDAO;
    protected CalendarDAO calendarDAO;

    public RecipesViewModel(@NonNull Application application) {
        super(application);

        MenuApplication app = (MenuApplication) application;
        MenuDatabase database = app.database;
        this.recipeDAO = database.recipeDAO();
        this.groceryDAO = database.groceryDAO();
        this.calendarDAO = database.calendarDAO();
        this.productDAO = database.productDAO();

        this.recipes = recipeDAO.select();
        this.products = productDAO.getAll();

    }

    public LiveData<List<RecipeEntity>> getRecipes() {
        return recipes;
    }

    public LiveData<List<ProductEntity>> getProducts() {
        return products;
    }

    public void addItem(RecipeEntity item) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                recipeDAO.insert(item);
            }
        });
    }

    public void updateItem(RecipeEntity item) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                recipeDAO.update(item);
            }
        });
    }

    public void deleteItem(RecipeEntity item) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                groceryDAO.deleteGroceriesForRecipe(item.id);
                calendarDAO.deleteForRecipe(item.id);
                recipeDAO.delete(item);
            }
        });
    }

    public void importRecipes(List<ProductEntity> products, List<RecipeEntity> recipes, OnAdd callback) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Gson gson = new Gson();
                    HashMap<Long, Long> oldNewProductIds = new HashMap<>();
                    for (ProductEntity p : products) {
                        ProductEntity blank = new ProductEntity();
                        blank.defaultUnit = p.defaultUnit;
                        blank.label = p.label;
                        blank.secion = p.secion;

                        oldNewProductIds.put(p.id, productDAO.insertProduct(blank));
                    }


                    oldNewProductIds.forEach((key, val) -> {
                        System.out.println(String.valueOf(key) + " -> " + String.valueOf(val));
                    });

                    for (RecipeEntity r : recipes) {
                        List<Ingredient.Serialized> deserialized = gson.fromJson(
                                r.ingredients,
                                new TypeToken<List<Ingredient.Serialized>>(){}.getType()
                        );
                        for (Ingredient.Serialized s : deserialized)
                            s.ingredientId = oldNewProductIds.get(s.ingredientId);

                        r.ingredients = gson.toJson(deserialized);

                        RecipeEntity blank = new RecipeEntity();
                        blank.ingredients = r.ingredients;
                        blank.portions = r.portions;
                        blank.title = r.title;
                        blank.cookbook = r.cookbook;
                        blank.category = r.category;
                        blank.note = r.note;
                        blank.steps = r.steps;

                        recipeDAO.insert(blank);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onFailure();
                }
            }
        });
    }

    public interface OnAdd {
        void onFailure();
    }
}
