package com.pigeoff.menu.models;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.pigeoff.menu.MenuApplication;
import com.pigeoff.menu.data.Ingredient;
import com.pigeoff.menu.database.MenuDatabase;
import com.pigeoff.menu.database.ProductDAO;
import com.pigeoff.menu.database.ProductEntity;
import com.pigeoff.menu.database.RecipeDAO;
import com.pigeoff.menu.database.RecipeEntity;
import com.pigeoff.menu.util.Util;

import java.util.List;

public class ProductViewModel extends AndroidViewModel {

    LiveData<List<ProductEntity>> items;
    ProductDAO productDAO;
    RecipeDAO recipeDAO;

    public ProductViewModel(@NonNull Application application) {
        super(application);

        MenuApplication app = (MenuApplication) application;
        MenuDatabase database = app.database;

        productDAO = database.productDAO();
        recipeDAO = database.recipeDAO();
        items = productDAO.getAll();
    }

    public LiveData<List<ProductEntity>> getItems() {
        return items;
    }
    public void addProduct(ProductEntity item, FragmentActivity activity, ProductAddCallBack callBack) {
        new Thread(() -> {
            long id = productDAO.insertProduct(item);
            activity.runOnUiThread(() -> callBack.onAdd(id));
        }).start();
    }

    public void updateProduct(ProductEntity item) {
        AsyncTask.execute(() -> productDAO.updateProduct(item));
    }

    public void deleteProduct(ProductEntity item, List<ProductEntity> products, FragmentActivity activity, ProductDeleteCallback callback) {
        new Thread(() -> {
            boolean success = true;

            List<RecipeEntity> recipes = recipeDAO.selectStatic();

            for (RecipeEntity r : recipes) {
                List<Ingredient> ingredients = Ingredient.fromJson(Util.productsToDict(products), r.ingredients);
                for (Ingredient i : ingredients) {
                    if (i.product.id == item.id) {
                        success = false;
                        break;
                    }
                }
                if (!success) break;
            }

            if (success) productDAO.deleteProduct(item);

            boolean finalSuccess = success;
            activity.runOnUiThread(() -> {
                if (finalSuccess) callback.onSuccess();
                else callback.onFailure();
            });
        }).start();
    }

    public interface ProductDeleteCallback {
        void onSuccess();
        void onFailure();
    }

    public interface ProductAddCallBack {
        void onAdd(long id);
    }
}
