package com.pigeoff.menu.models;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

import com.pigeoff.menu.MenuApplication;
import com.pigeoff.menu.data.Ingredient;
import com.pigeoff.menu.database.CalendarDAO;
import com.pigeoff.menu.database.CalendarEntity;
import com.pigeoff.menu.database.CalendarWithRecipe;
import com.pigeoff.menu.database.GroceryDAO;
import com.pigeoff.menu.database.GroceryEntity;
import com.pigeoff.menu.database.MenuDatabase;
import com.pigeoff.menu.database.ProductDAO;
import com.pigeoff.menu.database.ProductEntity;
import com.pigeoff.menu.database.RecipeDAO;
import com.pigeoff.menu.database.RecipeEntity;
import com.pigeoff.menu.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WeekModel extends AndroidViewModel {

    public static class StartEnd {
        public long start;
        public long end;

        public StartEnd(long start, long end) {
            this.start = start;
            this.end = end;
        }
    }

    CalendarDAO calendarDAO;
    GroceryDAO groceryDAO;
    ProductDAO productDAO;
    RecipeDAO recipeDAO;

    LiveData<List<CalendarWithRecipe>> items;
    LiveData<List<ProductEntity>> products;
    MutableLiveData<StartEnd> startEnd;

    public WeekModel(StartEnd begin, @NonNull Application application) {
        super(application);

        MenuApplication app = (MenuApplication) application;
        MenuDatabase database = app.database;

        calendarDAO = database.calendarDAO();
        groceryDAO = database.groceryDAO();
        productDAO = database.productDAO();
        recipeDAO = database.recipeDAO();
        startEnd = new MutableLiveData<>(begin);


        items = Transformations.switchMap(startEnd, object -> calendarDAO.select(object.start, object.end));
        products = productDAO.getAll();
    }

    public LiveData<List<CalendarWithRecipe>> getEvents() {
        return items;
    }

    public LiveData<List<ProductEntity>> getProducts() {
        return products;
    }

    public void setStartEnd(StartEnd config) {
        startEnd.setValue(config);
    }

    public void addEvent(long datetime, RecipeEntity recipe, boolean newRecipe) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                if (newRecipe) {
                    recipe.id = recipeDAO.insert(recipe);
                }

                CalendarEntity event = new CalendarEntity();
                event.label = recipe.title;
                event.recipe = recipe.id;
                event.datetime = datetime; // getTimestampAtDay(Calendar, day)
                event.groceriesState = 0;
                calendarDAO.insert(event);
            }
        });
    }


    public void deleteEvent(CalendarWithRecipe item) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                calendarDAO.delete(item.calendar);
                groceryDAO.deleteGroceriesForCalendar(item.calendar.id);
                if (!item.recipe.cookbook) recipeDAO.delete(item.recipe);
            }
        });
    }
    public void addEventToGroceries(
            HashMap<Long, ProductEntity> products,
            List<CalendarWithRecipe> items,
            FragmentActivity context,
            EventToGroceriesCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean success = true;

                for (CalendarWithRecipe i : items) {
                    if (!addToGrocerie(products, i)) {
                        success = false;
                    }
                }

                boolean finalSuccess = success;
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (finalSuccess) callback.onSuccess();
                        else callback.onFailure();
                    }
                });
            }
        }).start();
    }

    public void addEventToGroceries(
            HashMap<Long, ProductEntity> products,
            CalendarWithRecipe item,
            FragmentActivity context,
            EventToGroceriesCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean success = true;

                if (!addToGrocerie(products, item)) {
                    success = false;
                }

                boolean finalSuccess = success;
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (finalSuccess) callback.onSuccess();
                        else callback.onFailure();
                    }
                });
            }
        }).start();
    }

    private boolean addToGrocerie(HashMap<Long, ProductEntity> products, CalendarWithRecipe item) {
        if (groceryDAO.eventAlreadyAdded(item.calendar.id)) return false;

        ArrayList<Ingredient> ingredients = Ingredient.fromJson(products, item.recipe.ingredients);

        for (Ingredient i : ingredients) {
            GroceryEntity product = new GroceryEntity();
            product.checked = false;
            product.ingredientId = i.product.id;
            product.unit = i.unit;
            product.value = i.value;
            product.eventId = item.calendar.id;
            product.recipeId = item.recipe.id;
            product.datetime = item.calendar.datetime;
            product.recipeLabel = item.recipe.title;

            groceryDAO.addGrocery(product);
        }

        return true;
    }

    public interface EventToGroceriesCallback {
        void onSuccess();
        void onFailure();
    }
}
