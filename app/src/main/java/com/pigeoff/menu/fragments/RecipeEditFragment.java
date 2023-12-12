package com.pigeoff.menu.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.pigeoff.menu.R;
import com.pigeoff.menu.adapters.IngredientsEditAdapter;
import com.pigeoff.menu.adapters.StepAdapter;
import com.pigeoff.menu.data.Ingredient;
import com.pigeoff.menu.database.ProductEntity;
import com.pigeoff.menu.database.RecipeEntity;
import com.pigeoff.menu.models.RecipeViewModel;
import com.pigeoff.menu.util.Constants;
import com.pigeoff.menu.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class RecipeEditFragment extends DialogFragment {

    public static String RECIPE_ID = "recipeid";
    private RecipeViewModel model;
    private RecipeEntity recipe;
    private HashMap<Long, ProductEntity> products;
    private OnActionListener actionListener;

    // BINDINGS
    private Button editSubmit;
    private TextInputEditText editTitle;
    private AutoCompleteTextView editType;
    private TextInputEditText editPortions;
    private NestedScrollView layoutIngredients;
    private NestedScrollView layoutSteps;

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

        if (getArguments() != null) {
            long id = getArguments().getLong(RECIPE_ID, -1);
            model = new RecipeViewModel(id, requireActivity().getApplication());
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
        editSubmit = view.findViewById(R.id.edit_submit);
        editTitle = view.findViewById(R.id.edit_title);
        editType = view.findViewById(R.id.edit_meal);
        editPortions = view.findViewById(R.id.edit_portions);
        TabLayout tabLayoutPanels = view.findViewById(R.id.tab_panels);
        layoutIngredients = view.findViewById(R.id.layout_ingredients);
        layoutSteps = view.findViewById(R.id.layout_steps);
        recyclerViewIngredients = view.findViewById(R.id.recycler_view_ingredients);
        editIngredientSubmit = view.findViewById(R.id.button_ingredient_submit);
        recyclerViewSteps = view.findViewById(R.id.recycler_view_steps);
        editStep = view.findViewById(R.id.edit_step);
        editStepSubmit = view.findViewById(R.id.button_step_submit);

        tabLayoutPanels.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    layoutIngredients.setVisibility(View.VISIBLE);
                    layoutSteps.setVisibility(View.GONE);
                } else {
                    layoutIngredients.setVisibility(View.GONE);
                    layoutSteps.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        editIngredientSubmit.setOnClickListener(v -> {
            ProductFragment productFragment = ProductFragment.newInstance(false, true, Constants.SECTION_EMPTY);
            productFragment.addProductActionListener(item -> {
                Ingredient ingredient = new Ingredient(
                        item,
                        ""
                );
                ingredientAdapter.addItem(ingredient);
            });
            productFragment.show(getParentFragmentManager(), "edit_product");
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


        recyclerViewSteps.setLayoutManager(new LinearLayoutManager(requireContext()));

        editStepSubmit.setOnClickListener(v -> {
            if (String.valueOf(editStep.getText()).equals("")) return;

            String step = String.valueOf(editStep.getText());
            stepAdapter.addItem(step);

            editStep.setText("");
        });

        editSubmit.setOnClickListener(v -> {
            RecipeEntity returnRecipe = updateRecipe(recipe);
            if (actionListener != null) actionListener.onSubmit(returnRecipe);
            try {
                dismissFullScreen(getParentFragmentManager());
            } catch (Exception e) {
                dismiss();
            }
        });

        view.findViewById(R.id.button_back).setOnClickListener(v -> {
            try {
                dismissFullScreen(getParentFragmentManager());
            } catch (Exception e) {
                dismiss();
            }
        });

        model.getRecipe().observe(getViewLifecycleOwner(), object -> {
            if (object.a == null || object.b == null) return;

            products = Util.productsToDict(object.b);

            if (recipe != null) return;

            recipe = object.a;

            // Enabling buttons
            editSubmit.setEnabled(true);
            editIngredientSubmit.setEnabled(true);
            editStepSubmit.setEnabled(true);

            // Filling
            editTitle.setText(recipe.title);
            editPortions.setText(String.valueOf(recipe.portions));
            Util.selectRecipeTypeAutoCompleteItem(editType, recipe.category);

            // Ingredients
            ArrayList<Ingredient> ingredients = Ingredient.fromJson(products, recipe.ingredients);
            ingredientAdapter = new IngredientsEditAdapter(requireContext(), ingredients);
            recyclerViewIngredients.setLayoutManager(new LinearLayoutManager(requireContext()));
            recyclerViewIngredients.setAdapter(ingredientAdapter);

            // Preparation
            ArrayList<String> steps = Util.listFromJson(recipe.steps);
            stepAdapter = new StepAdapter(requireContext(), steps, true);
            recyclerViewSteps.setAdapter(stepAdapter);

            new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                    int from = viewHolder.getAdapterPosition();
                    int to = target.getAdapterPosition();
                    stepAdapter.switchItems(from, to);
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
                    ingredientAdapter.switchItems(from, to);
                    return true;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                }
            }).attachToRecyclerView(recyclerViewIngredients);
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

    private RecipeEntity updateRecipe(RecipeEntity source) {

        List<String> recipesTypes = Arrays.asList(Util.getRecipesTypes(requireContext()));

        source.title = String.valueOf(editTitle.getText());
        source.category = recipesTypes.indexOf(editType.getText().toString());
        try {
            source.portions = Integer.parseInt(String.valueOf(editPortions.getText()));
        } catch (Exception e) {
            source.portions = 1;
        }
        // Ingredients

        ArrayList<Ingredient> newIngredients = ingredientAdapter.getIngredients();
        source.ingredients = Ingredient.toJson(newIngredients);

        ArrayList<String> newSteps = stepAdapter.getSteps();
        source.steps = Util.listToJson(newSteps);

        return source;
    }



}
