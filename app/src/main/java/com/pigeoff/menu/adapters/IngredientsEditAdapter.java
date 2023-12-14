package com.pigeoff.menu.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.pigeoff.menu.R;
import com.pigeoff.menu.data.Ingredient;
import com.pigeoff.menu.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class IngredientsEditAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    ArrayList<Ingredient> ingredients;
    List<String> unitTypes;

    public IngredientsEditAdapter(Context context, ArrayList<Ingredient> ingredients) {
        this.context = context;
        this.ingredients = ingredients;
        this.unitTypes = Arrays.asList(Util.getUnitsLabel(context));
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new IngredientEditViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_ingredient, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        IngredientEditViewHolder itemHolder = (IngredientEditViewHolder) holder;
        Ingredient ingredient = ingredients.get(position);

        if (ingredient.product != null) itemHolder.editLabel.setText(ingredient.product.label);
        itemHolder.editQuantity.setText(ingredient.quantity);

        itemHolder.editQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ingredient.quantity = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        itemHolder.buttonClose.setOnClickListener(v -> {
            itemHolder.editQuantity.clearFocus();
            itemHolder.editLabel.clearFocus();
            deleteItem(itemHolder.getAdapterPosition());
        });


    }

    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        System.out.println("HELLO WORLD");
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public static class IngredientEditViewHolder extends RecyclerView.ViewHolder {

        public TextInputEditText editLabel;
        public TextInputEditText editQuantity;
        public ImageButton buttonClose;

        public IngredientEditViewHolder(@NonNull View itemView) {
            super(itemView);
            editLabel = itemView.findViewById(R.id.edit_ingredient_label);
            editQuantity = itemView.findViewById(R.id.edit_ingredient_quantity);
            buttonClose = itemView.findViewById(R.id.button_close);
        }
    }

    // TODO Switch items
    public void switchItems(int start, int end) {
        Collections.swap(ingredients, start, end);
        notifyItemMoved(start, end);
    }

    public void addItem(Ingredient ingredient) {
        ingredients.add(ingredient);
        notifyItemInserted(ingredients.size());
    }

    public void deleteItem(int position) {
        ingredients.remove(position);
        notifyItemRemoved(position);
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }
}
