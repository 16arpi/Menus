package com.pigeoff.menu.models;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.pigeoff.menu.MenuApplication;
import com.pigeoff.menu.database.CalendarDAO;
import com.pigeoff.menu.database.CalendarEntity;
import com.pigeoff.menu.database.CalendarWithRecipe;
import com.pigeoff.menu.database.GroceryDAO;
import com.pigeoff.menu.database.MenuDatabase;
import com.pigeoff.menu.database.ProductDAO;
import com.pigeoff.menu.database.ProductEntity;
import com.pigeoff.menu.database.RecipeDAO;

import java.util.List;

public class EventViewModel extends AndroidViewModel {

    CalendarDAO calendarDAO;
    GroceryDAO groceryDAO;
    ProductDAO productDAO;
    RecipeDAO recipeDAO;

    private final CombinedLiveData eventWithProducts;

    public EventViewModel(long eventId, @NonNull Application application) {
        super(application);

        MenuApplication app = (MenuApplication) application;
        MenuDatabase database = app.database;

        calendarDAO = database.calendarDAO();
        groceryDAO = database.groceryDAO();
        productDAO = database.productDAO();
        recipeDAO = database.recipeDAO();


        LiveData<CalendarWithRecipe>  item = calendarDAO.select(eventId);
        LiveData<List<ProductEntity>> products = productDAO.getAll();

        eventWithProducts = new CombinedLiveData<>(
                item,
                products,
                CombinedLiveData.CombinedObject::new);
    }

    public CombinedLiveData<
            LiveData<CalendarWithRecipe>,
            LiveData<List<ProductEntity>>,
            CombinedLiveData.CombinedObject<
                    CalendarWithRecipe,
                    List<ProductEntity>
                    >
            > getItem() {
        return eventWithProducts;
    }

    public void update(CalendarEntity item) {
        AsyncTask.execute(() -> calendarDAO.update(item));
    }

}
