package com.pigeoff.menu.models;

import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.pigeoff.menu.MenuApplication;
import com.pigeoff.menu.R;
import com.pigeoff.menu.activities.RecipeActivity;
import com.pigeoff.menu.adapters.EventAdapter;
import com.pigeoff.menu.adapters.OnAdapterAction;
import com.pigeoff.menu.data.Ingredient;
import com.pigeoff.menu.database.CalendarEntity;
import com.pigeoff.menu.database.MenuDatabase;
import com.pigeoff.menu.database.GroceryEntity;
import com.pigeoff.menu.database.ProductEntity;
import com.pigeoff.menu.database.RecipeEntity;
import com.pigeoff.menu.fragments.RecipeEditFragment;
import com.pigeoff.menu.fragments.RecipePickerFragment;
import com.pigeoff.menu.util.Constants;
import com.pigeoff.menu.util.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class WeekModel {

    public static class Days {
        public static int MONDAY = 0;
        public static int TUESDAY = 1;
        public static int WEDNESDAY = 2;
        public static int THURSDAY = 3;
        public static int FRIDAY = 4;
        public static int SATURDAY = 5;
        public static int SUNDAY = 6;
    }

    private FragmentActivity context;
    private MenuDatabase database;
    private RecyclerView[] recyclerViews;
    private ImageButton[] imageButtons;
    private ImageButton[] groceriesButtons;
    private MaterialToolbar toolbar;
    private CollapsingToolbarLayout toolbarLayout;
    private EventAdapter[] adapters;
    private List<CalendarEntity>[] items;

    private Calendar calendar;
    private HashMap<Long, ProductEntity> products;

    public WeekModel(
            FragmentActivity context,
            RecyclerView[] recyclerViews,
            ImageButton[] imageButtons,
            ImageButton[] groceriesButtons,
            MaterialToolbar toolbar,
            CollapsingToolbarLayout toolbarLayout
    ) {
        this.context = context;
        this.recyclerViews = recyclerViews;
        this.imageButtons = imageButtons;
        this.groceriesButtons = groceriesButtons;
        this.toolbar = toolbar;
        this.toolbarLayout = toolbarLayout;

        MenuApplication app = (MenuApplication) context.getApplication();
        database = app.database;

        products = Util.productsToDict(database.productDAO().getAll());

        items = new List[] {
                null,
                null,
                null,
                null,
                null,
                null,
                null,
        };

        adapters = new EventAdapter[] {
                new EventAdapter(context, new ArrayList<>()),
                new EventAdapter(context, new ArrayList<>()),
                new EventAdapter(context, new ArrayList<>()),
                new EventAdapter(context, new ArrayList<>()),
                new EventAdapter(context, new ArrayList<>()),
                new EventAdapter(context, new ArrayList<>()),
                new EventAdapter(context, new ArrayList<>()),
        };

        // Setting up recipe edit

        // Setting up recyclerView and imageButton
        for (int day = 0; day < 7; ++day) {
            RecyclerView recyclerView = recyclerViews[day];
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            int finalDay = day;
            adapters[day].setOnAdapterAction(new OnAdapterAction<CalendarEntity>() {
                @Override
                public void onItemClick(CalendarEntity item) {
                    Intent intent = new Intent(context, RecipeActivity.class);
                    intent.putExtra(Constants.RECIPE_ID, item.recipe);
                    intent.putExtra(Constants.RECIPE_READONLY, true);
                    context.startActivity(intent);
                }

                @Override
                public void onItemClick(CalendarEntity item, int action) {
                    if (action == OnAdapterAction.ACTION_GROCERY) {
                        addIngredientsToGroceries(item);
                        Snackbar.make(
                                context.findViewById(android.R.id.content),
                                context.getString(R.string.calendar_product_added),
                                Snackbar.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onItemLongClick(CalendarEntity item, int position) {
                    database.calendarDAO().delete(item);
                    database.groceryDAO().deleteGroceriesForCalendar(item.id);
                    RecipeEntity recipe = database.recipeDAO().select(item.recipe);
                    if (!recipe.cookbook) database.recipeDAO().delete(recipe);
                    adapters[finalDay].deleteItem(position);
                }
            });
            recyclerView.setAdapter(adapters[day]);

            int thisDay = day;
            imageButtons[day].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RecipePickerFragment picker = new RecipePickerFragment();
                    picker.show(context.getSupportFragmentManager(), Constants.EDIT_FRAGMENT_TAG);
                    picker.setOnRecipePicked(new RecipePickerFragment.OnRecipePicked() {
                        @Override
                        public void onRecipePicked(RecipeEntity recipe) {
                            picker.dismiss();
                            addRecipeAtDate(recipe, thisDay);
                        }
                    });
                }
            });

            imageButtons[day].setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    RecipeEditFragment editFragment = RecipeEditFragment.newInstance();
                    editFragment.showFullScreen(context.getSupportFragmentManager());
                    editFragment.setActionListener(new RecipeEditFragment.OnActionListener() {
                        @Override
                        public void onSubmit(RecipeEntity recipe) {
                            editFragment.dismiss();
                            recipe.cookbook = false;
                            recipe.id = database.recipeDAO().insert(recipe);
                            addRecipeAtDate(recipe, thisDay);
                        }
                    });
                    return true;
                }
            });

            groceriesButtons[day].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addDayToGroceries(thisDay);
                    /*
                    Snackbar.make(
                            context.findViewById(R.id.top_app_bar),
                            context.getString(R.string.calendar_product_added_plural),
                            Snackbar.LENGTH_SHORT).show();
                    */
                    Toast.makeText(
                            context,
                            context.getString(R.string.calendar_product_added_plural),
                            Toast.LENGTH_SHORT
                    ).show();
                }
            });

        }

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case (R.id.item_back):{
                        setLastWeek();
                        break;
                    }
                    case (R.id.item_forward):{
                        setNextWeek();
                        break;
                    }
                    case (R.id.item_today):{
                        setThisWeek();
                        break;
                    }
                }
                return true;
            }
        });

        setThisWeek();
    }

    // PUBLIC METHODS

    public void setThisWeek() {
        System.out.println("This week");

        // Setting week timestamp
        calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.clear(Calendar.MINUTE);
        calendar.clear(Calendar.SECOND);
        calendar.clear(Calendar.MILLISECOND);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());

        updateItems();
        setupInterface();
    }

    public void setLastWeek() {
        System.out.println("Last week");
        calendar.add(Calendar.WEEK_OF_YEAR, -1);
        updateItems();
        setupInterface();
    }

    public void setNextWeek() {
        System.out.println("Next week");
        calendar.add(Calendar.WEEK_OF_YEAR, 1);
        updateItems();
        setupInterface();
    }



    // PRIVATE METHODS

    private void updateItems() {
        Calendar cal = (Calendar) calendar.clone();
        for (int i = 0; i < 7; ++i) {
            Calendar today = (Calendar) cal.clone();
            long start = today.getTimeInMillis();
            today.add(Calendar.DAY_OF_YEAR, 1);
            long end = today.getTimeInMillis();
            items[i] = database.calendarDAO().select(start, end);

            cal.add(Calendar.DAY_OF_YEAR, 1);
        }
    }

    private void setupInterface() {
        for (int i = 0; i < 7; ++i) {
            adapters[i].updateItems(new ArrayList<>(items[i]));
        }

        toolbarLayout.setTitle(formatWeekDate());

        for (int day = 0; day < 7; ++day) {
            long thisDay = getTimestampOfDayMidnight(calendar, day);
            long nowDay = getTodayTimestamp();
            if (thisDay < nowDay) {
                System.out.println("Hour past");
                imageButtons[day].setVisibility(View.GONE);
                groceriesButtons[day].setVisibility(View.GONE);
            } else {
                System.out.println("Hour future");
                imageButtons[day].setVisibility(View.VISIBLE);
                groceriesButtons[day].setVisibility(View.VISIBLE);
            }
            System.out.println(thisDay);
            System.out.println(nowDay);
        }
    }

    private long getTodayTimestamp() {
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.add(Calendar.DAY_OF_WEEK, -1);
        return cal.getTimeInMillis();
    }

    private long getTimestampOfDay(Calendar calendar, int day) {
        Calendar cal = (Calendar) calendar.clone();
        cal.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek() + day);
        return cal.getTimeInMillis();
    }

    private long getTimestampOfDayMidnight(Calendar calendar, int day) {
        Calendar cal = (Calendar) calendar.clone();
        cal.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek() + day);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        return cal.getTimeInMillis();
    }

    private void addRecipeAtDate(RecipeEntity recipe, int day) {
        CalendarEntity event = new CalendarEntity();
        event.label = recipe.title;
        event.recipe = recipe.id;
        event.datetime = getTimestampOfDay(calendar, day);
        event.groceriesState = 0;
        database.calendarDAO().insert(event);

        updateItems();
        setupInterface();
    }

    private void addDayToGroceries(int day) {
        for (CalendarEntity i : items[day]) {
            addIngredientsToGroceries(i);
        }
    }

    private void addIngredientsToGroceries(CalendarEntity item) {
        RecipeEntity recipe = database.recipeDAO().select(item.recipe);
        ArrayList<Ingredient> ingredients = Ingredient.fromJson(products, recipe.ingredients);

        for (Ingredient i : ingredients) {
            GroceryEntity product = new GroceryEntity();
            product.checked = false;
            product.ingredientId = i.product.id;
            product.unit = i.unit;
            product.value = i.value;
            product.eventId = item.id;
            product.recipeId = item.recipe;
            product.datetime = item.datetime;
            product.recipeLabel = recipe.title;

            database.groceryDAO().addGrocery(product);
        }
    }

    private String formatWeekDate() {
        SimpleDateFormat format = new SimpleDateFormat("E dd", Locale.FRANCE);
        Calendar cal = (Calendar) calendar.clone();
        String start = format.format(cal.getTime());
        cal.add(Calendar.DAY_OF_YEAR, 6);
        String end = format.format(cal.getTime());
        return start + " - " + end;
    }
}
