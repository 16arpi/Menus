package com.pigeoff.menu.models;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.pigeoff.menu.MenuApplication;
import com.pigeoff.menu.database.MenuDatabase;
import com.pigeoff.menu.database.ProductDAO;
import com.pigeoff.menu.database.ProductEntity;
import com.pigeoff.menu.database.RecipeDAO;
import com.pigeoff.menu.database.RecipeEntity;
import com.pigeoff.menu.util.Export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

public class RecipesViewModel extends AndroidViewModel {

    private LiveData<List<RecipeEntity>> items;
    private LiveData<List<ProductEntity>> products;
    private RecipeDAO recipeDAO;
    private ProductDAO productDAO;

    public RecipesViewModel(@NonNull Application application) {
        super(application);

        MenuApplication app = (MenuApplication) application;
        MenuDatabase database = app.database;
        recipeDAO = database.recipeDAO();
        productDAO = database.productDAO();
        items = recipeDAO.select();
        products = productDAO.getAll();

    }

    public LiveData<List<RecipeEntity>> getItems() {
        return items;
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
}
