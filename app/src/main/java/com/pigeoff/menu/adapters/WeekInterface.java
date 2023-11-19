package com.pigeoff.menu.adapters;

import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.pigeoff.menu.R;
import com.pigeoff.menu.activities.RecipeActivity;
import com.pigeoff.menu.database.CalendarWithRecipe;
import com.pigeoff.menu.database.ProductEntity;
import com.pigeoff.menu.database.RecipeEntity;
import com.pigeoff.menu.fragments.RecipeEditFragment;
import com.pigeoff.menu.fragments.RecipePickerFragment;
import com.pigeoff.menu.models.WeekModel;
import com.pigeoff.menu.util.Constants;
import com.pigeoff.menu.util.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class WeekInterface {

    private final WeekModel model;
    private final ImageButton[] imageButtons;
    private final ImageButton[] groceriesButtons;
    private final CollapsingToolbarLayout toolbarLayout;
    private final EventAdapter[] adapters;
    private final List<CalendarWithRecipe>[] items;

    private Calendar calendar;
    private HashMap<Long, ProductEntity> products;

    public WeekInterface(
            FragmentActivity context,
            RecyclerView[] recyclerViews,
            ImageButton[] imageButtons,
            ImageButton[] groceriesButtons,
            MaterialToolbar toolbar,
            CollapsingToolbarLayout toolbarLayout
    ) {
        this.imageButtons = imageButtons;
        this.groceriesButtons = groceriesButtons;
        this.toolbarLayout = toolbarLayout;

        model = new WeekModel(getStartEnd(Calendar.getInstance()), context.getApplication());

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

            adapters[day].setOnAdapterAction(new OnAdapterAction<CalendarWithRecipe>() {
                @Override
                public void onItemClick(CalendarWithRecipe item) {
                    Intent intent = new Intent(context, RecipeActivity.class);
                    intent.putExtra(Constants.CALENDAR_ID, item.calendar.id);
                    context.startActivity(intent);
                }

                @Override
                public void onItemClick(CalendarWithRecipe item, int action) {
                    if (action == OnAdapterAction.ACTION_GROCERY) {
                        model.addEventToGroceries(products, item, context, new WeekModel.EventToGroceriesCallback() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(
                                        context,
                                        context.getString(R.string.calendar_product_added),
                                        Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure() {
                                Toast.makeText(
                                        context,
                                        context.getString(R.string.calendar_product_added_error),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onItemLongClick(CalendarWithRecipe item, int position) {
                    model.deleteEvent(item);
                }
            });
            recyclerView.setAdapter(adapters[day]);

            int thisDay = day;
            imageButtons[day].setOnClickListener(view -> {
                RecipePickerFragment picker = new RecipePickerFragment();
                picker.show(context.getSupportFragmentManager(), Constants.EDIT_FRAGMENT_TAG);
                picker.setOnRecipePicked(new RecipePickerFragment.OnRecipePicked() {
                    @Override
                    public void onRecipePicked(RecipeEntity recipe) {
                        picker.dismiss();
                        long moment = getTimestampOfDay(calendar, thisDay);
                        model.addEvent(moment, recipe, false);
                    }
                });
            });

            imageButtons[day].setOnLongClickListener(view -> {
                RecipeEditFragment editFragment = RecipeEditFragment.newInstance();
                editFragment.showFullScreen(context.getSupportFragmentManager());
                editFragment.setActionListener(new RecipeEditFragment.OnActionListener() {
                    @Override
                    public void onSubmit(RecipeEntity recipe) {
                        editFragment.dismiss();
                        recipe.cookbook = false;
                        long moment = getTimestampOfDay(calendar, thisDay);
                        model.addEvent(moment, recipe, true);
                    }
                });
                return true;
            });

            groceriesButtons[day].setOnClickListener(view -> {
                model.addEventToGroceries(products, items[thisDay], context, new WeekModel.EventToGroceriesCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(
                                context,
                                context.getString(R.string.calendar_product_added_plural),
                                Toast.LENGTH_SHORT
                        ).show();
                    }

                    @Override
                    public void onFailure() {
                        Toast.makeText(
                                context,
                                context.getString(R.string.calendar_product_added_plural),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
            });

        }

        toolbar.setOnMenuItemClickListener(item -> {
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
        });

        setThisWeek();

        model.getEvents().observe(context, calendarWithRecipes -> {
            updateItems(calendarWithRecipes);
            setupInterface();
        });

        model.getProducts().observe(context, productEntities ->
                products = Util.productsToDict(productEntities));
    }

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

        model.setStartEnd(getStartEnd(calendar));
    }

    public void setLastWeek() {
        System.out.println("Last week");
        calendar.add(Calendar.WEEK_OF_YEAR, -1);

        model.setStartEnd(getStartEnd(calendar));
    }

    public void setNextWeek() {
        System.out.println("Next week");
        calendar.add(Calendar.WEEK_OF_YEAR, 1);

        model.setStartEnd(getStartEnd(calendar));
    }



    // PRIVATE METHODS

    private WeekModel.StartEnd getStartEnd(Calendar cal) {
        Calendar c = (Calendar) cal.clone();
        long start = c.getTimeInMillis();
        c.add(Calendar.WEEK_OF_YEAR, 1);
        long end = c.getTimeInMillis();
        return new WeekModel.StartEnd(start, end);
    }

    private void updateItems(List<CalendarWithRecipe> data) {

        Calendar cal = (Calendar) calendar.clone();
        for (int i = 0; i < 7; ++i) {
            Calendar today = (Calendar) cal.clone();
            long start = today.getTimeInMillis();
            today.add(Calendar.DAY_OF_YEAR, 1);
            long end = today.getTimeInMillis();

            //items[i] = database.calendarDAO().select(start, end);
            items[i] = new ArrayList<>();
            for (CalendarWithRecipe d : data) {
                if (d.calendar.datetime >= start && d.calendar.datetime < end)  items[i].add(d);
            }

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

    private String formatWeekDate() {
        SimpleDateFormat format = new SimpleDateFormat("E dd", Locale.FRANCE);
        Calendar cal = (Calendar) calendar.clone();
        String start = format.format(cal.getTime());
        cal.add(Calendar.DAY_OF_YEAR, 6);
        String end = format.format(cal.getTime());
        return start + " - " + end;
    }
}
