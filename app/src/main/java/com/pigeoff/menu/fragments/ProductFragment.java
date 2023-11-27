package com.pigeoff.menu.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.pigeoff.menu.R;
import com.pigeoff.menu.adapters.ProductAdapter;
import com.pigeoff.menu.database.ProductEntity;
import com.pigeoff.menu.models.ProductViewModel;
import com.pigeoff.menu.util.Constants;
import com.pigeoff.menu.util.Util;

import java.util.ArrayList;
import java.util.List;

public class ProductFragment extends DialogFragment {

    private final boolean picker;
    private final int section;
    String query = "";

    ProductViewModel model;
    List<ProductEntity> products;
    ProductAdapter adapter;
    OnProductAction listener;
    TextInputEditText editSearch;
    FloatingActionButton floatingActionButton;
    RecyclerView recyclerView;
    ChipGroup chips;

    public ProductFragment(boolean picker, int section) {
        this.section = section;
        this.picker = picker;
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
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editSearch = view.findViewById(R.id.search_bar);
        floatingActionButton = view.findViewById(R.id.floating_action_button);
        chips = view.findViewById(R.id.chip_group_filter);
        recyclerView = view.findViewById(R.id.recycler_view);


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
            Util.showKeyboard(requireActivity());
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
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
        floatingActionButton.setOnClickListener(view -> {
            ProductEntity item = new ProductEntity();
            item.label = String.valueOf(editSearch.getText());
            ProductEditFragment editFragment = new ProductEditFragment(item, getSection());
            editFragment.setOnEditListener(new ProductEditFragment.OnEditListener() {
                @Override
                public void onSubmit(ProductEntity product) {
                    model.addProduct(item, requireActivity(), id -> {
                        if (picker && listener != null) {
                            product.id = id;
                            listener.onItemSelected(product);
                            dismissFullScreen(getParentFragmentManager());
                        }
                    });
                }
            });
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

        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        recyclerView.setAdapter(adapter);

        recyclerView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY > oldScrollY) floatingActionButton.hide();
            else floatingActionButton.show();
        });

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

        chips.setOnCheckedStateChangeListener((group, ids) -> {
            updateData();
        });

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

    public void updateData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Integer> checked = chips.getCheckedChipIds();
                List<ProductEntity> items = new ArrayList<>();
                for (ProductEntity p : products) {
                    if (Util.stringMatchSearch(p.label, query)) {
                        if (p.secion == 0 && checked.contains(R.id.chip_filter_groceries)) {
                            items.add(p);
                        } else if (p.secion == 1 && checked.contains(R.id.chip_filter_fruits)) {
                            items.add(p);
                        } else if (p.secion == 2 && checked.contains(R.id.chip_filter_meat)) {
                            items.add(p);
                        } else if (p.secion == 3 && checked.contains(R.id.chip_filter_fresh)) {
                            items.add(p);
                        } else if (p.secion == 4 && checked.contains(R.id.chip_filter_drinks)) {
                            items.add(p);
                        } else if (p.secion == 5 && checked.contains(R.id.chip_filter_divers)) {
                            items.add(p);
                        } else if (checked.size() == 0) {
                            items.add(p);
                        }
                    }
                }
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.updateItems(items);
                    }
                });
            }
        }).start();
    }

    private void chooseProduct(ProductEntity product) {
        if (picker) {
            listener.onItemSelected(product);
            dismissFullScreen(getParentFragmentManager());
        }
        else {
            ProductEditFragment editFragment = new ProductEditFragment(product, product.secion);
            editFragment.show(requireActivity().getSupportFragmentManager(), "edit");
            editFragment.setOnEditListener(p -> {
                model.updateProduct(p);
                editFragment.dismiss();
            });
        }
    }

    public void showFullScreen(FragmentManager fragmentManager) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(android.R.id.content, this)
                .addToBackStack(null)
                .commit();
    }

    public void dismissFullScreen(FragmentManager fragmentManager) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        transaction.remove(this).commit();
    }

    public interface OnProductAction {
        void onItemSelected(ProductEntity item);
    }
}
