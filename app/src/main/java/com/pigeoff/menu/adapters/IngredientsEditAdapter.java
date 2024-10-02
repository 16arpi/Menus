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
import com.google.android.material.textfield.TextInputLayout;
import com.pigeoff.menu.R;
import com.pigeoff.menu.data.Ingredient;
import com.pigeoff.menu.util.Util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class IngredientsEditAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<Ingredient> ingredients;
    List<String> unitTypes;
    OnLabelClick listener;

    public IngredientsEditAdapter(Context context, List<Ingredient> ingredients) {
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

        if (ingredient.product != null) {
            itemHolder.editLabel.setText(ingredient.product.label);
            if (ingredient.product.id < 0) {
                itemHolder.editLabelLayout.setErrorEnabled(true);
                itemHolder.editLabelLayout.setError(context.getString(R.string.wizard_warning_label));
            } else {
                itemHolder.editLabelLayout.setErrorEnabled(false);
                itemHolder.editLabelLayout.setError("");
            }
        }
        itemHolder.editQuantity.setText(ingredient.quantity);

        itemHolder.editLabel.setOnClickListener(v -> {
            if (this.listener != null)
                this.listener.onLabelClick(ingredients.get(itemHolder.getAdapterPosition()), itemHolder.getAdapterPosition());
        });

        itemHolder.editQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                System.out.println(ingredient.quantity + " | " + charSequence.toString());
                ingredients.get(itemHolder.getAdapterPosition()).quantity = charSequence.toString();
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
    public int getItemCount() {
        return ingredients.size();
    }

    public static class IngredientEditViewHolder extends RecyclerView.ViewHolder {

        public TextInputEditText editLabel;
        public TextInputLayout editLabelLayout;
        public TextInputEditText editQuantity;
        public ImageButton buttonClose;

        public IngredientEditViewHolder(@NonNull View itemView) {
            super(itemView);
            editLabelLayout = itemView.findViewById(R.id.edit_ingredient_label_layout);
            editLabel = itemView.findViewById(R.id.edit_ingredient_label);
            editQuantity = itemView.findViewById(R.id.edit_ingredient_quantity);
            buttonClose = itemView.findViewById(R.id.button_close);
        }
    }

    public void switchItems(int start, int end) {
        Collections.swap(ingredients, start, end);
        notifyItemMoved(start, end);
    }

    public void addItem(Ingredient ingredient) {
        ingredients.add(ingredient);
        notifyItemInserted(ingredients.size());
    }

    public void updateItem(Ingredient ingredient, int position) {
        ingredients.set(position, ingredient);
        notifyItemChanged(position);
    }

    public void deleteItem(int position) {
        ingredients.remove(position);
        notifyItemRemoved(position);
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void addLabelClickListener(OnLabelClick listener) {
        this.listener = listener;
    }

    public interface OnLabelClick {
        void onLabelClick(Ingredient ingredient, int position);
    }
}
