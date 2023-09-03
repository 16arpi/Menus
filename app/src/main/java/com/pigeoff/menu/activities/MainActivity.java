package com.pigeoff.menu.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.pigeoff.menu.R;
import com.pigeoff.menu.fragments.CalendarFragment;
import com.pigeoff.menu.fragments.GroceriesFragment;
import com.pigeoff.menu.fragments.RecipeFragment;

public class MainActivity extends AppCompatActivity {

    private static int TAB_CALENDAR = 0;
    private static int TAB_RECIPES = 1;
    private static int TAB_GROCERIES = 2;



    FrameLayout frameLayout;
    BottomNavigationView bottomNavigationView;
    Fragment fragments[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        frameLayout = findViewById(R.id.frame_layout);
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);

        fragments = new Fragment[] {
          new CalendarFragment(),
          new RecipeFragment(),
          new GroceriesFragment()
        };

        setFragment(TAB_CALENDAR);
        bottomNavigationView.setSelectedItemId(R.id.item_calendar);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_calendar:
                        setFragment(TAB_CALENDAR);
                        break;
                    case R.id.item_recipes:
                        setFragment(TAB_RECIPES);
                        break;
                    case R.id.item_groceries:
                        setFragment(TAB_GROCERIES);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    private void setFragment(int tab) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, fragments[tab])
                .commit();
    }
}