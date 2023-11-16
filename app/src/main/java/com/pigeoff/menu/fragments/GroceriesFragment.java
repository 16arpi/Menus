package com.pigeoff.menu.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pigeoff.menu.MenuApplication;
import com.pigeoff.menu.R;
import com.pigeoff.menu.adapters.GroceriesAdapter;
import com.pigeoff.menu.adapters.OnAdapterAction;
import com.pigeoff.menu.data.GrocerieGroup;
import com.pigeoff.menu.database.GroceryEntity;
import com.pigeoff.menu.database.GroceryWithProduct;
import com.pigeoff.menu.database.MenuDatabase;
import com.pigeoff.menu.database.ProductEntity;
import com.pigeoff.menu.models.GroceriesViewModel;
import com.pigeoff.menu.util.Util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class GroceriesFragment extends Fragment {

    GroceriesViewModel model;
    MaterialToolbar toolbar;
    RecyclerView recyclerView;
    FloatingActionButton floatingActionButton;
    GroceriesAdapter adapter;

    public GroceriesFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        model = new GroceriesViewModel(requireActivity().getApplication());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_groceries, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar = view.findViewById(R.id.top_app_bar);
        recyclerView = view.findViewById(R.id.recycler_view_groceries);
        floatingActionButton = view.findViewById(R.id.add_button);

        adapter = new GroceriesAdapter(requireContext(), new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);


        model.getItems().observe(getViewLifecycleOwner(), new Observer<List<GroceryWithProduct>>() {
            @Override
            public void onChanged(List<GroceryWithProduct> groceryEntities) {
                List<GrocerieGroup> group = GrocerieGroup.fromList(groceryEntities);
                updateGroceries(group);
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.item_remove_check) {
                    //database.groceryDAO().deleteAllChecked();
                    model.deleteAllChecked();
                }
                if (item.getItemId() == R.id.item_remove_all) {
                    //database.groceryDAO().deleteAllItems();
                    model.deleteAllItems();
                }
                if (item.getItemId() == R.id.item_events_manager) {
                    EventRecipeFragment dialog = new EventRecipeFragment();
                    dialog.show(getParentFragmentManager(), "event_recipe_edit");
                }
                return true;
            }
        });

        adapter.setOnAdapterActionListener(new OnAdapterAction<GrocerieGroup>() {
            @Override
            public void onItemClick(GrocerieGroup item) {

            }

            @Override
            public void onItemClick(GrocerieGroup item, int action) {
                if (action == OnAdapterAction.ACTION_CHECK) {
                    model.checkGrocery(item, item.checked);
                }
            }

            @Override
            public void onItemLongClick(GrocerieGroup item, int position) {

            }
        });

        floatingActionButton.setOnClickListener(v -> {
            new ProductFragment(true).addProductActionListener(item -> {
                new GrocerieEditFragment(item, it -> {
                    model.addItem(it);
                }).show(getParentFragmentManager(), "edit_grocerie");
            }).showFullScreen(getParentFragmentManager());
        });

        recyclerView.setOnScrollChangeListener((v, sx, sy, osx, osy) -> {
            if (sy > osy) floatingActionButton.hide();
            else floatingActionButton.show();
        });
    }

    private void updateGroceries(List<GrocerieGroup> items) {

        items.sort(new Comparator<GrocerieGroup>() {
            @Override
            public int compare(GrocerieGroup t1, GrocerieGroup t2) {
                return t1.product.label.compareTo(t2.product.label);
            }
        });
        items.sort(new Comparator<GrocerieGroup>() {
            @Override
            public int compare(GrocerieGroup t1, GrocerieGroup t2) {
                return t1.product.secion - t2.product.secion;
            }
        });


        adapter.updateGroceries(new ArrayList<>(items));
    }
}