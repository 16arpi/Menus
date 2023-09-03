package com.pigeoff.menu.fragments;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.pigeoff.menu.MenuApplication;
import com.pigeoff.menu.R;
import com.pigeoff.menu.adapters.OnAdapterAction;
import com.pigeoff.menu.adapters.RecipeAdapter;
import com.pigeoff.menu.database.RecipeEntity;
import com.pigeoff.menu.util.Util;

import java.util.ArrayList;
import java.util.List;

public class RecipePickerFragment extends BottomSheetDialogFragment {

    private OnRecipePicked listener;
    private RecyclerView recyclerViewSearch;
    private TextInputEditText searchBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        recyclerViewSearch = view.findViewById(R.id.recycler_view_search);

        MenuApplication app = (MenuApplication) getActivity().getApplication();
        ArrayList<RecipeEntity> recipes = new ArrayList<>(app.database.recipeDAO().select());

        RecipeAdapter adapter = new RecipeAdapter(requireContext(), recipes);
        recyclerViewSearch.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewSearch.setAdapter(adapter);

        adapter.setOnAdapterAction(new OnAdapterAction() {
            @Override
            public void onItemClick(Object item) {
                RecipeEntity recipe = (RecipeEntity) item;
                listener.onRecipePicked(recipe);
            }

            @Override
            public void onItemClick(Object item, int action) {

            }

            @Override
            public void onItemLongClick(Object item, int position) {

            }
        });

        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                List<RecipeEntity> all = app.database.recipeDAO().select();
                ArrayList<RecipeEntity> newRecipes = new ArrayList<>();
                for (RecipeEntity e : all) {
                    if (Util.stringMatchSearch(e.title, textView.getText().toString())) {
                        newRecipes.add(e);
                    }
                }
                adapter.updateRecipes(newRecipes);
                return true;
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
