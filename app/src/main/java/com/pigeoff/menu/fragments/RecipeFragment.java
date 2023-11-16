package com.pigeoff.menu.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;
import com.google.gson.Gson;
import com.pigeoff.menu.R;
import com.pigeoff.menu.activities.RecipeActivity;
import com.pigeoff.menu.adapters.OnAdapterAction;
import com.pigeoff.menu.adapters.RecipeAdapter;
import com.pigeoff.menu.database.ProductEntity;
import com.pigeoff.menu.database.RecipeEntity;
import com.pigeoff.menu.models.RecipesViewModel;
import com.pigeoff.menu.util.Constants;
import com.pigeoff.menu.util.Export;
import com.pigeoff.menu.util.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class RecipeFragment extends MenuFragment {
    RecipesViewModel model;
    LiveData<List<RecipeEntity>> recipes;
    List<ProductEntity> products;

    SearchView searchView;
    SearchBar searchBar;
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
        model = new RecipesViewModel(requireActivity().getApplication());
        recipes = model.getItems();

        products = new ArrayList<>();
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
        addButton = view.findViewById(R.id.add_button);
        searchView = view.findViewById(R.id.search_view);
        searchBar = view.findViewById(R.id.search_bar);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewSearch.setLayoutManager(new LinearLayoutManager(requireContext()));
        recipeAdapter = new RecipeAdapter(requireContext(), new ArrayList<>());
        recipeAdapterSearch = new RecipeAdapter(requireContext(), new ArrayList<>());

        recyclerViewSearch.setAdapter(recipeAdapterSearch);
        recyclerView.setAdapter(recipeAdapter);

        model.getProducts().observe(getViewLifecycleOwner(), new Observer<List<ProductEntity>>() {
            @Override
            public void onChanged(List<ProductEntity> productEntities) {
                products = productEntities;
            }
        });

        recipes.observe(getViewLifecycleOwner(), new Observer<List<RecipeEntity>>() {
            @Override
            public void onChanged(List<RecipeEntity> recipeEntities) {
                setupUI(recipeEntities);
            }
        });

    }

    private void setupUI(List<RecipeEntity> items) {
        //recyclerView.setAdapter(new RecipeAdapter(requireContext(), new ArrayList<>(items)));
        recipeAdapter.updateRecipes(new ArrayList<>(items));

        searchBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.item_products) {
                    ProductFragment productFragment = new ProductFragment(false);
                    productFragment.showFullScreen(requireActivity().getSupportFragmentManager());
                } else if (item.getItemId() == R.id.item_export_recipes) {
                    exportRecipes(items);
                }
                return true;
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecipeEditFragment editFragment = RecipeEditFragment.newInstance();
                editFragment.showFullScreen(requireActivity().getSupportFragmentManager());
                editFragment.setActionListener(new RecipeEditFragment.OnActionListener() {
                    @Override
                    public void onSubmit(RecipeEntity recipe) {
                        model.addItem(recipe);
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
                startActivity(intent);

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
                for (RecipeEntity r : items) {
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
                startActivity(intent);

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

    private void exportRecipes(List<RecipeEntity> recipes) {
        Export export = new Export(products, recipes);
        Gson gson = new Gson();
        String string = gson.toJson(export);

        try {
            File outputDir = requireContext().getFilesDir();
            File outputFile = File.createTempFile("export", ".json", outputDir);
            Uri uri = FileProvider.getUriForFile(requireContext(), "com.pigeoff.menu.fileprovider", outputFile);

            FileOutputStream inputStream = new FileOutputStream(outputFile);
            inputStream.write(string.getBytes(Charset.defaultCharset()));
            inputStream.close();

            if (uri != null) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                shareIntent.setDataAndType(uri, requireContext().getContentResolver().getType(uri));
                startActivity(Intent.createChooser(shareIntent, null));
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }




}