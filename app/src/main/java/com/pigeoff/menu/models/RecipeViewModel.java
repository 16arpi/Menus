package com.pigeoff.menu.models;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.pigeoff.menu.MenuApplication;
import com.pigeoff.menu.database.CalendarDAO;
import com.pigeoff.menu.database.GroceryDAO;
import com.pigeoff.menu.database.MenuDatabase;
import com.pigeoff.menu.database.ProductDAO;
import com.pigeoff.menu.database.ProductEntity;
import com.pigeoff.menu.database.RecipeDAO;
import com.pigeoff.menu.database.RecipeEntity;

import java.util.List;

public class RecipeViewModel extends AndroidViewModel {

    private MenuDatabase database;
    private LiveData<RecipeEntity> item;
    private LiveData<List<ProductEntity>> productEntities;
    private CombinedLiveData recipeWithProducts;
    private RecipeDAO recipeDAO;
    private GroceryDAO groceryDAO;
    private CalendarDAO calendarDAO;
    private ProductDAO productDAO;

    public RecipeViewModel(long id, @NonNull Application application) {
        super(application);

        MenuApplication app = (MenuApplication) application;
        this.database = app.database;
        this.recipeDAO = database.recipeDAO();
        this.groceryDAO = database.groceryDAO();
        this.calendarDAO = database.calendarDAO();
        this.productDAO = database.productDAO();
        this.productEntities = productDAO.getAll();

        this.item = id > 0 ? recipeDAO.select(id) : new MutableLiveData<>(new RecipeEntity());

        this.recipeWithProducts = new CombinedLiveData<>(
                item,
                productEntities,
                CombinedLiveData.CombinedObject::new);

    }

    public CombinedLiveData<
            LiveData<RecipeEntity>,
            LiveData<List<ProductEntity>>,
            CombinedLiveData.CombinedObject<
                    RecipeEntity,
                    List<ProductEntity>
                    >
            > getItem() {
        return recipeWithProducts;
    }

    public LiveData<List<ProductEntity>> getProducts() {
        return productEntities;
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
}
