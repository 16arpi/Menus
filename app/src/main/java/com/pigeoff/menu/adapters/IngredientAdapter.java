package com.pigeoff.menu.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pigeoff.menu.R;
import com.pigeoff.menu.data.Ingredient;
import com.pigeoff.menu.database.ProductEntity;
import com.pigeoff.menu.util.Util;

import java.util.ArrayList;
import java.util.HashMap;

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
        itemHolder.buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = itemHolder.getAdapterPosition();
                deleteItem(pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

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
