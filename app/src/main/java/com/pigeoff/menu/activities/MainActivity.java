package com.pigeoff.menu.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

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



    FrameLayout frameLayout;
    BottomNavigationView bottomNavigationView;
    Fragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        frameLayout = findViewById(R.id.frame_layout);
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);

        CalendarFragment calendarFragment = new CalendarFragment();
        RecipeFragment recipeFragment = new RecipeFragment();
        GroceriesFragment groceriesFragment = new GroceriesFragment();

        recipeFragment.addCallback(open ->
                bottomNavigationView.setVisibility(open ? View.GONE : View.VISIBLE)
        );

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.frame_layout, groceriesFragment, FRAGMENT_GROCERIES)
                .hide(calendarFragment)
                .add(R.id.frame_layout, recipeFragment, FRAGMENT_RECIPES)
                .hide(recipeFragment)
                .add(R.id.frame_layout, calendarFragment, FRAGMENT_CALENDAR)
                .commit();

        activeFragment = calendarFragment;

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.item_calendar) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .hide(activeFragment)
                        .show(calendarFragment)
                        .commit();
                activeFragment = calendarFragment;
            } else if (item.getItemId() == R.id.item_recipes) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .hide(activeFragment)
                        .show(recipeFragment)
                        .commit();
                activeFragment = recipeFragment;
            } else if (item.getItemId() == R.id.item_groceries) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .hide(activeFragment)
                        .show(groceriesFragment)
                        .commit();
                activeFragment = groceriesFragment;
            }
            return true;
        });

        bottomNavigationView.setSelectedItemId(R.id.item_calendar);
    }

}