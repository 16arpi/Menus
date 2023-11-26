package com.pigeoff.menu.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.pigeoff.menu.R;
import com.pigeoff.menu.adapters.OnAdapterAction;
import com.pigeoff.menu.adapters.RecipeAdapter;
import com.pigeoff.menu.database.RecipeEntity;
import com.pigeoff.menu.models.RecipesViewModel;
import com.pigeoff.menu.util.Util;

import java.util.ArrayList;
import java.util.List;

public class RecipePickerFragment extends BottomSheetDialogFragment {

    private LiveData<List<RecipeEntity>> recipes;
    private OnRecipePicked listener;
    private TextInputEditText searchBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RecipesViewModel model = new RecipesViewModel(requireActivity().getApplication());
        recipes = model.getRecipes();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_picker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchBar = view.findViewById(R.id.search_bar_picker);
        RecyclerView recyclerViewSearch = view.findViewById(R.id.recycler_view_search);

        RecipeAdapter adapter = new RecipeAdapter(requireContext(), new ArrayList<>());
        recyclerViewSearch.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewSearch.setAdapter(adapter);

        recipes.observe(getViewLifecycleOwner(), recipeEntities -> {
            adapter.updateRecipes(new ArrayList<>(recipeEntities));
            searchBar.setOnEditorActionListener(((textView, i, keyEvent) -> {
                ArrayList<RecipeEntity> newRecipes = new ArrayList<>();
                for (RecipeEntity e : recipeEntities) {
                    if (Util.stringMatchSearch(e.title, textView.getText().toString())) {
                        newRecipes.add(e);
                    }
                }
                adapter.updateRecipes(newRecipes);
                return true;
            }));
        });

        adapter.setOnAdapterAction(new OnAdapterAction<RecipeEntity>() {
            @Override
            public void onItemClick(RecipeEntity item) {
                listener.onRecipePicked(item);
            }

            @Override
            public void onItemClick(RecipeEntity item, int action) {

            }

            @Override
            public void onItemLongClick(RecipeEntity item, int position) {

            }
        });

    }

    public interface OnRecipePicked {
        void onRecipePicked(RecipeEntity recipe);
    }

    public void setOnRecipePicked(OnRecipePicked listener) {
        this.listener = listener;
    }
}
