package com.pigeoff.menu.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.pigeoff.menu.R;
import com.pigeoff.menu.adapters.ProductAdapter;
import com.pigeoff.menu.database.ProductEntity;
import com.pigeoff.menu.models.ProductViewModel;
import com.pigeoff.menu.util.Constants;
import com.pigeoff.menu.util.Util;

import java.util.ArrayList;
import java.util.List;

public class ProductFragment extends BottomSheetDialogFragment {

    private boolean fullscreen;
    private boolean picker;
    private int section;
    String query = "";

    ProductViewModel model;
    List<ProductEntity> products;
    ProductAdapter adapter;
    OnProductAction listener;
    TextInputLayout editSearchLayout;
    TextInputEditText editSearch;
    RecyclerView recyclerView;
    ChipGroup chips;

    public ProductFragment() {

    }

    public static ProductFragment newInstance(boolean fullscreen, boolean picker, int section) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.BUNDLE_FULLSCREEN, fullscreen);
        bundle.putBoolean(Constants.BUNDLE_PICKER, picker);
        bundle.putInt(Constants.BUNDLE_SECTION, section);

        ProductFragment fragment = new ProductFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public ProductFragment addProductActionListener(OnProductAction listener) {
        this.listener = listener;
        return this;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        model = new ProductViewModel(requireActivity().getApplication());
        products = new ArrayList<>();

        Bundle bundle = getArguments();
        if (bundle != null) {
            this.section = bundle.getInt(Constants.BUNDLE_SECTION, Constants.SECTION_EMPTY);
            this.picker = bundle.getBoolean(Constants.BUNDLE_PICKER, false);
            this.fullscreen = bundle.getBoolean(Constants.BUNDLE_FULLSCREEN, false);
            System.out.println("Setting bundle arguments");
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NestedScrollView nestedScrollView = view.findViewById(R.id.nested_scroll_view);
        editSearchLayout = view.findViewById(R.id.search_bar_layout);
        editSearch = view.findViewById(R.id.search_bar);
        chips = view.findViewById(R.id.chip_group_filter);
        recyclerView = view.findViewById(R.id.recycler_view);

        if (!fullscreen) {
            nestedScrollView.setBackgroundColor(0);
            System.out.println("Fond transparent");
        }

        if (picker) {
            Util.showKeyboard(editSearch, requireContext());
        }

        model.getItems().observe(getViewLifecycleOwner(), productEntities -> {
            products = productEntities;
            updateData();
        });

        setupUI();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (picker) {
            editSearch.requestFocus();
            Util.showKeyboard(editSearch, requireContext());
        }
    }

    @Override
    public void show(@NonNull FragmentManager manager, @Nullable String tag) {
        Bundle args = getArguments();
        if (args != null && args.getBoolean(Constants.BUNDLE_FULLSCREEN, false)) {
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.add(android.R.id.content, this)
                    .addToBackStack(null)
                    .commit();
        } else {
            System.out.println("Pas fullscreen !!!");
            System.out.println(this.fullscreen);
            super.show(manager, tag);
        }
    }

    public void dismiss(FragmentManager manager) {
        if (this.fullscreen) {
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            transaction.remove(this).commit();
        } else {
            super.dismiss();
        }
    }

    private int getSection() {
        if (chips.getCheckedChipIds().size() == 1) {
            int id = chips.getCheckedChipIds().get(0);
            if (id == R.id.chip_filter_groceries) {
                return 0;
            } else if (id == R.id.chip_filter_fruits) {
                return 1;
            } else if (id == R.id.chip_filter_meat) {
                return 2;
            } else if (id == R.id.chip_filter_fresh) {
                return 3;
            } else if (id == R.id.chip_filter_drinks) {
                return 4;
            } else if (id == R.id.chip_filter_divers) {
                return 5;
            }
        }

        return Constants.TAB_GROCERIES;
    }

    private void setupUI() {

        editSearchLayout.setEndIconOnClickListener(v -> {
            ProductEntity item = new ProductEntity();
            item.label = String.valueOf(editSearch.getText());
            ProductEditFragment editFragment = ProductEditFragment.newInstance(item, getSection());
            editFragment.setOnEditListener(product ->
                    model.addProduct(item, requireActivity(), id -> {
                        if (picker && listener != null) {
                            product.id = id;
                            listener.onItemSelected(product);
                            dismiss(getParentFragmentManager());
                        }
                    })
            );
            editFragment.show(requireActivity().getSupportFragmentManager(), "edit");
        });

        adapter = new ProductAdapter(requireContext(), new ArrayList<>(), new ProductAdapter.OnItemAction() {
            @Override
            public void onItemSelected(ProductEntity product) {
                chooseProduct(product);
            }

            @Override
            public void onItemDeleted(ProductEntity product) {
                model.deleteProduct(product, products, requireActivity(), new ProductViewModel.ProductDeleteCallback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFailure() {
                        new MaterialAlertDialogBuilder(requireContext())
                                .setMessage(R.string.product_delete_dialog_message)
                                .setPositiveButton(R.string.product_delete_dialog_ok, null)
                                .show();
                    }
                });
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                query = charSequence.toString();
                updateData();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        chips.setOnCheckedStateChangeListener((group, ids) -> updateData());

        if (section >= 0) {
            if (section == 0) {
                chips.check(R.id.chip_filter_groceries);
            } else if (section == 1) {
                chips.check(R.id.chip_filter_fruits);
            } else if (section == 2) {
                chips.check(R.id.chip_filter_meat);
            } else if (section == 3) {
                chips.check(R.id.chip_filter_fresh);
            } else if (section == 4) {
                chips.check(R.id.chip_filter_drinks);
            } else if (section == 5) {
                chips.check(R.id.chip_filter_divers);
            }
        }

    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    public void updateData() {
        new Thread(() -> {
            List<Integer> checked = chips.getCheckedChipIds();
            List<ProductEntity> items = new ArrayList<>();
            for (ProductEntity p : products) {
                if (Util.stringMatchSearch(p.label, query)) {
                    if (p.section == 0 && checked.contains(R.id.chip_filter_groceries)) {
                        items.add(p);
                    } else if (p.section == 1 && checked.contains(R.id.chip_filter_fruits)) {
                        items.add(p);
                    } else if (p.section == 2 && checked.contains(R.id.chip_filter_meat)) {
                        items.add(p);
                    } else if (p.section == 3 && checked.contains(R.id.chip_filter_fresh)) {
                        items.add(p);
                    } else if (p.section == 4 && checked.contains(R.id.chip_filter_drinks)) {
                        items.add(p);
                    } else if (p.section == 5 && checked.contains(R.id.chip_filter_divers)) {
                        items.add(p);
                    } else if (checked.size() == 0) {
                        items.add(p);
                    }
                }
            }
            requireActivity().runOnUiThread(() -> adapter.updateItems(items));
        }).start();
    }

    private void chooseProduct(ProductEntity product) {
        if (picker) {
            listener.onItemSelected(product);
            dismiss(getParentFragmentManager());
        }
        else {
            ProductEditFragment editFragment = ProductEditFragment.newInstance(product, product.section);
            editFragment.show(requireActivity().getSupportFragmentManager(), "edit");
            editFragment.setOnEditListener(p -> {
                model.updateProduct(p);
                editFragment.dismiss();
            });
        }
    }

    public interface OnProductAction {
        void onItemSelected(ProductEntity item);
    }
}
