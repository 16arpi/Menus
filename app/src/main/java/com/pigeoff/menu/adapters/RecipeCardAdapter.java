package com.pigeoff.menu.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.pigeoff.menu.R;
import com.pigeoff.menu.database.RecipeEntity;
import com.pigeoff.menu.util.DiffUtilCallback;
import com.pigeoff.menu.util.Util;

import java.util.ArrayList;
import java.util.List;

public class RecipeCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final ArrayList<RecipeEntity> recipes;
    private final Context context;
    private OnAdapterAction<RecipeEntity> listener;

    public RecipeCardAdapter(Context context, ArrayList<RecipeEntity> recipes) {
        this.recipes = recipes;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CardListViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_recipe, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CardListViewHolder recipeViewHolder = (CardListViewHolder) holder;
        RecipeEntity entity = recipes.get(position);

        if (entity.title.length() > 1) {
            String letter = entity.title.substring(0, 1).toUpperCase();
            recipeViewHolder.itemLetter.setText(letter);
        } else {
            recipeViewHolder.itemLetter.setText("R");
        }

        Util.injectImage(context, entity.picturePath, recipeViewHolder.imageView);
        recipeViewHolder.itemTitle.setText(entity.title);
        recipeViewHolder.itemSubTitle.setText(Util.getRecipesTypes(context, entity.category));
        recipeViewHolder.cardItem.setOnClickListener(view -> listener.onItemClick(entity));
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public void updateRecipes(List<RecipeEntity> newItems) {
        DiffUtilCallback<RecipeEntity> utilCallback = new DiffUtilCallback<>(recipes, newItems, (new DiffUtilCallback.DifferenceCallback<RecipeEntity>() {
            @Override
            public boolean sameItem(RecipeEntity oldElement, RecipeEntity newElement) {
                return oldElement.id == newElement.id;
            }

            @Override
            public boolean sameContent(RecipeEntity oldElement, RecipeEntity newElement) {
                return oldElement.equals(newElement);
            }
        }));

        DiffUtil.DiffResult result = DiffUtil.calculateDiff(utilCallback);
        recipes.clear();
        recipes.addAll(newItems);
        result.dispatchUpdatesTo(this);
    }

    public void setOnAdapterAction(OnAdapterAction<RecipeEntity> listener) {
        this.listener = listener;
    }

}
