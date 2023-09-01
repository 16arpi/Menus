package com.pigeoff.menu.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.pigeoff.menu.R;
import com.pigeoff.menu.database.RecipeEntity;
import com.pigeoff.menu.util.Util;

import java.util.ArrayList;

public class RecipeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<RecipeEntity> recipes;
    private Context context;
    private OnAdapterAction listener;

    public RecipeAdapter(Context context, ArrayList<RecipeEntity> recipes) {
        this.recipes = recipes;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecipeViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_recipe, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RecipeViewHolder recipeViewHolder = (RecipeViewHolder) holder;
        RecipeEntity entity = recipes.get(position);

        if (entity.title.length() > 1) {
            String letter = entity.title.substring(0, 1).toUpperCase();
            recipeViewHolder.itemLetter.setText(letter);
        } else {
            recipeViewHolder.itemLetter.setText("R");
        }

        recipeViewHolder.itemTitle.setText(entity.title);
        recipeViewHolder.itemSubTitle.setText(Util.getRecipesTypes(context, entity.category));
        recipeViewHolder.cardItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(entity);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public void addRecipe(RecipeEntity recipe) {
        recipes.add(0, recipe);
        notifyItemInserted(0);
    }

    public void editRecipe(RecipeEntity recipe, int position) {
        recipes.set(position, recipe);
        notifyItemChanged(position);
    }

    public void deleteRecipe(RecipeEntity recipe, int position) {
        recipes.remove(position);
        notifyItemRemoved(position);
    }

    public void updateRecipes(ArrayList<RecipeEntity> newRecipes) {
        recipes = newRecipes;
        notifyDataSetChanged();
    }

    public void setOnAdapterAction(OnAdapterAction listener) {
        this.listener = listener;
    }

    private class RecipeViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout cardItem;
        public TextView itemLetter;
        public TextView itemTitle;
        public TextView itemSubTitle;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            cardItem = itemView.findViewById(R.id.card_item);
            itemLetter = itemView.findViewById(R.id.text_letter);
            itemTitle = itemView.findViewById(R.id.text_recipe);
            itemSubTitle = itemView.findViewById(R.id.text_recipe_sub);
        }
    }

}
