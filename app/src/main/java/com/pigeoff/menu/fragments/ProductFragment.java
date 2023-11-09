package com.pigeoff.menu.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.pigeoff.menu.MenuApplication;
import com.pigeoff.menu.R;
import com.pigeoff.menu.adapters.ProductAdapter;
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
    String query = "";
    int tab = Constants.TAB_GROCERIES;

    OnProductAction listener;
    TextInputEditText editSearch;
    TabLayout tabLayout;
    RecyclerView recyclerView;
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
        recyclerView = view.findViewById(R.id.recycler_view);
        floatingActionButton = view.findViewById(R.id.floating_action_button);

        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        setupUI("", Constants.TAB_GROCERIES);

        if (picker) editSearch.requestFocus();

        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                query = charSequence.toString();
                setupUI(query, tab);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab t) {
                tab = t.getPosition();
                setupUI(query, tab);
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
                ProductEditFragment editFragment = new ProductEditFragment(new ProductEntity(), tab);
                editFragment.setOnEditListener(new ProductEditFragment.OnEditListener() {
                    @Override
                    public void onSubmit(ProductEntity product) {
                        product.id = database.productDAO().insertProduct(product);

                        if (picker && listener != null) {
                            listener.onItemSelected(product);
                            dismissFullScreen(getParentFragmentManager());
                        }
                        else {
                            setupUI(query, tab);
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

    private void setupUI(String query, int tab) {
        List<ProductEntity> all = database.productDAO().getAllFromSection(tab);
        ArrayList<ProductEntity> filtered = new ArrayList<>();
        for (ProductEntity p : all) {
            if (Util.stringMatchSearch(p.label, query)) filtered.add(p);
        }
        products = filtered;
        recyclerView.setAdapter(new ProductAdapter(requireContext(), products, new ProductAdapter.OnItemAction() {
            @Override
            public void onItemSelected(ProductEntity product) {
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
                            setupUI(query, tab);
                        }
                    });
                }
            }

            @Override
            public void onItemDeleted(ProductEntity product) {
                if (deleteProduct(product)) {
                    setupUI(query, tab);
                } else {
                    new MaterialAlertDialogBuilder(requireContext())
                            .setMessage(R.string.product_delete_dialog_message)
                            .setPositiveButton(R.string.product_delete_dialog_ok, null)
                            .show();
                }
            }
        }));
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
