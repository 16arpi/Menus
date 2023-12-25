package com.pigeoff.menu.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.pigeoff.menu.R;
import com.pigeoff.menu.adapters.IngredientsEditAdapter;
import com.pigeoff.menu.adapters.StepEditAdapter;
import com.pigeoff.menu.data.Ingredient;
import com.pigeoff.menu.database.RecipeEntity;
import com.pigeoff.menu.fragments.ProductFragment;
import com.pigeoff.menu.models.RecipesViewModel;
import com.pigeoff.menu.util.BackgroundTask;
import com.pigeoff.menu.util.Constants;
import com.pigeoff.menu.util.RecipeInternet;
import com.pigeoff.menu.util.Util;

import java.util.Arrays;
import java.util.List;

public class WizardActivity extends AppCompatActivity {
    ViewFlipper flipper;
    TextInputLayout editUrl;
    MaterialButton buttonUrl;
    TextInputEditText editTitle;
    AutoCompleteTextView editType;
    TextInputEditText editPortions;
    RecyclerView recyclerViewIngredients;
    RecyclerView recyclerViewSteps;
    MaterialButton buttonAddIngredient;
    MaterialButton buttonAddStep;

    StepEditAdapter stepsAdapter;
    IngredientsEditAdapter ingredientsAdapter;

    MaterialButton navigationInfoCancel;
    MaterialButton navigationInfoNext;
    MaterialButton navigationIngredientsBack;
    MaterialButton navigationIngredientsNext;
    MaterialButton navigationStepsBack;
    MaterialButton navigationStepsSubmit;

    // private objects

    private RecipeEntity recipe;
    private RecipesViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wizard);

        AppCompatActivity activity = this;

        recipe = new RecipeEntity();
        model = new RecipesViewModel(getApplication());

        flipper = findViewById(R.id.view_flipper);
        editUrl = findViewById(R.id.edit_url);
        buttonUrl = findViewById(R.id.button_url);
        editTitle = findViewById(R.id.edit_title);
        editType = findViewById(R.id.edit_type);
        editPortions = findViewById(R.id.edit_portions);
        recyclerViewIngredients = findViewById(R.id.recycler_view_ingredients);
        buttonAddIngredient = findViewById(R.id.button_ingredients_submit);
        recyclerViewSteps = findViewById(R.id.recycler_view_steps);
        buttonAddStep = findViewById(R.id.button_steps_submit);

        navigationInfoCancel = findViewById(R.id.button_info_cancel);
        navigationInfoNext = findViewById(R.id.button_info_next);
        navigationIngredientsBack = findViewById(R.id.button_ingredients_prev);
        navigationStepsBack = findViewById(R.id.button_steps_prev);
        navigationStepsSubmit = findViewById(R.id.button_steps_next);
        navigationIngredientsNext = findViewById(R.id.button_ingredients_next);

        flipper.setAutoStart(false);

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(R.string.wizard_loading_label);

        BackgroundTask<RecipeInternet> fetchRecipe =
                new BackgroundTask<>(
                        activity,
                        () -> new RecipeInternet(String.valueOf(editUrl.getEditText().getText())))
                .addPreProcessListener(progressDialog::show)
                .addSuccessListener(object -> {
                    // Handling products
                    new BackgroundTask<>(activity, () -> model.ingredientParserToEntities(object.recipe.ingredients))
                            .addSuccessListener(ingrs -> {
                        progressDialog.dismiss();
                        updateFields(object.recipe, ingrs);
                        flipper.showNext();
                    }).start();
                }).addFailureListener(exception -> {
                    progressDialog.dismiss();
                    editUrl.setError(getString(R.string.wizard_error_url));
                });

        // Setting up recycler views

        recyclerViewIngredients.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSteps.setLayoutManager(new LinearLayoutManager(this));

        buttonAddStep.setEnabled(true);
        buttonAddIngredient.setEnabled(true);

        // Handling button callbacks

        navigationInfoNext.setOnClickListener(v -> flipper.showNext());
        navigationInfoCancel.setOnClickListener(v -> flipper.showPrevious());
        navigationIngredientsBack.setOnClickListener(v -> flipper.showPrevious());
        navigationStepsBack.setOnClickListener(v -> flipper.showPrevious());

        // Starting point

        buttonUrl.setOnClickListener(v -> {
            String url = String.valueOf(editUrl.getEditText().getText());
            if (url.isEmpty()) return;
            fetchRecipe.start();
        });

        // Handling extra intent

        Intent intent = getIntent();
        String text = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (text != null) {
            String url = Util.findURLinString(text);

            if (!url.isEmpty()) {
                editUrl.getEditText().setText(url);
                fetchRecipe.start();
            }
        }

    }

    private void updateFields(RecipeInternet.Model model, List<Ingredient> ingrs) {
        editTitle.setText(model.title);
        editPortions.setText(String.valueOf(model.portions));
        Util.selectRecipeTypeAutoCompleteItem(editType, recipe.category);

        // Handling recycler view and adapters
        ingredientsAdapter = new IngredientsEditAdapter(this, ingrs);
        stepsAdapter = new StepEditAdapter(this, model.steps, true);

        recyclerViewIngredients.setAdapter(ingredientsAdapter);
        recyclerViewSteps.setAdapter(stepsAdapter);

        ingredientsAdapter.addLabelClickListener((item, pos) -> {
            Ingredient ingr = ingredientsAdapter.getIngredients().get(pos);
            String query = ingr.product.id < 0 ? ingr.product.label : "";
            ProductFragment productFragment = ProductFragment.newInstance(
                    false,
                    true,
                    Constants.SECTION_EMPTY,
                    query);

            productFragment.addProductActionListener(it -> {
                item.product = it;
                ingredientsAdapter.updateItem(item, pos);
                Util.clearFocus(this);
            });

            productFragment.show(getSupportFragmentManager(), "edit_product");
        });

        buttonAddIngredient.setOnClickListener(v -> {
            ProductFragment productFragment = ProductFragment.newInstance(false, true, Constants.SECTION_EMPTY);
            productFragment.addProductActionListener(item -> {
                Ingredient ingredient = new Ingredient(
                        item,
                        ""
                );
                ingredientsAdapter.addItem(ingredient);
                Util.clearFocus(this);
            });
            productFragment.show(getSupportFragmentManager(), "edit_product");
        });

        buttonAddStep.setOnClickListener(v -> stepsAdapter.addItem(""));

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int from = viewHolder.getAdapterPosition();
                int to = target.getAdapterPosition();
                stepsAdapter.switchItems(from, to);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        }).attachToRecyclerView(recyclerViewSteps);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int from = viewHolder.getAdapterPosition();
                int to = target.getAdapterPosition();
                ingredientsAdapter.switchItems(from, to);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        }).attachToRecyclerView(recyclerViewIngredients);

        // Checking

        navigationIngredientsNext.setOnClickListener(v -> {
            for (Ingredient i : ingredientsAdapter.getIngredients()) {
                if (i.product.id < 0) {
                    Toast.makeText(this, R.string.wizard_error_ingredients, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            flipper.showNext();
        });

        navigationStepsSubmit.setOnClickListener(v -> {
            addRecipe();
        });

    }

    private void addRecipe() {
        List<String> recipesTypes = Arrays.asList(Util.getRecipesTypes(this));

        // Check all ingredients have been added to database
        for (Ingredient i : ingredientsAdapter.getIngredients()) {
            if (i.product.id < 0) {
                Toast.makeText(this, R.string.wizard_error_ingredients, Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Update ingredients and steps
        recipe.title = String.valueOf(editTitle.getText());
        recipe.category = recipesTypes.indexOf(editType.getText().toString());
        try {
            recipe.portions = Integer.parseInt(String.valueOf(editPortions.getText()));
        } catch (Exception e) {
            recipe.portions = 1;
        }

        List<Ingredient> newIngredients = ingredientsAdapter.getIngredients();
        recipe.ingredients = Ingredient.toJson(newIngredients);

        List<String> newSteps = stepsAdapter.getSteps();
        recipe.steps = Util.listToJson(newSteps);


        // Update the recipe entity
        model.addItem(recipe);
        Toast.makeText(this, R.string.wizard_submit_message, Toast.LENGTH_SHORT).show();
        finish();
    }
}