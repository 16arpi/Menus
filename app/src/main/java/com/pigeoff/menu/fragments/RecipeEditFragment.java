package com.pigeoff.menu.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.pigeoff.menu.MenuApplication;
import com.pigeoff.menu.R;
import com.pigeoff.menu.adapters.IngredientsEditAdapter;
import com.pigeoff.menu.adapters.StepAdapter;
import com.pigeoff.menu.data.Ingredient;
import com.pigeoff.menu.database.MenuDatabase;
import com.pigeoff.menu.database.ProductEntity;
import com.pigeoff.menu.database.RecipeEntity;
import com.pigeoff.menu.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class RecipeEditFragment extends DialogFragment {

    public static String RECIPE_ID = "recipeid";

    private boolean newRecipe = true;
    private MenuDatabase database;
    private RecipeEntity recipe;
    private HashMap<Long, ProductEntity> products;
    private OnActionListener actionListener;

    // BINDINGS
    private TextInputEditText editTitle;
    private AutoCompleteTextView editType;
    private TextInputEditText editPortions;

    private RecyclerView recyclerViewIngredients;
    private MaterialButton editIngredientSubmit;

    private RecyclerView recyclerViewSteps;
    private TextInputEditText editStep;
    private MaterialButton editStepSubmit;

    // ADAPTERS
    private IngredientsEditAdapter ingredientAdapter;
    private StepAdapter stepAdapter;

    public interface OnActionListener {
        void onSubmit(RecipeEntity recipe);
    }

    public static RecipeEditFragment newInstance(long recipeId) {
        Bundle args = new Bundle();
        args.putLong(RECIPE_ID, recipeId);
        RecipeEditFragment fragment = new RecipeEditFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static RecipeEditFragment newInstance() {
        Bundle args = new Bundle();
        RecipeEditFragment fragment = new RecipeEditFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MenuApplication app = (MenuApplication) requireActivity().getApplication();
        database = app.database;

        products = Util.productsToDict(database.productDAO().getAll());

        long id = getArguments().getLong(RECIPE_ID, -1);
        if (id >= 0) {
            recipe = database.recipeDAO().select(id);
            newRecipe = false;
        } else {
            recipe = new RecipeEntity();
            newRecipe = true;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
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
        editIngredientSubmit = view.findViewById(R.id.button_ingredient_submit);
        recyclerViewSteps = view.findViewById(R.id.recycler_view_steps);
        editStep = view.findViewById(R.id.edit_step);
        editStepSubmit = view.findViewById(R.id.button_step_submit);


        // Filling
        editTitle.setText(recipe.title);
        editPortions.setText(String.valueOf(recipe.portions));
        Util.selectRecipeTypeAutoCompleteItem(editType, recipe.category);

        // Ingredients
        ArrayList<Ingredient> ingredients = Ingredient.fromJson(products, recipe.ingredients);
        ingredientAdapter = new IngredientsEditAdapter(requireContext(), ingredients);
        recyclerViewIngredients.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewIngredients.setAdapter(ingredientAdapter);

        editIngredientSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProductFragment productFragment = new ProductFragment(true);
                productFragment.addProductActionListener(new ProductFragment.OnProductAction() {
                    @Override
                    public void onItemSelected(ProductEntity item) {
                        Ingredient ingredient = new Ingredient(
                                item,
                                0.0f,
                                item.defaultUnit
                        );
                        ingredientAdapter.addItem(ingredient);
                    }
                });
                productFragment.showFullScreen(getParentFragmentManager());
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                ingredientAdapter.deleteItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(recyclerViewIngredients);



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
                try {
                    dismissFullScreen(getParentFragmentManager());
                } catch (Exception e) {
                    dismiss();
                }
            }
        });

        view.findViewById(R.id.button_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    dismissFullScreen(getParentFragmentManager());
                } catch (Exception e) {
                    dismiss();
                }
            }
        });
    }

    public void showFullScreen(FragmentManager fragmentManager) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(android.R.id.content, this)
                .addToBackStack(null).commit();
    }

    public void dismissFullScreen(FragmentManager fragmentManager) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        transaction.remove(this).commit();
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
