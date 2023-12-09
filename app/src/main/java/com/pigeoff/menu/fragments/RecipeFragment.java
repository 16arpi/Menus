package com.pigeoff.menu.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.ChipGroup;
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

public class RecipeFragment extends Fragment {

    boolean searchStateOpen = false;
    OnBackPressedCallback backCallback;

    OnSearchCallback searchCallback;
    RecipesViewModel model;
    LiveData<List<RecipeEntity>> recipes;
    List<ProductEntity> products;
    ImportExport<Fragment> importExport;

    SearchView searchView;
    SearchBar searchBar;
    ChipGroup chips;
    FloatingActionButton addButton;
    RecyclerView recyclerView;
    RecyclerView recyclerViewSearch;
    LinearLayout layoutEmpty;

    RecipeAdapter recipeAdapter;
    RecipeAdapter recipeAdapterSearch;

    public RecipeFragment() {

    }

    public void addCallback(OnSearchCallback callback) {
        this.searchCallback = callback;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = new RecipesViewModel(requireActivity().getApplication());
        recipes = model.getRecipes();
        importExport = new ImportExport<>(this, model);

        backCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (searchStateOpen) {
                    if (searchView != null) searchView.hide();
                }
                else {
                    setEnabled(false);
                    requireActivity().onBackPressed();
                }
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(backCallback);
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
        chips = view.findViewById(R.id.chip_group_filter);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewSearch.setLayoutManager(new LinearLayoutManager(requireContext()));
        recipeAdapter = new RecipeAdapter(requireContext(), new ArrayList<>());
        recipeAdapterSearch = new RecipeAdapter(requireContext(), new ArrayList<>());

        recyclerViewSearch.setAdapter(recipeAdapterSearch);
        recyclerView.setAdapter(recipeAdapter);

        model.getProducts().observe(getViewLifecycleOwner(), productEntities -> products = productEntities);
        recipes.observe(getViewLifecycleOwner(), items -> {
            chips.setOnCheckedStateChangeListener(((group, checkedIds) -> setupUI(checkedIds, items)));
            setupUI(chips.getCheckedChipIds(), items);
        });

    }

    private void setupUI(List<Integer> filters, List<RecipeEntity> items) {

        if (items.size() > 0) {
            layoutEmpty.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.VISIBLE);
        }

        recipeAdapter.updateRecipes(new ArrayList<>(
                getFilteredRecipes(filters, items)
        ));

        searchBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.item_products) {
                ProductFragment productFragment = ProductFragment.newInstance(true, false, Constants.SECTION_EMPTY);
                productFragment.show(requireActivity().getSupportFragmentManager(), "product");
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
                searchStateOpen = false;
                recipeAdapterSearch.updateRecipes(new ArrayList<>());
                if (searchCallback != null) searchCallback.onSearchOpen(false);
            } else if (newState == SearchView.TransitionState.SHOWING) {
                searchStateOpen = true;
                backCallback.setEnabled(true);
                if (searchCallback != null) searchCallback.onSearchOpen(true);
            }
        }));
    }

    private static List<RecipeEntity> getFilteredRecipes(List<Integer> filters, List<RecipeEntity> items) {
        List<RecipeEntity> filtered = new ArrayList<>();
        for (RecipeEntity i : items) {
            if (i.category == 0 && filters.contains(R.id.chip_filter_meal)) {
                filtered.add(i);
            } else if (i.category == 1 && filters.contains(R.id.chip_filter_starter)) {
                filtered.add(i);
            } else if (i.category == 2 && filters.contains(R.id.chip_filter_dessert)) {
                filtered.add(i);
            } else if (i.category == 3 && filters.contains(R.id.chip_filter_other)) {
                filtered.add(i);
            } else if (filters.size() == 0) {
                filtered.add(i);
            }
        }
        return filtered;
    }

}