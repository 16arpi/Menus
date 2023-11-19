package com.pigeoff.menu.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.pigeoff.menu.R;
import com.pigeoff.menu.data.Ingredient;
import com.pigeoff.menu.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
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

        itemHolder.label.setText(ingredient.product.label);
        itemHolder.editValue.setText(ingredient.value > 0.0f ? Util.formatFloat(ingredient.value) : "");

        Util.selectUnitAutoCompleteItem(itemHolder.editUnit, ingredient.unit);


        itemHolder.editUnit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ingredient.unit = unitTypes.indexOf(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        itemHolder.editValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    ingredient.value = Float.parseFloat(charSequence.toString());
                } catch (Exception e) {
                    ingredient.value = 0.0f;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public static class IngredientEditViewHolder extends RecyclerView.ViewHolder {

        TextView label;
        TextInputEditText editValue;
        AutoCompleteTextView editUnit;

        public IngredientEditViewHolder(@NonNull View itemView) {
            super(itemView);
            label = itemView.findViewById(R.id.edit_ingredient_label);
            editValue = itemView.findViewById(R.id.edit_ingredient_value);
            editUnit = itemView.findViewById(R.id.edit_ingredient_unit);
        }
    }

    // TODO Switch items
    public void switchItems(int start, int end) {
        Ingredient iStart = ingredients.get(start);
        Ingredient iEnd = ingredients.get(end);

        ingredients.set(end, iStart);
        ingredients.set(start, iEnd);

        notifyItemChanged(start);
        notifyItemChanged(end);
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
