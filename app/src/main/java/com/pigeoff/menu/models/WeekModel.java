package com.pigeoff.menu.models;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
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
        AsyncTask.execute(() -> {
            if (newRecipe) {
                recipe.id = recipeDAO.insert(recipe);
            }

            CalendarEntity event = new CalendarEntity();
            event.label = recipe.title;
            event.portions = recipe.portions;
            event.recipe = recipe.id;
            event.datetime = datetime;
            calendarDAO.insert(event);
        });
    }


    public void deleteEvent(CalendarWithRecipe item) {
        AsyncTask.execute(() -> {
            calendarDAO.delete(item.calendar);
            groceryDAO.deleteGroceriesForCalendar(item.calendar.id);
            if (!item.recipe.cookbook) recipeDAO.delete(item.recipe);
        });
    }

    public void addToGrocerie(List<GroceryEntity> items) {
        AsyncTask.execute(() -> {
            for (GroceryEntity item : items) {
                groceryDAO.deleteGroceriesForCalendar(item.ingredientId, item.eventId);
                groceryDAO.addGrocery(item);
            }
        });
    }

    public List<GroceryEntity> prepareGroceries(HashMap<Long, ProductEntity> products, List<CalendarWithRecipe> items) {
        List<GroceryEntity> finalItems = new ArrayList<>();
        for (CalendarWithRecipe r : items) {
            ArrayList<Ingredient> ingredients = Ingredient.fromJson(
                    products,
                    r.recipe.ingredients,
                    r.recipe.portions,
                    r.calendar.portions);

            for (Ingredient i : ingredients) {

                GroceryEntity shopping = new GroceryEntity();
                shopping.checked = false;
                shopping.ingredientId = i.product.id;
                shopping.quantity = i.quantity;
                shopping.eventId = r.calendar.id;
                shopping.recipeId = r.recipe.id;
                shopping.datetime = r.calendar.datetime;
                shopping.recipeLabel = r.recipe.title;

                finalItems.add(shopping);
            }
        }
        return finalItems;
    }
}
