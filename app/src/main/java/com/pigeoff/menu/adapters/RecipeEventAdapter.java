package com.pigeoff.menu.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.pigeoff.menu.R;
import com.pigeoff.menu.data.EventRecipe;
import com.pigeoff.menu.database.GroceryEntity;
import com.pigeoff.menu.util.Util;

import java.util.List;

public class RecipeEventAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final List<EventRecipe> items;
    private final OnAdapterAction listener;

    public RecipeEventAdapter(Context context, List<EventRecipe> items, OnAdapterAction listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecipeEventViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_event_recipe, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int position) {
        RecipeEventViewHolder holder = (RecipeEventViewHolder) h;
        EventRecipe item = items.get(position);

        holder.title.setText(item.recipeLabel);
        holder.subtitle.setText(Util.formatDate(item.datetime));
        holder.options.setOnClickListener(view -> {
            PopupMenu optionMenu = new PopupMenu(context, view);
            optionMenu.getMenuInflater().inflate(R.menu.recipe_event_menu, optionMenu.getMenu());
            optionMenu.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId() == R.id.item_delete) {
                    deleteItem(holder.getAdapterPosition());
                }
                return true;
            });
            optionMenu.show();
        });

        int totalIngredients = item.groceries.size();
        int checkedIngredients = 0;
        for (GroceryEntity g : item.groceries) if (g.checked) checkedIngredients++;

        holder.progress.setMax(totalIngredients);
        holder.progress.setProgress(checkedIngredients);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void deleteItem(int position) {
        EventRecipe item = items.get(position);
        listener.onItemDeleted(item);
        items.remove(position);
        notifyItemRemoved(position);
    }

    public interface OnAdapterAction {
        void onItemDeleted(EventRecipe item);
    }
    private static class RecipeEventViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView subtitle;
        ImageButton options;
        CircularProgressIndicator progress;

        public RecipeEventViewHolder(@NonNull View view) {
            super(view);
            title = view.findViewById(R.id.text_view_label);
            subtitle = view.findViewById(R.id.text_view_subtitle);
            options = view.findViewById(R.id.button_options);
            progress = view.findViewById(R.id.progress);
        }
    }
}
