package com.pigeoff.menu.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.pigeoff.menu.R;
import com.pigeoff.menu.data.Ingredient;
import com.pigeoff.menu.database.ProductEntity;
import com.pigeoff.menu.util.Util;

import java.util.ArrayList;


public class GroceriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    boolean bind = false;
    Context context;
    ArrayList<ProductEntity> items;
    OnAdapterAction<ProductEntity> listener;

    public GroceriesAdapter(Context context, ArrayList<ProductEntity> products) {
        this.context = context;
        this.items = products;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GroceriesViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_check, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ProductEntity product = items.get(position);
        GroceriesViewHolder groceriesHolder = (GroceriesViewHolder) holder;

        bind = true;
        groceriesHolder.checkBox.setChecked(product.checked);
        paintCheckText(groceriesHolder.label, product.checked);
        paintCheckText(groceriesHolder.labelRecipe, product.checked);

        groceriesHolder.label.setText(Util.formatIngredient(product.value, product.unit, product.label));
        if (product.recipeLabel != null && !product.recipeLabel.isEmpty()) {
            groceriesHolder.labelRecipe.setVisibility(View.VISIBLE);

            String date = Util.formatDate(product.datetime);
            String data = String.format("%s · %s", product.recipeLabel, date);
            groceriesHolder.labelRecipe.setText(data);
        } else {
            groceriesHolder.labelRecipe.setVisibility(View.GONE);
        }
        bind = false;

        groceriesHolder.checkBox.addOnCheckedStateChangedListener(new MaterialCheckBox.OnCheckedStateChangedListener() {
            @Override
            public void onCheckedStateChangedListener(@NonNull MaterialCheckBox checkBox, int state) {
                if (bind) return;
                product.checked = !product.checked;
                listener.onItemClick(product, OnAdapterAction.ACTION_CHECK);
                items.set(groceriesHolder.getAdapterPosition(), product);

                paintCheckText(groceriesHolder.label, product.checked);
                paintCheckText(groceriesHolder.labelRecipe, product.checked);
            }
        });

        groceriesHolder.buttonAction.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private class GroceriesViewHolder extends RecyclerView.ViewHolder {

        MaterialCheckBox checkBox;
        TextView label;
        TextView labelRecipe;
        ImageButton buttonAction;

        public GroceriesViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkBox);
            label = itemView.findViewById(R.id.textLabel);
            labelRecipe = itemView.findViewById(R.id.textRecipe);
            buttonAction = itemView.findViewById(R.id.buttonAction);
        }
    }

    private void paintCheckText(TextView view, boolean check) {
        if (check) {
            view.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        } else {
            view.setPaintFlags(Paint.ANTI_ALIAS_FLAG);
        }
    }

    public void setOnAdapterActionListener(OnAdapterAction<ProductEntity> listener) {
        this.listener = listener;
    }

    public void updateProducts(ArrayList<ProductEntity> newProducts) {
        items = newProducts;
        notifyDataSetChanged();
    }
}
