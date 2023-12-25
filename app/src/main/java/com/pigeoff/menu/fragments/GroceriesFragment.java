package com.pigeoff.menu.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pigeoff.menu.R;
import com.pigeoff.menu.adapters.GroceriesAdapter;
import com.pigeoff.menu.adapters.OnAdapterAction;
import com.pigeoff.menu.data.GrocerieGroup;
import com.pigeoff.menu.database.GroceryEntity;
import com.pigeoff.menu.models.GroceriesViewModel;
import com.pigeoff.menu.util.Constants;
import com.pigeoff.menu.util.Util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GroceriesFragment extends Fragment {

    GroceriesViewModel model;
    MaterialToolbar toolbar;
    TextView textTitle;
    RecyclerView recyclerView;
    FloatingActionButton floatingActionButton;
    LinearLayout layoutEmpty;
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
    public void onResume() {
        super.onResume();
        Util.hideKeyboard(requireActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_groceries, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar = view.findViewById(R.id.top_app_bar);
        recyclerView = view.findViewById(R.id.recycler_view_groceries);
        textTitle = view.findViewById(R.id.text_title);
        floatingActionButton = view.findViewById(R.id.add_button);
        layoutEmpty = view.findViewById(R.id.layout_empty);

        adapter = new GroceriesAdapter(requireContext(), new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);


        model.getItems().observe(getViewLifecycleOwner(), groceryWithProducts -> {
            List<GrocerieGroup> group = GrocerieGroup.fromList(groceryWithProducts);
            layoutEmpty.setVisibility(group.size() > 0 ? View.GONE : View.VISIBLE);
            updateGroceries(group);
        });

        toolbar.setOnMenuItemClickListener(item -> {
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
        });

        adapter.setOnAdapterActionListener(new OnAdapterAction<GrocerieGroup>() {
            @Override
            public void onItemClick(GrocerieGroup item) {

            }

            @Override
            public void onItemClick(GrocerieGroup item, int action) {
                if (action == OnAdapterAction.ACTION_CHECK) {
                    model.checkGrocery(item, item.checked);
                } else if (action == OnAdapterAction.ACTION_ADD) {
                    addCustomGrocerie(item.section);
                }
            }

            @Override
            public void onItemLongClick(GrocerieGroup item, int position) {

            }
        });

        floatingActionButton.setOnClickListener(v -> addCustomGrocerie(Constants.SECTION_DIVERS));

        recyclerView.setOnScrollChangeListener((v, sx, sy, osx, osy) -> {
            if (sy > osy) floatingActionButton.hide();
            else floatingActionButton.show();
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                GrocerieGroup group = adapter.getGroup(viewHolder.getAdapterPosition());
                if (group != null) for (GroceryEntity g : group.groceries) model.deleteItem(g);
                else adapter.notifyItemChanged(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void updateGroceries(List<GrocerieGroup> items) {
        items.sort(Comparator.comparing(t -> t.product.label));
        items.sort(Comparator.comparingInt(t -> t.product.section));
        adapter.updateGroceries(new ArrayList<>(items));
    }

    private void addCustomGrocerie(int section) {
        GrocerieEditFragment fragment = GrocerieEditFragment.newInstance(section);
        fragment.addOnCallback((sec, label, it) -> model.addItemWithProduct(sec, label, it));
        fragment.show(getParentFragmentManager(), "edit_grocerie");
    }
}