package com.pigeoff.menu.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pigeoff.menu.R;
import com.pigeoff.menu.data.Ingredient;
import com.pigeoff.menu.util.Util;

import java.util.ArrayList;

public class IngredientAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    ArrayList<Ingredient> ingredients;
    boolean editable;

    public IngredientAdapter(Context context, ArrayList<Ingredient> ingredients, boolean editable) {
        this.context = context;
        this.ingredients = ingredients;
        this.editable = editable;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SimpleListViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SimpleListViewHolder itemHolder = (SimpleListViewHolder) holder;
        Ingredient ingredient = ingredients.get(position);
        itemHolder.textItem.setText(Util.formatIngredient(ingredient));

        if (editable) {
            itemHolder.buttonClose.setVisibility(View.VISIBLE);
        }
        else {
            itemHolder.buttonClose.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }
}
