package com.pigeoff.menu.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.pigeoff.menu.R;
import com.pigeoff.menu.database.GroceryEntity;
import com.pigeoff.menu.database.ProductEntity;
import com.pigeoff.menu.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;


public class GroceriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    boolean bind = false;
    Context context;
    ArrayList<GroceryEntity> items;
    HashMap<Long, ProductEntity> products;
    OnAdapterAction<GroceryEntity> listener;


    private static final int VIEW_GROCERY = 6;
    private static final int VIEW_SECTION_GROCERIES = 0;
    private static final int VIEW_SECTION_FRUITS = 1;
    private static final int VIEW_SECTION_MEAT = 2;
    private static final int VIEW_SECTION_FRESH = 3;
    private static final int VIEW_SECTION_DRINKS = 4;
    private static final int VIEW_SECTION_DIVERS = 5;

    public GroceriesAdapter(Context context, HashMap<Long, ProductEntity> products, ArrayList<GroceryEntity> items) {
        this.context = context;
        this.products = products;
        this.items = items;
    }

    @Override
    public int getItemViewType(int position) {
        int[] viewTypes = new int[getItemCount()];

        int lastHeader = -1;
        int x = 0;
        int i = 0;
        while (x < items.size()) {
            GroceryEntity g = items.get(x);
            ProductEntity p = products.get(g.ingredientId);

            if (p.secion != lastHeader) {
                viewTypes[i] = p.secion;
                viewTypes[i+1] = VIEW_GROCERY;
                lastHeader = p.secion;
                i += 2;
            } else {
                viewTypes[i] = VIEW_GROCERY;
                i++;
            }

            x++;
        }

        return viewTypes[position];
    }

    public GroceryEntity getProductAt(int position) {
        GroceryEntity[] gList = new GroceryEntity[getItemCount()];

        int lastHeader = -1;
        int x = 0;
        int i = 0;
        while (x < items.size()) {
            GroceryEntity g = items.get(x);
            ProductEntity p = products.get(g.ingredientId);

            if (p.secion != lastHeader) {
                gList[i] = null;
                gList[i+1] = g;
                i += 2;
                lastHeader = p.secion;
            } else {
                gList[i] = g;
                i++;
            }

            x++;
        }

        return gList[position];
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_GROCERY) {
            return new GroceriesViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_check, parent, false));
        } else {
            return new SectionViewHolder(viewType, LayoutInflater.from(context).inflate(R.layout.adapter_section, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof GroceriesViewHolder) {
            GroceryEntity item = getProductAt(position);
            System.out.println("Last position");
            System.out.println(position);
            ProductEntity product = products.get(item.ingredientId);
            GroceriesViewHolder groceriesHolder = (GroceriesViewHolder) holder;

            bind = true;
            groceriesHolder.checkBox.setChecked(item.checked);
            paintCheckText(groceriesHolder.label, item.checked);
            paintCheckText(groceriesHolder.labelRecipe, item.checked);

            groceriesHolder.label.setText(product.label);
            groceriesHolder.labelValueUnit.setText(Util.formatIngredient(item.value, item.unit));
            if (item.recipeLabel != null && !item.recipeLabel.isEmpty()) {
                groceriesHolder.labelRecipe.setVisibility(View.VISIBLE);

                String date = Util.formatDate(item.datetime);
                String data = String.format("%s · %s", item.recipeLabel, date);

                groceriesHolder.labelRecipe.setText(data);
            } else {
                groceriesHolder.labelRecipe.setVisibility(View.GONE);
            }
            bind = false;

            groceriesHolder.checkBox.addOnCheckedStateChangedListener(new MaterialCheckBox.OnCheckedStateChangedListener() {
                @Override
                public void onCheckedStateChangedListener(@NonNull MaterialCheckBox checkBox, int state) {
                    if (bind) return;
                    item.checked = !item.checked;
                    listener.onItemClick(item, OnAdapterAction.ACTION_CHECK);

                    paintCheckText(groceriesHolder.label, item.checked);
                    paintCheckText(groceriesHolder.labelRecipe, item.checked);
                }
            });

            groceriesHolder.buttonAction.setVisibility(View.GONE);
        } else {
            SectionViewHolder sectionViewHolder = (SectionViewHolder) holder;
            sectionViewHolder.section.setText(Util.getSectionsLabel(context)[sectionViewHolder.type]);
        }
    }

    @Override
    public int getItemCount() {
        TreeSet<Integer> count = new TreeSet<>();
        for (GroceryEntity g : items) {
            ProductEntity p = products.get(g.ingredientId);
            count.add(p.secion);
        }
        return items.size() + count.size();
    }

    private class GroceriesViewHolder extends RecyclerView.ViewHolder {

        MaterialCheckBox checkBox;
        TextView label;
        TextView labelValueUnit;
        TextView labelRecipe;
        ImageButton buttonAction;

        public GroceriesViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox);
            label = itemView.findViewById(R.id.text_label);
            labelValueUnit = itemView.findViewById(R.id.text_label_value_unit);
            labelRecipe = itemView.findViewById(R.id.text_sub_label);
            buttonAction = itemView.findViewById(R.id.button_action);
        }
    }

    private class SectionViewHolder extends RecyclerView.ViewHolder {

        TextView section;
        int type;

        public SectionViewHolder(int viewType, @NonNull View itemView) {
            super(itemView);
            type = viewType;
            section = itemView.findViewById(R.id.text_title);
        }
    }

    private void paintCheckText(TextView view, boolean check) {
        if (check) {
            view.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        } else {
            view.setPaintFlags(Paint.ANTI_ALIAS_FLAG);
        }
    }

    public void setOnAdapterActionListener(OnAdapterAction<GroceryEntity> listener) {
        this.listener = listener;
    }

    public void updateProducts(ArrayList<GroceryEntity> newProducts) {
        items = newProducts;
        notifyDataSetChanged();
    }
}
