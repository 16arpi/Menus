package com.pigeoff.menu.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.pigeoff.menu.MenuApplication;
import com.pigeoff.menu.R;
import com.pigeoff.menu.adapters.IngredientAdapter;
import com.pigeoff.menu.adapters.StepAdapter;
import com.pigeoff.menu.data.Ingredient;
import com.pigeoff.menu.database.MenuDatabase;
import com.pigeoff.menu.database.ProductEntity;
import com.pigeoff.menu.database.RecipeEntity;
import com.pigeoff.menu.fragments.RecipeEditFragment;
import com.pigeoff.menu.util.Constants;
import com.pigeoff.menu.util.Util;

import java.util.ArrayList;
import java.util.HashMap;

public class RecipeActivity extends AppCompatActivity {

    MenuDatabase database;

    RecipeEntity recipe;
    HashMap<Long, ProductEntity> products;

    MaterialCardView cardIngredients;
    MaterialCardView cardSteps;
    TextView textTitle;
    TextView textType;
    RecyclerView recyclerViewIngredients;
    RecyclerView recyclerViewSteps;

    boolean readonly = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        MenuApplication app = (MenuApplication) getApplication();
        database = app.database;

        products = Util.productsToDict(database.productDAO().getAll());

        Intent intent = getIntent();
        long id = intent.getLongExtra(Constants.RECIPE_ID, -1);
        readonly = intent.getBooleanExtra(Constants.RECIPE_READONLY, false);
        if (id < 0) return;

        // Binding
        MaterialToolbar toolbar = findViewById(R.id.top_app_bar);
        cardIngredients = findViewById(R.id.card_ingredients);
        cardSteps = findViewById(R.id.card_steps);
        textTitle = findViewById(R.id.recipe_title);
        textType = findViewById(R.id.recipeType);
        recyclerViewIngredients = findViewById(R.id.recycler_view_ingredients);
        recyclerViewSteps = findViewById(R.id.recycler_view_steps);

        recyclerViewIngredients.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSteps.setLayoutManager(new LinearLayoutManager(this));

        // Action bar
        setSupportActionBar(toolbar);
        setTitle("");

        // Database
        recipe = database.recipeDAO().select(id);

        setupUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!readonly) getMenuInflater().inflate(R.menu.recipe_item_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:{
                finish();
                return true;
            }
            case R.id.item_delete:{
                database.recipeDAO().delete(recipe);

                new MaterialAlertDialogBuilder(this)
                        .setTitle(R.string.recipe_delete_title)
                        .setMessage(R.string.recipe_delete_message)
                        .setNegativeButton(R.string.recipe_delete_cancel, null)
                        .setPositiveButton(R.string.recipe_delete_delete, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent();
                                intent.putExtra(Constants.RECIPE_ID, recipe.id);
                                setResult(Constants.RESULT_DELETE, intent);

                                deleteRecipe(recipe);

                                finish();
                            }
                        })
                        .show();

                return true;
            }
            case R.id.item_edit:{
                openEditDialog();
                return true;
            }
            default:{
                return super.onOptionsItemSelected(item);
            }
        }
    }

    private void setupUI() {
        String[] recipesTypes = Util.getRecipesTypes(this);
        textTitle.setText(recipe.title);
        textType.setText(recipesTypes[recipe.category]);

        ArrayList<Ingredient> ingredients = Ingredient.fromJson(products, recipe.ingredients);
        recyclerViewIngredients.setAdapter(new IngredientAdapter(this, ingredients, false));
        if (ingredients.size() == 0) {
            cardIngredients.setVisibility(View.GONE);
        } else {
            cardIngredients.setVisibility(View.VISIBLE);
        }

        ArrayList<String> steps = Util.listFromJson(recipe.steps);
        recyclerViewSteps.setAdapter(new StepAdapter(this, steps, false));
        if (steps.size() == 0) {
            cardSteps.setVisibility(View.GONE);
        } else {
            cardSteps.setVisibility(View.VISIBLE);
        }
    }

    private void openEditDialog() {
        RecipeEditFragment editFragment = RecipeEditFragment.newInstance(recipe.id);
        editFragment.showFullScreen(getSupportFragmentManager());
        editFragment.setActionListener(new RecipeEditFragment.OnActionListener() {
            @Override
            public void onSubmit(RecipeEntity newRecipe) {
                database.recipeDAO().update(newRecipe);
                recipe = newRecipe;

                database.recipeDAO().update(recipe);

                Intent intent = new Intent();
                intent.putExtra(Constants.RECIPE_ID, recipe.id);
                setResult(Constants.RESULT_EDIT, intent);

                setupUI();
            }
        });
    }

    private void deleteRecipe(RecipeEntity recipe) {
        database.groceryDAO().deleteGroceriesForRecipe(recipe.id);
        database.calendarDAO().deleteForRecipe(recipe.id);
        database.recipeDAO().delete(recipe);
    }

}