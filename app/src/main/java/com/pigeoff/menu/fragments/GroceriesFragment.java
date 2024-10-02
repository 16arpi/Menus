package com.pigeoff.menu.fragments;

import android.content.DialogInterface;
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
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
import java.util.HashMap;
import java.util.List;
import java.util.Set;

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
                // Commented for now because it conflicts with
                // the dragging feature...
                // => TODO necessity to find a workaround!

                // changeGrocerieCategory(item);
            }
        });

        floatingActionButton.setOnClickListener(v -> addCustomGrocerie(Constants.SECTION_DIVERS));

        recyclerView.setOnScrollChangeListener((v, sx, sy, osx, osy) -> {
            if (sy > osy) floatingActionButton.hide();
            else floatingActionButton.show();
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT) {

            HashMap<GrocerieGroup, Integer> commits = new HashMap<>();

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder current, @NonNull RecyclerView.ViewHolder target) {
                List<GrocerieGroup> items = adapter.getItems();
                int currentPos = current.getAdapterPosition();
                int targetPos = target.getAdapterPosition();

                GrocerieGroup objCurrent = items.get(currentPos);
                GrocerieGroup objTarget = items.get(targetPos);

                int viewTypeTarget = adapter.getItemViewType(targetPos);

                String labelCurrent = objCurrent.label;
                String labelTarget = objTarget.label;

                // Checking if moving element can come here
                boolean goesDown = currentPos < targetPos;

                // If element going down
                if (goesDown) {
                    if (targetPos == items.size() - 1) { // if last element of the list
                        if (labelCurrent.compareTo(labelTarget) > 0) {
                            for (int i = currentPos; i < targetPos; ++i) {
                                adapter.switchItems(i, i + 1);
                            }
                        }
                    } else {
                        int viewTypeTargetNext = adapter.getItemViewType(targetPos + 1);
                        String labelTargetNext = items.get(targetPos + 1).label;
                        if (viewTypeTarget != GroceriesAdapter.VIEW_GROCERY) { // If after header
                            if (labelCurrent.compareTo(labelTargetNext) < 0) {
                                for (int i = currentPos; i < targetPos; ++i) {
                                    adapter.switchItems(i, i + 1);
                                }
                            }
                        } else {
                            if (labelCurrent.compareTo(labelTarget) > 0) {
                                if (viewTypeTargetNext != GroceriesAdapter.VIEW_GROCERY || labelCurrent.compareTo(labelTargetNext) < 0) {
                                    for (int i = currentPos; i < targetPos; ++i) {
                                        adapter.switchItems(i, i + 1);
                                    }
                                }
                            }
                        }
                    }
                } else { // if going up
                    int viewTypeTargetNext = adapter.getItemViewType(targetPos - 1);
                    String labelTargetNext = items.get(targetPos - 1).label;

                    if (viewTypeTarget != GroceriesAdapter.VIEW_GROCERY) { // If after header
                        if (labelCurrent.compareTo(labelTargetNext) > 0) {
                            for (int i = currentPos; i > targetPos; --i) {
                                adapter.switchItems(i, i - 1);
                            }
                        }
                    } else {
                        if (labelCurrent.compareTo(labelTarget) < 0) {
                            if (viewTypeTargetNext != GroceriesAdapter.VIEW_GROCERY || labelCurrent.compareTo(labelTargetNext) > 0) {
                                for (int i = currentPos; i > targetPos; --i) {
                                    adapter.switchItems(i, i - 1);
                                }
                            }
                        }
                    }
                }

                return false;
            }

            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(
                        adapter.getItemViewType(viewHolder.getAdapterPosition()) == GroceriesAdapter.VIEW_GROCERY ?
                        ItemTouchHelper.UP | ItemTouchHelper.DOWN : 0,
                        ItemTouchHelper.RIGHT);
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                GrocerieGroup group = adapter.getGroup(viewHolder.getAdapterPosition());
                if (group != null) for (GroceryEntity g : group.groceries) model.deleteItem(g);
                else adapter.notifyItemChanged(viewHolder.getAdapterPosition());
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);

                int actualSection = 0;
                List<GrocerieGroup> items = adapter.getItems();
                for (int i = 0; i < items.size(); ++i) {
                    GrocerieGroup g = items.get(i);
                    if (adapter.getItemViewType(i) != GroceriesAdapter.VIEW_GROCERY)
                        actualSection =  g.section;
                    else {
                        g.section = actualSection;
                        model.changeGrocerieSection(g, g.section);
                    }
                }
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void updateGroceries(List<GrocerieGroup> items) {
        items.sort(Comparator.comparing(t -> t.product.label));
        items.sort(Comparator.comparingInt(t -> t.product.section));
        adapter.updateGroceries(new ArrayList<>(items));
    }

    private void changeGrocerieCategory(GrocerieGroup group) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.groceries_change_category)
                .setItems(R.array.section, (dialog, which) -> {
                    model.changeGrocerieSection(group, which);
                })
                .show();
    }

    private void addCustomGrocerie(int section) {
        GrocerieEditFragment fragment = GrocerieEditFragment.newInstance(section);
        fragment.addOnCallback((sec, label, it) -> model.addItemWithProduct(sec, label, it));
        fragment.show(getParentFragmentManager(), "edit_grocerie");
    }
}