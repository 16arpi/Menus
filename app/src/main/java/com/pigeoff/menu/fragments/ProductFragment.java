package com.pigeoff.menu.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.pigeoff.menu.MenuApplication;
import com.pigeoff.menu.R;
import com.pigeoff.menu.adapters.ProductAdapter;
import com.pigeoff.menu.adapters.ProductSectionAdapter;
import com.pigeoff.menu.data.Ingredient;
import com.pigeoff.menu.database.MenuDatabase;
import com.pigeoff.menu.database.ProductEntity;
import com.pigeoff.menu.database.RecipeEntity;
import com.pigeoff.menu.models.ProductViewModel;
import com.pigeoff.menu.util.Constants;
import com.pigeoff.menu.util.Util;

import java.util.ArrayList;
import java.util.List;

public class ProductFragment extends DialogFragment {

    private boolean picker = false;

    ProductViewModel model;
    List<ProductEntity> products;

    ProductSectionAdapter adapter;
    int tab = Constants.TAB_GROCERIES;

    OnProductAction listener;
    AutoCompleteTextView editSearch;
    TabLayout tabLayout;
    ViewPager2 viewPager;
    FloatingActionButton floatingActionButton;

    public ProductFragment(boolean picker) {
        this.picker = picker;
    }

    @Override
    public void onStart() {
        super.onStart();
        /*Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }*/
    }

    public ProductFragment addProductActionListener(OnProductAction listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*setStyle(DialogFragment.STYLE_NO_FRAME, getTheme());*/

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
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.view_pager);
        floatingActionButton = view.findViewById(R.id.floating_action_button);


        model.getItems().observe(getViewLifecycleOwner(), new Observer<List<ProductEntity>>() {
            @Override
            public void onChanged(List<ProductEntity> productEntities) {
                products = productEntities;
                editSearch.setAdapter(
                        new ArrayAdapter<>(requireContext(),
                                android.R.layout.simple_list_item_1, products)
                );
                updateData();
            }
        });

        setupUI();

        if (picker) editSearch.requestFocus();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    private void setupUI() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab t) {
                tab = t.getPosition();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProductEntity item = new ProductEntity();
                item.label = editSearch.getText().toString();
                ProductEditFragment editFragment = new ProductEditFragment(item, tab);
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
            }
        });

        adapter = new ProductSectionAdapter(requireContext(), new ArrayList<>(), new ProductAdapter.OnItemAction() {
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

        viewPager.setAdapter(adapter);

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                String[] sections = requireContext().getResources().getStringArray(R.array.section);
                tab.setText(sections[position]);
            }
        });

        tabLayoutMediator.attach();

    }

    public void updateData() {
        adapter.updateData(products);
    }

    private void chooseProduct(ProductEntity product) {
        if (picker) {
            listener.onItemSelected(product);
            dismissFullScreen(getParentFragmentManager());
        }
        else {
            ProductEditFragment editFragment = new ProductEditFragment(product, tab);
            editFragment.show(requireActivity().getSupportFragmentManager(), "edit");
            editFragment.setOnEditListener(new ProductEditFragment.OnEditListener() {
                @Override
                public void onSubmit(ProductEntity product) {
                    model.updateProduct(product);
                    editFragment.dismiss();
                }
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
