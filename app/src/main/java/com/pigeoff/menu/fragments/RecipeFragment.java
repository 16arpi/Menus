package com.pigeoff.menu.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.search.SearchView;
import com.pigeoff.menu.R;
import com.pigeoff.menu.activities.RecipeActivity;
import com.pigeoff.menu.adapters.OnAdapterAction;
import com.pigeoff.menu.adapters.RecipeAdapter;
import com.pigeoff.menu.database.RecipeEntity;
import com.pigeoff.menu.util.Constants;
import com.pigeoff.menu.util.Util;

import java.util.ArrayList;
import java.util.List;

public class RecipeFragment extends MenuFragment {
    private List<RecipeEntity> recipes;

    SearchView searchView;
    FloatingActionButton addButton;
    RecyclerView recyclerView;
    RecyclerView recyclerViewSearch;

    RecipeAdapter recipeAdapter;
    RecipeAdapter recipeAdapterSearch;

    public RecipeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recipes = new ArrayList<>(database.recipeDAO().select());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipe, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ActivityResultLauncher<Intent> recipeActivityResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        switch (result.getResultCode()) {
                            case (Constants.RESULT_DELETE): {
                                Intent intent = result.getData();
                                if (intent == null) return;

                                long id = intent.getLongExtra(Constants.RECIPE_ID, -1);
                                if (id < 0) return;

                                for (int i = 0; i < recipes.size(); ++i) {
                                    if (recipes.get(i).id == id) {
                                        recipeAdapter.deleteRecipe(null, i);
                                        recipes.remove(i);
                                        return;
                                    }
                                }
                            }
                            case (Constants.RESULT_EDIT): {
                                Intent intent = result.getData();
                                if (intent == null) return;

                                long id = intent.getLongExtra(Constants.RECIPE_ID, -1);
                                if (id < 0) return;

                                for (int i = 0; i < recipes.size(); ++i) {
                                    if (recipes.get(i).id == id) {
                                        RecipeEntity r = database.recipeDAO().select(id);
                                        recipeAdapter.editRecipe(r, i);
                                        recipes.set(i, r);
                                        return;
                                    }
                                }
                            }
                            default:{

                            }
                        }

                    }
                }
        );

        recyclerView = view.findViewById(R.id.recycler_view_recipe);
        recyclerViewSearch = view.findViewById(R.id.recycler_view_recipe_search);
        addButton = view.findViewById(R.id.add_button);
        searchView = view.findViewById(R.id.search_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewSearch.setLayoutManager(new LinearLayoutManager(requireContext()));
        recipeAdapter = new RecipeAdapter(requireContext(), new ArrayList<>(recipes));
        recipeAdapterSearch = new RecipeAdapter(requireContext(), new ArrayList<>());
        recyclerView.setAdapter(recipeAdapter);
        recyclerViewSearch.setAdapter(recipeAdapterSearch);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditRecipeFragment editFragment = EditRecipeFragment.newInstance();
                editFragment.showFullScreen(requireActivity().getSupportFragmentManager());
                editFragment.setActionListener(new EditRecipeFragment.OnActionListener() {
                    @Override
                    public void onSubmit(RecipeEntity recipe) {
                        recipe.id = database.recipeDAO().insert(recipe);
                        recipeAdapter.addRecipe(recipe);
                        recipes.add(0, recipe);
                    }
                });
            }
        });

        recipeAdapter.setOnAdapterAction(new OnAdapterAction() {
            @Override
            public void onItemClick(Object item) {
                RecipeEntity recipeItem = (RecipeEntity) item;
                long id = recipeItem.id;

                Intent intent = new Intent(getActivity(), RecipeActivity.class);
                intent.putExtra(Constants.RECIPE_ID, id);
                recipeActivityResult.launch(intent);

            }

            @Override
            public void onItemClick(Object item, int action) {

            }

            @Override
            public void onItemLongClick(Object item, int position) {

            }
        });

        searchView.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                ArrayList<RecipeEntity> results = new ArrayList<>();
                for (RecipeEntity r : recipes) {
                    if (Util.stringMatchSearch(r.title, searchView.getText().toString())) {
                        results.add(r);
                    }
                }

                recipeAdapterSearch.updateRecipes(results);
                return true;
            }
        });

        recipeAdapterSearch.setOnAdapterAction(new OnAdapterAction() {
            @Override
            public void onItemClick(Object item) {
                RecipeEntity recipeItem = (RecipeEntity) item;
                long id = recipeItem.id;

                Intent intent = new Intent(getActivity(), RecipeActivity.class);
                intent.putExtra(Constants.RECIPE_ID, id);
                recipeActivityResult.launch(intent);


                searchView.hide();
            }

            @Override
            public void onItemClick(Object item, int action) {

            }

            @Override
            public void onItemLongClick(Object item, int position) {

            }
        });

        searchView.addTransitionListener(new SearchView.TransitionListener() {
            @Override
            public void onStateChanged(@NonNull SearchView searchView, @NonNull SearchView.TransitionState previousState, @NonNull SearchView.TransitionState newState) {
                if (newState == SearchView.TransitionState.HIDDEN) {
                    recipeAdapterSearch.updateRecipes(new ArrayList<>());
                }
            }
        });
    }
}