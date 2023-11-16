package com.pigeoff.menu.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
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
import com.pigeoff.menu.models.CombinedLiveData;
import com.pigeoff.menu.models.RecipeViewModel;
import com.pigeoff.menu.util.Constants;
import com.pigeoff.menu.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecipeActivity extends AppCompatActivity {

    MenuDatabase database;
    RecipeViewModel model;

    RecipeEntity recipe;

    HashMap<Long, ProductEntity> products;

    MaterialCardView cardIngredients;
    MaterialCardView cardSteps;
    TextView textTitle;
    TextView textType;
    RecyclerView recyclerViewIngredients;
    RecyclerView recyclerViewSteps;

    boolean readonly = false;
    long id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        Intent intent = getIntent();
        id = intent.getLongExtra(Constants.RECIPE_ID, -1);
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
        model = new RecipeViewModel(id, getApplication());

        model.getItem().observe(this, object -> {
            if (object.a == null || object.b == null) return;

            products = Util.productsToDict(object.b);
            recipe = object.a;
            id = recipe.id;
            setupUI(recipe);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!readonly) getMenuInflater().inflate(R.menu.recipe_item_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.item_delete) {
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
        } else if (item.getItemId() == R.id.item_edit) {
            openEditDialog(id);
        }

        return true;
    }

    private void setupUI(RecipeEntity item) {
        if (item == null) return;

        String[] recipesTypes = Util.getRecipesTypes(this);
        textTitle.setText(item.title);
        textType.setText(recipesTypes[item.category]);

        ArrayList<Ingredient> ingredients = Ingredient.fromJson(products, item.ingredients);
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

    private void openEditDialog(long id) {
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