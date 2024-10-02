package com.pigeoff.menu.models;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.pigeoff.menu.MenuApplication;
import com.pigeoff.menu.data.GrocerieGroup;
import com.pigeoff.menu.database.GroceryDAO;
import com.pigeoff.menu.database.GroceryEntity;
import com.pigeoff.menu.database.GroceryWithProduct;
import com.pigeoff.menu.database.MenuDatabase;
import com.pigeoff.menu.database.ProductDAO;
import com.pigeoff.menu.database.ProductEntity;

import java.util.List;

public class GroceriesViewModel extends AndroidViewModel {
    private final LiveData<List<GroceryWithProduct>> items;
    private final LiveData<List<GroceryWithProduct>> eventsItems;
    private final LiveData<List<ProductEntity>> productsEntities;
    private final GroceryDAO groceryDAO;
    private final ProductDAO productDAO;

    public GroceriesViewModel(@NonNull Application application) {
        super(application);

        MenuApplication app = (MenuApplication) application;
        MenuDatabase database = app.database;

        this.productDAO = database.productDAO();
        this.groceryDAO = database.groceryDAO();
        this.items = groceryDAO.getGroceries();
        this.eventsItems = groceryDAO.getEventsGroceries();
        this.productsEntities = productDAO.getAll();
    }

    public LiveData<List<GroceryWithProduct>> getItems() {
        return items;
    }

    public LiveData<List<GroceryWithProduct>> getEventsItems() {
        return eventsItems;
    }

    public LiveData<List<ProductEntity>> getProducts() {
        return productsEntities;
    }

    public void addItemWithProduct(int section, String productLabel, GroceryEntity item) {
        AsyncTask.execute(() -> {
            ProductEntity product = productDAO.selectByName(productLabel);
            long productId;
            if (product == null) {
                product = new ProductEntity();
                product.label = productLabel;
                product.section = section;
                product.permanent = false;
                productId  = productDAO.insertProduct(product);
            } else {
                productId = product.id;
            }


            item.ingredientId = productId;
            groceryDAO.addGrocery(item);
        });
    }

    // TODO Swipe to delete
    public void deleteItem(GroceryEntity item) {
        AsyncTask.execute(() -> {
            groceryDAO.deleteGrocery(item);
            productDAO.deleteTemporaryProduct(item.ingredientId);
        });
    }

    public void deleteAllChecked() {
        AsyncTask.execute(() -> {
            productDAO.deleteTemporaryCheckedProducts();
            groceryDAO.deleteAllChecked();
        });
    }

    public void checkGrocery(GrocerieGroup item, boolean checked) {
        AsyncTask.execute(() -> {
            for (GroceryEntity g : item.groceries) groceryDAO.checkGrocery(g.id, checked);
        });
    }

    public void deleteAllItems() {
        AsyncTask.execute(() -> {
            productDAO.deleteTemporaryProducts();
            groceryDAO.deleteAllItems();
        });
    }

    public void deleteGroceriesForCalendar(long eventId) {
        AsyncTask.execute(() -> groceryDAO.deleteGroceriesForCalendar(eventId));
    }

    public void changeGrocerieSection(GrocerieGroup group, int newSection) {
        if (group.product.section == newSection) return;
        group.product.section = newSection;
        AsyncTask.execute(() -> productDAO.updateProduct(group.product));
    }


}
