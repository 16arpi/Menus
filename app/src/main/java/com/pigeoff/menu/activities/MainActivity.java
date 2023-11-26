package com.pigeoff.menu.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.pigeoff.menu.R;
import com.pigeoff.menu.fragments.CalendarFragment;
import com.pigeoff.menu.fragments.GroceriesFragment;
import com.pigeoff.menu.fragments.RecipeFragment;
import com.pigeoff.menu.util.OnSearchCallback;

public class MainActivity extends AppCompatActivity {

    private static final int TAB_CALENDAR = 0;
    private static final int TAB_RECIPES = 1;
    private static final int TAB_GROCERIES = 2;



    FrameLayout frameLayout;
    BottomNavigationView bottomNavigationView;
    Fragment[] fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        frameLayout = findViewById(R.id.frame_layout);
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);

        fragments = new Fragment[] {
          new CalendarFragment(),
          new RecipeFragment(new OnSearchCallback() {
              @Override
              public void onSearchOpen() {
                  bottomNavigationView.setVisibility(View.GONE);
              }

              @Override
              public void onSearchClose() {
                  bottomNavigationView.setVisibility(View.VISIBLE);
              }
          }),
          new GroceriesFragment()
        };

        setFragment(TAB_CALENDAR);
        bottomNavigationView.setSelectedItemId(R.id.item_calendar);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.item_calendar) {
                setFragment(TAB_CALENDAR);
            } else if (item.getItemId() == R.id.item_recipes) {
                setFragment(TAB_RECIPES);
            } else if (item.getItemId() == R.id.item_groceries) {
                setFragment(TAB_GROCERIES);
            }
            return true;
        });
    }

    private void setFragment(int tab) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, fragments[tab])
                .commit();
    }

    private void showHideBottomNavigation(boolean show) {

    }
}