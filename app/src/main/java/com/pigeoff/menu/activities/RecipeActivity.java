package com.pigeoff.menu.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.pigeoff.menu.R;
import com.pigeoff.menu.adapters.IngredientAdapter;
import com.pigeoff.menu.adapters.StepAdapter;
import com.pigeoff.menu.data.Ingredient;
import com.pigeoff.menu.database.CalendarWithRecipe;
import com.pigeoff.menu.database.MenuDatabase;
import com.pigeoff.menu.database.ProductEntity;
import com.pigeoff.menu.database.RecipeEntity;
import com.pigeoff.menu.fragments.RecipeEditFragment;
import com.pigeoff.menu.models.EventViewModel;
import com.pigeoff.menu.models.RecipeViewModel;
import com.pigeoff.menu.util.Constants;
import com.pigeoff.menu.util.Util;

import java.util.ArrayList;
import java.util.HashMap;

public class RecipeActivity extends AppCompatActivity {

    MenuDatabase database;

    HashMap<Long, ProductEntity> products;

    MaterialToolbar toolbar;
    MaterialCardView cardPortions;
    Button buttonPortionsMore;
    Button buttonPortionsLess;
    TextView textPortions;
    MaterialCardView cardIngredients;
    MaterialCardView cardSteps;
    TextView textTitle;
    TextView textType;
    RecyclerView recyclerViewIngredients;
    RecyclerView recyclerViewSteps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        Intent intent = getIntent();
        long recipeId = intent.getLongExtra(Constants.RECIPE_ID, -1);
        long calendarId = intent.getLongExtra(Constants.CALENDAR_ID, -1);
        if (recipeId < 0 && calendarId < 0) return;

        // Binding
        toolbar = findViewById(R.id.top_app_bar);
        cardPortions = findViewById(R.id.card_portions);
        buttonPortionsMore = findViewById(R.id.button_more);
        buttonPortionsLess = findViewById(R.id.button_less);
        textPortions = findViewById(R.id.text_portions);
        cardIngredients = findViewById(R.id.card_ingredients);
        cardSteps = findViewById(R.id.card_steps);
        textTitle = findViewById(R.id.recipe_title);
        textType = findViewById(R.id.recipeType);
        recyclerViewIngredients = findViewById(R.id.recycler_view_ingredients);
        recyclerViewSteps = findViewById(R.id.recycler_view_steps);

        recyclerViewIngredients.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSteps.setLayoutManager(new LinearLayoutManager(this));

        // Action bar
        toolbar.setTitle("");
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });

        // Database
        if (recipeId > 0) {
            RecipeViewModel model = new RecipeViewModel(recipeId, getApplication());

            toolbar.inflateMenu(R.menu.recipe_item_menu);

            model.getItem().observe(this, object -> {
                if (object.a == null || object.b == null) return;

                products = Util.productsToDict(object.b);
                RecipeEntity recipe = object.a;
                setupRecipeUI(recipe, recipe.portions);

                toolbar.setOnMenuItemClickListener(menu -> {
                    if (menu.getItemId() == R.id.item_delete) {
                        new MaterialAlertDialogBuilder(this)
                                .setTitle(R.string.recipe_delete_title)
                                .setMessage(R.string.recipe_delete_message)
                                .setNegativeButton(R.string.recipe_delete_cancel, null)
                                .setPositiveButton(R.string.recipe_delete_delete, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        model.deleteItem(recipe);
                                        finish();
                                    }
                                })
                                .show();
                    } else if (menu.getItemId() == R.id.item_edit) {
                        openEditDialog(model, recipe.id);
                    }

                    return true;
                });
            });

        } else if (calendarId > 0) {
            EventViewModel model = new EventViewModel(calendarId, getApplication());

            model.getItem().observe(this, object -> {
                if (object.a == null || object.b == null) return;

                products = Util.productsToDict(object.b);
                CalendarWithRecipe item = object.a;

                setupCalendarUI(model, item);
            });
        }
    }

    private void setupRecipeUI(RecipeEntity item, int customPortions) {
        if (item == null) return;

        String[] recipesTypes = Util.getRecipesTypes(this);
        textTitle.setText(item.title);
        textType.setText(recipesTypes[item.category]);

        ArrayList<Ingredient> ingredients = Ingredient.fromJson(products, item.ingredients, item.portions, customPortions);
        recyclerViewIngredients.setAdapter(new IngredientAdapter(this, ingredients, false));
        if (ingredients.size() == 0) {
            cardIngredients.setVisibility(View.GONE);
        } else {
            cardIngredients.setVisibility(View.VISIBLE);
        }

        ArrayList<String> steps = Util.listFromJson(item.steps);
        recyclerViewSteps.setAdapter(new StepAdapter(this, steps, false));
        if (steps.size() == 0) {
            cardSteps.setVisibility(View.GONE);
        } else {
            cardSteps.setVisibility(View.VISIBLE);
        }


    }

    private void setupCalendarUI(EventViewModel model, CalendarWithRecipe item) {
        if (item == null) return;

        cardPortions.setVisibility(View.VISIBLE);
        textPortions.setText(String.valueOf(item.calendar.portions));
        buttonPortionsMore.setOnClickListener(v -> {
            item.calendar.portions += 1;
            model.update(item.calendar);
        });
        buttonPortionsLess.setOnClickListener(v -> {
            item.calendar.portions = --item.calendar.portions < 0 ? 0 : item.calendar.portions;
            model.update(item.calendar);
        });

        setupRecipeUI(item.recipe, item.calendar.portions);
    }

    private void openEditDialog(RecipeViewModel model, long id) {
        RecipeEditFragment editFragment = RecipeEditFragment.newInstance(id);
        editFragment.showFullScreen(getSupportFragmentManager());
        editFragment.setActionListener(new RecipeEditFragment.OnActionListener() {
            @Override
            public void onSubmit(RecipeEntity newRecipe) {
                model.updateItem(newRecipe);
            }
        });
    }

}