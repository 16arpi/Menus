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
    private MenuDatabase database;
    private LiveData<List<GroceryWithProduct>> items;
    private LiveData<List<GroceryWithProduct>> eventsItems;
    private LiveData<List<ProductEntity>> productsEntities;
    private GroceryDAO groceryDAO;
    private ProductDAO productDAO;
    public GroceriesViewModel(@NonNull Application application) {
        super(application);

        MenuApplication app = (MenuApplication) application;
        this.database = app.database;
        this.groceryDAO = database.groceryDAO();
        this.productDAO = database.productDAO();

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

    public void addItem(GroceryEntity item) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                groceryDAO.addGrocery(item);
            }
        });
    }

    public void updateItem(GroceryEntity item) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                groceryDAO.updateGrocery(item);
            }
        });
    }

    public void deleteItem(GroceryEntity item) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                groceryDAO.deleteGrocery(item);
            }
        });
    }

    public void deleteAllChecked() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                groceryDAO.deleteAllChecked();
            }
        });
    }

    public void checkGrocery(GroceryWithProduct item, boolean checked) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                groceryDAO.checkGrocery(item.grocery.id, checked);
            }
        });
    }

    public void checkGrocery(GrocerieGroup item, boolean checked) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                for (GroceryEntity g : item.groceries) groceryDAO.checkGrocery(g.id, checked);
            }
        });
    }

    public void deleteAllItems() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                groceryDAO.deleteAllItems();
            }
        });
    }

    public void deleteGroceriesForCalendar(long eventId) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                groceryDAO.deleteGroceriesForCalendar(eventId);
            }
        });
    }


}
