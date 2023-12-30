package com.pigeoff.menu.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.pigeoff.menu.R;
import com.pigeoff.menu.adapters.IngredientAdapter;
import com.pigeoff.menu.adapters.StepAdapter;
import com.pigeoff.menu.data.Ingredient;
import com.pigeoff.menu.database.CalendarWithRecipe;
import com.pigeoff.menu.database.ProductEntity;
import com.pigeoff.menu.database.RecipeEntity;
import com.pigeoff.menu.fragments.RecipeEditFragment;
import com.pigeoff.menu.models.EventViewModel;
import com.pigeoff.menu.models.RecipeViewModel;
import com.pigeoff.menu.util.Constants;
import com.pigeoff.menu.util.ImportExport;
import com.pigeoff.menu.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class RecipeActivity extends AppCompatActivity {
    HashMap<Long, ProductEntity> products;
    ImportExport<FragmentActivity> importExport;
    MaterialToolbar toolbar;
    MaterialCardView cardPortions;
    ImageView imagePreview;
    Button buttonPortionsMore;
    Button buttonPortionsLess;
    TextView textPortions;
    LinearLayout cardIngredients;
    LinearLayout cardSteps;
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
        imagePreview = findViewById(R.id.image_view);
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
        toolbar.setNavigationOnClickListener(v -> finish());

        // Database
        if (recipeId > 0) {
            RecipeViewModel model = new RecipeViewModel(recipeId, getApplication());
            importExport = new ImportExport<>(this, model);

            toolbar.inflateMenu(R.menu.recipe_item_menu);

            model.getRecipe().observe(this, object -> {
                if (object.a == null || object.b == null) return;

                List<ProductEntity> allProducts = object.b;
                products = Util.productsToDict(allProducts);
                RecipeEntity recipe = object.a;
                setupRecipeUI(recipe, recipe.portions);

                toolbar.setOnMenuItemClickListener(menu -> {
                    if (menu.getItemId() == R.id.item_delete) {
                        new MaterialAlertDialogBuilder(this)
                                .setTitle(R.string.recipe_delete_title)
                                .setMessage(R.string.recipe_delete_message)
                                .setNegativeButton(R.string.recipe_delete_cancel, null)
                                .setPositiveButton(R.string.recipe_delete_delete, (dialogInterface, i) -> {
                                    model.deleteItem(recipe);
                                    finish();
                                })
                                .show();
                    } else if (menu.getItemId() == R.id.item_edit) {
                        openEditDialog(model, recipe.id);
                    }
                    else if (menu.getItemId() == R.id.item_export) {
                        importExport.export(allProducts, Collections.singletonList(recipe));
                    } else if (menu.getItemId() == R.id.item_share) {
                        shareRecipe(products, recipe);
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

    @Override
    protected void onResume() {
        super.onResume();
        Util.hideKeyboard(this);
    }

    private void setupRecipeUI(RecipeEntity item, int customPortions) {
        if (item == null) return;

        String[] recipesTypes = Util.getRecipesTypes(this);
        textTitle.setText(item.title);
        textType.setText(recipesTypes[item.category]);

        // Preview
        Util.injectImage(this, item.picturePath, imagePreview);

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
        editFragment.setActionListener(model::updateItem);
    }

    private void shareRecipe(HashMap<Long, ProductEntity> products, RecipeEntity recipe) {
        new Thread(() -> {
            StringBuilder text = new StringBuilder();

            // Title
            text.append(recipe.title);
            text.append("\n");

            // Ingredients
            text.append(String.format("\n%s\n", getString(R.string.label_ingredient)));
            List<Ingredient> ingredients = Ingredient.fromJson(products, recipe.ingredients);
            for (Ingredient s : ingredients) text.append(String.format("· %s \n", s.format()));

            // Preparation
            text.append(String.format("\n%s\n", getString(R.string.label_steps)));
            List<String> steps = Util.listFromJson(recipe.steps);
            for (String s : steps) text.append(String.format("· %s \n", s));

            String finalText = text.toString();
            runOnUiThread(() -> {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, finalText);
                intent.setType("text/plain");

                Intent chooser = Intent.createChooser(intent, getString(R.string.label_share));
                startActivity(chooser);
            });
        }).start();
    }


}