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
import com.pigeoff.menu.database.MenuDatabase;
import com.pigeoff.menu.database.ProductEntity;

import java.util.ArrayList;
import java.util.List;

public class GroceriesFragment extends Fragment {

    MaterialToolbar toolbar;
    RecyclerView recyclerView;
    MenuDatabase database;
    GroceriesAdapter adapter;

    public GroceriesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MenuApplication app = (MenuApplication) requireActivity().getApplication();
        database = app.database;
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

        toolbar = view.findViewById(R.id.topAppBar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.item_remove_check) {
                    database.productDAO().deleteAllChecked();
                    updateGroceries();
                }
                return true;
            }
        });

        adapter = new GroceriesAdapter(requireContext(), new ArrayList<>());
        recyclerView = view.findViewById(R.id.recyclerViewGroceries);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        adapter.setOnAdapterActionListener(new OnAdapterAction<ProductEntity>() {
            @Override
            public void onItemClick(ProductEntity item) {

            }

            @Override
            public void onItemClick(ProductEntity item, int action) {
                if (action == OnAdapterAction.ACTION_CHECK) {
                    database.productDAO().checkProduct(item.id, item.checked);
                }
            }

            @Override
            public void onItemLongClick(ProductEntity item, int position) {

            }
        });

        updateGroceries();
    }

    private void updateGroceries() {
        List<ProductEntity> products = database.productDAO().getProducts();
        adapter.updateProducts(new ArrayList<>(products));
    }
}