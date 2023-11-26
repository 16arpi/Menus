package com.pigeoff.menu.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;
import com.pigeoff.menu.R;
import com.pigeoff.menu.activities.RecipeActivity;
import com.pigeoff.menu.adapters.OnAdapterAction;
import com.pigeoff.menu.adapters.RecipeAdapter;
import com.pigeoff.menu.database.ProductEntity;
import com.pigeoff.menu.database.RecipeEntity;
import com.pigeoff.menu.models.RecipesViewModel;
import com.pigeoff.menu.util.Constants;
import com.pigeoff.menu.util.ImportExport;
import com.pigeoff.menu.util.OnSearchCallback;
import com.pigeoff.menu.util.Util;

import java.util.ArrayList;
import java.util.List;

public class RecipeFragment extends MenuFragment {
    OnSearchCallback searchCallback;
    RecipesViewModel model;
    LiveData<List<RecipeEntity>> recipes;
    List<ProductEntity> products;
    ImportExport<Fragment> importExport;

    SearchView searchView;
    SearchBar searchBar;
    FloatingActionButton addButton;
    RecyclerView recyclerView;
    RecyclerView recyclerViewSearch;
    LinearLayout layoutEmpty;

    RecipeAdapter recipeAdapter;
    RecipeAdapter recipeAdapterSearch;

    public RecipeFragment(OnSearchCallback searchCallback) {
        // Required empty public constructor
        this.searchCallback = searchCallback;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = new RecipesViewModel(requireActivity().getApplication());
        recipes = model.getRecipes();
        importExport = new ImportExport<Fragment>(this, model);
    }

    @Override
    public void onResume() {
        super.onResume();
        Util.hideKeyboard(requireActivity());
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

        recyclerView = view.findViewById(R.id.recycler_view_recipe);
        recyclerViewSearch = view.findViewById(R.id.recycler_view_recipe_search);
        layoutEmpty = view.findViewById(R.id.layout_empty);
        addButton = view.findViewById(R.id.add_button);
        searchView = view.findViewById(R.id.search_view);
        searchBar = view.findViewById(R.id.search_bar);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewSearch.setLayoutManager(new LinearLayoutManager(requireContext()));
        recipeAdapter = new RecipeAdapter(requireContext(), new ArrayList<>());
        recipeAdapterSearch = new RecipeAdapter(requireContext(), new ArrayList<>());

        recyclerViewSearch.setAdapter(recipeAdapterSearch);
        recyclerView.setAdapter(recipeAdapter);

        model.getProducts().observe(getViewLifecycleOwner(), productEntities -> products = productEntities);
        recipes.observe(getViewLifecycleOwner(), this::setupUI);

    }

    private void setupUI(List<RecipeEntity> items) {

        if (items.size() > 0) {
            layoutEmpty.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.VISIBLE);
        }

        recipeAdapter.updateRecipes(new ArrayList<>(items));

        searchBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.item_products) {
                ProductFragment productFragment = new ProductFragment(false, Constants.TAB_GROCERIES);
                productFragment.showFullScreen(requireActivity().getSupportFragmentManager());
            } else if (item.getItemId() == R.id.item_export_recipes) {
                if (products != null) importExport.export(products, items);
            } else if (item.getItemId() == R.id.item_import_recipes) {
                importExport.open();
            }
            return true;
        });

        addButton.setOnClickListener(v -> {
            RecipeEditFragment editFragment = RecipeEditFragment.newInstance();
            editFragment.showFullScreen(requireActivity().getSupportFragmentManager());
            editFragment.setActionListener(recipe -> {
                model.addItem(recipe);
                Util.hideKeyboard(requireActivity());
            });
        });

        recipeAdapter.setOnAdapterAction(new OnAdapterAction<RecipeEntity>() {
            @Override
            public void onItemClick(RecipeEntity item) {
                long id = item.id;

                Intent intent = new Intent(getActivity(), RecipeActivity.class);
                intent.putExtra(Constants.RECIPE_ID, id);
                startActivity(intent);

            }

            @Override
            public void onItemClick(RecipeEntity item, int action) {

            }

            @Override
            public void onItemLongClick(RecipeEntity item, int position) {

            }
        });

        searchView.getEditText().setOnEditorActionListener(((textView, i, keyEvent) -> {
            ArrayList<RecipeEntity> results = new ArrayList<>();
            for (RecipeEntity r : items) {
                if (Util.stringMatchSearch(r.title, String.valueOf(searchView.getText()))) {
                    results.add(r);
                }
            }

            recipeAdapterSearch.updateRecipes(results);
            return true;
        }));

        recipeAdapterSearch.setOnAdapterAction(new OnAdapterAction<RecipeEntity>() {
            @Override
            public void onItemClick(RecipeEntity item) {
                long id = item.id;

                Intent intent = new Intent(getActivity(), RecipeActivity.class);
                intent.putExtra(Constants.RECIPE_ID, id);
                startActivity(intent);

                searchView.hide();
            }

            @Override
            public void onItemClick(RecipeEntity item, int action) {

            }

            @Override
            public void onItemLongClick(RecipeEntity item, int position) {

            }
        });

        searchView.addTransitionListener(((searchView1, previousState, newState) -> {
            if (newState == SearchView.TransitionState.HIDDEN) {
                recipeAdapterSearch.updateRecipes(new ArrayList<>());
                if (searchCallback != null) searchCallback.onSearchClose();
            } else if (newState == SearchView.TransitionState.SHOWING) {
                if (searchCallback != null) searchCallback.onSearchOpen();
            }
        }));
    }
}