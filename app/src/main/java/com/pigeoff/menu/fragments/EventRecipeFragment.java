package com.pigeoff.menu.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.pigeoff.menu.MenuApplication;
import com.pigeoff.menu.R;
import com.pigeoff.menu.adapters.RecipeEventAdapter;
import com.pigeoff.menu.data.EventRecipe;
import com.pigeoff.menu.database.GroceryEntity;
import com.pigeoff.menu.database.GroceryWithProduct;
import com.pigeoff.menu.database.MenuDatabase;
import com.pigeoff.menu.models.GroceriesViewModel;

import java.util.List;

public class EventRecipeFragment extends BottomSheetDialogFragment {

    GroceriesViewModel model;
    LiveData<List<GroceryWithProduct>> items;
    RecyclerView recyclerView;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.model = new GroceriesViewModel(requireActivity().getApplication());
        this.items = model.getEventsItems();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_edit_recipe_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        items.observe(getViewLifecycleOwner(), new Observer<List<GroceryWithProduct>>() {
            @Override
            public void onChanged(List<GroceryWithProduct> groceryEntities) {
                List<EventRecipe> eventRecipes = EventRecipe.fromGroceries(groceryEntities);
                recyclerView.setAdapter(new RecipeEventAdapter(requireContext(), eventRecipes, new RecipeEventAdapter.OnAdapterAction() {
                    @Override
                    public void onItemDeleted(EventRecipe item) {
                        model.deleteGroceriesForCalendar(item.eventId);
                    }
                }));
            }
        });


    }


}
