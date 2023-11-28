package com.pigeoff.menu.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.ChipGroup;
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
    private ChipGroup chips;

    public RecipePickerFragment() {

    }

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
        chips = view.findViewById(R.id.chip_group_filter);
        RecyclerView recyclerViewSearch = view.findViewById(R.id.recycler_view_search);

        RecipeAdapter adapter = new RecipeAdapter(requireContext(), new ArrayList<>());
        recyclerViewSearch.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewSearch.setAdapter(adapter);

        recipes.observe(getViewLifecycleOwner(), recipeEntities -> {

            adapter.updateRecipes(getFilteredRecipes(
                    chips.getCheckedChipIds(),
                    String.valueOf(searchBar.getText()),
                    recipeEntities));
            searchBar.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    adapter.updateRecipes(
                            getFilteredRecipes(
                                    chips.getCheckedChipIds(),
                                    charSequence.toString(),
                                    recipeEntities)
                    );
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            chips.setOnCheckedStateChangeListener((group, filters) ->
                adapter.updateRecipes(
                        getFilteredRecipes(
                                filters,
                                String.valueOf(searchBar.getText()),
                                recipeEntities
                        )
                )
            );
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

    private static List<RecipeEntity> getFilteredRecipes(List<Integer> filter, String query, List<RecipeEntity> items) {
        List<RecipeEntity> filtered = new ArrayList<>();
        for (RecipeEntity i : items) {
            if (i.category == 0 && filter.contains(R.id.chip_filter_meal)) {
                if (Util.stringMatchSearch(i.title, query)) filtered.add(i);
            } else if (i.category == 1 && filter.contains(R.id.chip_filter_starter)) {
                if (Util.stringMatchSearch(i.title, query)) filtered.add(i);
            } else if (i.category == 2 && filter.contains(R.id.chip_filter_dessert)) {
                if (Util.stringMatchSearch(i.title, query)) filtered.add(i);
            } else if (i.category == 3 && filter.contains(R.id.chip_filter_other)) {
                if (Util.stringMatchSearch(i.title, query)) filtered.add(i);
            } else if (filter.size() == 0) {
                if (Util.stringMatchSearch(i.title, query)) filtered.add(i);
            }
        }
        return filtered;
    }

    public interface OnRecipePicked {
        void onRecipePicked(RecipeEntity recipe);
    }

    public void setOnRecipePicked(OnRecipePicked listener) {
        this.listener = listener;
    }
}
