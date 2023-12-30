package com.pigeoff.menu.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pigeoff.menu.R;
import com.pigeoff.menu.fragments.CalendarFragment;
import com.pigeoff.menu.fragments.GroceriesFragment;
import com.pigeoff.menu.fragments.RecipeFragment;

public class MainActivity extends AppCompatActivity {

    private static final String FRAGMENT_CALENDAR = "calendar";
    private static final String FRAGMENT_RECIPES = "recipes";
    private static final String FRAGMENT_GROCERIES = "groceries";
    private static final String ACTIVE_FRAGMENT = "active_fragment";

    int activeTabId = R.id.item_calendar;
    FrameLayout frameLayout;
    BottomNavigationView bottomNavigationView;
    Fragment activeFragment;

    CalendarFragment calendarFragment;
    RecipeFragment recipeFragment;
    GroceriesFragment groceriesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        frameLayout = findViewById(R.id.frame_layout);
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);

        calendarFragment = new CalendarFragment();
        recipeFragment = new RecipeFragment();
        groceriesFragment = new GroceriesFragment();

        recipeFragment.addCallback(open -> bottomNavigationView.setVisibility(open ? View.GONE : View.VISIBLE));

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.frame_layout, groceriesFragment, FRAGMENT_GROCERIES)
                .hide(groceriesFragment)
                .add(R.id.frame_layout, recipeFragment, FRAGMENT_RECIPES)
                .hide(recipeFragment)
                .add(R.id.frame_layout, calendarFragment, FRAGMENT_CALENDAR)
                .commit();

        activeFragment = calendarFragment;

        switchTab(activeTabId);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switchTab(item.getItemId());
            return true;
        });

        bottomNavigationView.setSelectedItemId(R.id.item_calendar);
    }

    private void switchTab(int id) {
        Fragment newFragment;

        if (id == R.id.item_calendar) {
            newFragment = calendarFragment;
        } else if (id == R.id.item_recipes) {
            newFragment = recipeFragment;
        } else {
            newFragment = groceriesFragment;
        }

        getSupportFragmentManager()
                .beginTransaction()
                .hide(activeFragment)
                .show(newFragment)
                .commit();

        activeFragment = newFragment;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ACTIVE_FRAGMENT, bottomNavigationView.getSelectedItemId());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        activeTabId = savedInstanceState.getInt(ACTIVE_FRAGMENT, R.id.item_calendar);
    }
}