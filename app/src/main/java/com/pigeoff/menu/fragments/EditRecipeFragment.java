package com.pigeoff.menu.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.pigeoff.menu.MenuApplication;
import com.pigeoff.menu.R;
import com.pigeoff.menu.adapters.IngredientAdapter;
import com.pigeoff.menu.adapters.StepAdapter;
import com.pigeoff.menu.data.Ingredient;
import com.pigeoff.menu.database.MenuDatabase;
import com.pigeoff.menu.database.RecipeEntity;
import com.pigeoff.menu.util.Unit;
import com.pigeoff.menu.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditRecipeFragment extends BottomSheetDialogFragment {

    public static String RECIPE_ID = "recipeid";

    private boolean newRecipe = true;
    private MenuDatabase database;
    private RecipeEntity recipe;
    private OnActionListener actionListener;

    // BINDINGS
    private TextInputEditText editTitle;
    private AutoCompleteTextView editType;
    private TextInputEditText editPortions;

    private RecyclerView recyclerViewIngredients;
    private TextInputEditText editIngredientValue;
    private MaterialAutoCompleteTextView editIngredientUnit;
    private TextInputEditText editIngredientLabel;
    private MaterialButton editIngredientSubmit;

    private RecyclerView recyclerViewSteps;
    private TextInputEditText editStep;
    private MaterialButton editStepSubmit;

    // ADAPTERS
    private IngredientAdapter ingredientAdapter;
    private StepAdapter stepAdapter;

    public interface OnActionListener {
        void onSubmit(RecipeEntity recipe);
    }

    public static EditRecipeFragment newInstance(long recipeId) {
        Bundle args = new Bundle();
        args.putLong(RECIPE_ID, recipeId);
        EditRecipeFragment fragment = new EditRecipeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static EditRecipeFragment newInstance() {
        Bundle args = new Bundle();
        EditRecipeFragment fragment = new EditRecipeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MenuApplication app = (MenuApplication) requireActivity().getApplication();
        database = app.database;

        long id = getArguments().getLong(RECIPE_ID, -1);
        if (id >= 0) {
            recipe = database.recipeDAO().select(id);
            newRecipe = false;
        } else {
            recipe = new RecipeEntity();
            newRecipe = true;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_edit_recipe, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Bindings
        editTitle = view.findViewById(R.id.edit_title);
        editType = view.findViewById(R.id.edit_meal);
        editPortions = view.findViewById(R.id.edit_portions);
        recyclerViewIngredients = view.findViewById(R.id.recycler_view_ingredients);
        editIngredientValue = view.findViewById(R.id.edit_ingredient_value);
        editIngredientUnit = view.findViewById(R.id.edit_ingredient_unit);
        editIngredientLabel = view.findViewById(R.id.edit_ingredient_label);
        editIngredientSubmit = view.findViewById(R.id.button_ingredient_submit);
        recyclerViewSteps = view.findViewById(R.id.recycler_view_steps);
        editStep = view.findViewById(R.id.edit_step);
        editStepSubmit = view.findViewById(R.id.button_step_submit);


        // Filling
        editTitle.setText(recipe.title);
        editPortions.setText(String.valueOf(recipe.portions));
        Util.selectRecipeTypeAutoCompleteItem(editType, recipe.category);
        Util.selectUnitAutoCompleteItem(editIngredientUnit, 0);

        // Ingredients
        ArrayList<Ingredient> ingredients = Ingredient.fromJson(recipe.ingredients);
        ingredientAdapter = new IngredientAdapter(requireContext(), ingredients, true);
        recyclerViewIngredients.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewIngredients.setAdapter(ingredientAdapter);
        editIngredientSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (editIngredientValue.getText().toString().equals("")) return;
                if (editIngredientLabel.getText().toString().equals("")) return;

                Ingredient ingredient = new Ingredient(
                        Float.parseFloat(editIngredientValue.getText().toString()),
                        Unit.getUnitId(editIngredientUnit.getText().toString()),
                        editIngredientLabel.getText().toString()
                );
                ingredientAdapter.addItem(ingredient);

                editIngredientValue.setText("0");
                Util.selectUnitAutoCompleteItem(editIngredientUnit, 0);
                editIngredientLabel.setText("");

            }
        });

        // Preparation
        ArrayList<String> steps = Util.listFromJson(recipe.steps);
        stepAdapter = new StepAdapter(requireContext(), steps, true);
        recyclerViewSteps.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewSteps.setAdapter(stepAdapter);
        editStepSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editStep.getText().toString().equals("")) return;

                String step = editStep.getText().toString();
                stepAdapter.addItem(step);

                editStep.setText("");
            }
        });

        view.findViewById(R.id.edit_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecipeEntity returnRecipe = updateRecipe();
                if (actionListener != null) actionListener.onSubmit(returnRecipe);
                dismiss();
            }
        });
    }

    public void setActionListener(OnActionListener actionListener) {
        this.actionListener = actionListener;
    }

    private RecipeEntity updateRecipe() {

        List<String> recipesTypes = Arrays.asList(Util.getRecipesTypes(requireContext()));

        recipe.title = editTitle.getText().toString();
        recipe.category = recipesTypes.indexOf(editType.getText().toString());
        try {
            recipe.portions = Integer.parseInt(editPortions.getText().toString());
        } catch (Exception e) {
            recipe.portions = 1;
            System.out.println("Erreur portions");
        }
        // Ingredients

        ArrayList<Ingredient> newIngredients = ingredientAdapter.getIngredients();
        recipe.ingredients = Ingredient.toJson(newIngredients);

        ArrayList<String> newSteps = stepAdapter.getSteps();
        recipe.steps = Util.listToJson(newSteps);

        return recipe;
    }



}
