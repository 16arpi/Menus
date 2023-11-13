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
import com.pigeoff.menu.util.Constants;
import com.pigeoff.menu.util.Util;

import java.util.ArrayList;
import java.util.List;

public class ProductFragment extends DialogFragment {

    private boolean picker = false;

    MenuDatabase database;

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

    public ProductFragment addProductActionListener(OnProductAction listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MenuApplication app = (MenuApplication) requireActivity().getApplication();
        database = app.database;
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
        //recyclerView = view.findViewById(R.id.recycler_view);
        viewPager = view.findViewById(R.id.view_pager);
        floatingActionButton = view.findViewById(R.id.floating_action_button);

        //recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        setupUI();
        updateData();

        if (picker) editSearch.requestFocus();

        editSearch.setAdapter(new ArrayAdapter<ProductEntity>(requireContext(), android.R.layout.simple_list_item_1, products));
        editSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView text = (TextView) view;
                String label = text.getText().toString();
                for (ProductEntity p : products) if (p.label == label) {
                    chooseProduct(p);
                    editSearch.setText("");
                }
            }
        });

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
                        product.id = database.productDAO().insertProduct(product);

                        if (picker && listener != null) {
                            listener.onItemSelected(product);
                            dismissFullScreen(getParentFragmentManager());
                        }
                        else {
                            products = database.productDAO().getAll();
                            adapter.updateData(products);
                        }
                    }
                });
                editFragment.show(requireActivity().getSupportFragmentManager(), "edit");
            }
        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    private void setupUI() {
        adapter = new ProductSectionAdapter(requireContext(), new ArrayList<>(), new ProductAdapter.OnItemAction() {
            @Override
            public void onItemSelected(ProductEntity product) {
                chooseProduct(product);
            }

            @Override
            public void onItemDeleted(ProductEntity product) {
                if (deleteProduct(product)) {
                    setupUI();
                } else {
                    new MaterialAlertDialogBuilder(requireContext())
                            .setMessage(R.string.product_delete_dialog_message)
                            .setPositiveButton(R.string.product_delete_dialog_ok, null)
                            .show();
                }
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
        products = database.productDAO().getAll();
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
                    database.productDAO().updateProduct(product);
                    editFragment.dismiss();
                    updateData();
                }
            });
        }
    }

    private boolean deleteProduct(ProductEntity product) {
        List<RecipeEntity> recipes = database.recipeDAO().select();

        for (RecipeEntity r : recipes) {
            List<Ingredient> ingredients = Ingredient.fromJson(Util.productsToDict(products), r.ingredients);
            for (Ingredient i : ingredients) {
                if (i.product.id == product.id) return false;
            }
        }

        database.productDAO().deleteProduct(product);
        return true;
    }

    public void showFullScreen(FragmentManager fragmentManager) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(android.R.id.content, this)
                .addToBackStack(null).commit();
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
