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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.pigeoff.menu.MenuApplication;
import com.pigeoff.menu.R;
import com.pigeoff.menu.adapters.GroceriesAdapter;
import com.pigeoff.menu.adapters.OnAdapterAction;
import com.pigeoff.menu.database.GroceryEntity;
import com.pigeoff.menu.database.MenuDatabase;
import com.pigeoff.menu.database.ProductEntity;
import com.pigeoff.menu.util.Util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class GroceriesFragment extends Fragment {

    MaterialToolbar toolbar;
    RecyclerView recyclerView;
    MenuDatabase database;
    GroceriesAdapter adapter;
    HashMap<Long, ProductEntity> products;

    public GroceriesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MenuApplication app = (MenuApplication) requireActivity().getApplication();
        database = app.database;

        products = Util.productsToDict(database.productDAO().getAll());
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
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.item_remove_check) {
                    database.groceryDAO().deleteAllChecked();
                    updateGroceries();
                }
                if (item.getItemId() == R.id.item_remove_all) {
                    database.groceryDAO().deleteAllItems();
                    updateGroceries();
                }
                return true;
            }
        });

        adapter = new GroceriesAdapter(requireContext(), products, new ArrayList<>());
        recyclerView = view.findViewById(R.id.recycler_view_groceries);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        adapter.setOnAdapterActionListener(new OnAdapterAction<GroceryEntity>() {
            @Override
            public void onItemClick(GroceryEntity item) {

            }

            @Override
            public void onItemClick(GroceryEntity item, int action) {
                if (action == OnAdapterAction.ACTION_CHECK) {
                    database.groceryDAO().checkGrocery(item.id, item.checked);
                }
            }

            @Override
            public void onItemLongClick(GroceryEntity item, int position) {

            }
        });

        updateGroceries();
    }

    private void updateGroceries() {
        List<GroceryEntity> items = database.groceryDAO().getGroceries();

        items.sort(new Comparator<GroceryEntity>() {
            @Override
            public int compare(GroceryEntity t1, GroceryEntity t2) {
                ProductEntity p1 = products.get(t1.ingredientId);
                ProductEntity p2 = products.get(t2.ingredientId);
                return p1.label.compareTo(p2.label);
            }
        });
        items.sort(new Comparator<GroceryEntity>() {
            @Override
            public int compare(GroceryEntity t1, GroceryEntity t2) {
                ProductEntity p1 = products.get(t1.ingredientId);
                ProductEntity p2 = products.get(t2.ingredientId);
                return p1.secion - p2.secion;
            }
        });
        for (GroceryEntity g : items) System.out.println(products.get(g.ingredientId).secion);
        adapter.updateProducts(new ArrayList<>(items));
    }
}