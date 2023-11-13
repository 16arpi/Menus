package com.pigeoff.menu.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.pigeoff.menu.MenuApplication;
import com.pigeoff.menu.R;
import com.pigeoff.menu.adapters.RecipeEventAdapter;
import com.pigeoff.menu.data.EventRecipe;
import com.pigeoff.menu.database.MenuDatabase;

import java.util.List;

public class EventRecipeFragment extends BottomSheetDialogFragment {

    RecyclerView recyclerView;
    OnEventRecipeAction listener;


    public EventRecipeFragment(OnEventRecipeAction listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        MenuApplication app = (MenuApplication) requireActivity().getApplication();
        MenuDatabase database = app.database;

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        List<EventRecipe> eventRecipes = EventRecipe.fromGroceries(database.groceryDAO().getGroceries());
        recyclerView.setAdapter(new RecipeEventAdapter(requireContext(), eventRecipes, new RecipeEventAdapter.OnAdapterAction() {
            @Override
            public void onItemDeleted(EventRecipe item) {
                database.groceryDAO().deleteGroceriesForCalendar(item.eventId);
                if (listener != null) listener.onEventRecipeDeleted(item);
            }
        }));
    }

    public interface OnEventRecipeAction {
        void onEventRecipeDeleted(EventRecipe eventRecipe);
    }

}
