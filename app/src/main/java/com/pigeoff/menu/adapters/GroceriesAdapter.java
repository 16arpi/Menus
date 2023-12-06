package com.pigeoff.menu.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.color.MaterialColors;
import com.pigeoff.menu.R;
import com.pigeoff.menu.data.GrocerieGroup;
import com.pigeoff.menu.database.GroceryEntity;
import com.pigeoff.menu.database.ProductEntity;
import com.pigeoff.menu.util.DiffUtilCallback;
import com.pigeoff.menu.util.Util;

import java.util.ArrayList;
import java.util.List;


public class GroceriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    int expandedPosition = -1;

    Context context;
    ArrayList<GrocerieGroup> items;
    OnAdapterAction<GrocerieGroup> listener;
    int[] viewTypes;

    private static final int VIEW_GROCERY = 6;

    public GroceriesAdapter(Context context, ArrayList<GrocerieGroup> items) {
        this.context = context;
        this.items = prepareItems(items);
        this.viewTypes = prepareViewTypes(items);
    }

    private ArrayList<GrocerieGroup> prepareItems(List<GrocerieGroup> all) {
        ArrayList<GrocerieGroup> result = new ArrayList<>();

        int lastHeader = -1;
        int x = 0;
        int i = 0;
        while (x < all.size()) {
            GrocerieGroup gp = all.get(x);
            ProductEntity p = gp.product;

            if (p.section != lastHeader) {
                result.add(i, new GrocerieGroup(new ProductEntity(), new ArrayList<>(), gp.section));
                result.add(i + 1, gp);
                lastHeader = p.section;
                i += 2;
            } else {
                result.add(i, gp);
                i++;
            }

            x++;
        }

        return result;
    }

    private int[] prepareViewTypes(List<GrocerieGroup> all) {
        int[] viewTypes = new int[getItemCount()];

        int lastHeader = -1;
        int x = 0;
        int i = 0;
        while (x < all.size()) {
            GrocerieGroup gp = all.get(x);
            ProductEntity p = gp.product;

            if (p.section != lastHeader) {
                viewTypes[i] = p.section;
                viewTypes[i+1] = VIEW_GROCERY;
                lastHeader = p.section;
                i += 2;
            } else {
                viewTypes[i] = VIEW_GROCERY;
                i++;
            }

            x++;
        }

        return viewTypes;
    }

    @Override
    public int getItemViewType(int position) {
        return viewTypes[position];
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
            onBindGroceriesViewHolder((GroceriesViewHolder) holder, position);
        } else if (holder instanceof SectionViewHolder) {
            onBindSectionViewHolder((SectionViewHolder) holder, position);
        }
    }

    private void onBindGroceriesViewHolder(GroceriesViewHolder holder, int position) {
        GrocerieGroup gp = items.get(position);
        ProductEntity product = gp.product;

        holder.label.setText(product.label);
        holder.labelValueUnit.setVisibility(View.GONE);

        int colorAttr;
        if (gp.checked) {
            colorAttr = MaterialColors.getColor(context, com.google.android.material.R.attr.colorPrimary, Color.BLACK);
            holder.checkBox.setImageDrawable(
                    AppCompatResources.getDrawable(context, R.drawable.ic_check)
            );
            holder.checkBox.setColorFilter(colorAttr);
        } else {
            colorAttr = MaterialColors.getColor(context, com.google.android.material.R.attr.colorSurfaceInverse, Color.BLACK);
            holder.checkBox.setImageDrawable(
                    AppCompatResources.getDrawable(context, R.drawable.ic_uncheck)
            );
            holder.checkBox.setColorFilter(colorAttr);
        }

        Util.paintCheckText(holder.label, gp.checked);

        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        holder.recyclerView.setAdapter(new RecipeDetailsAdapter(context, gp.groceries));

        holder.checkBox.setOnClickListener(view -> {
            gp.setChecked(!gp.checked);
            listener.onItemClick(gp, OnAdapterAction.ACTION_CHECK);
        });

        holder.buttonAction.setOnClickListener(v -> expandAction(holder.getAdapterPosition()));
        holder.label.setOnClickListener(v -> expandAction(holder.getAdapterPosition()));


        if (expandedPosition == holder.getAdapterPosition()) {
            holder.buttonAction.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_collapse));
            holder.recyclerView.setVisibility(View.VISIBLE);
        } else {
            holder.buttonAction.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_expand));
            holder.recyclerView.setVisibility(View.GONE);
        }
    }

    private void expandAction(int position) {
        int oldExpandedPosition = expandedPosition;
        expandedPosition = expandedPosition == position ? -1 : position;
        notifyItemChanged(position);
        notifyItemChanged(oldExpandedPosition);
    }

    private void onBindSectionViewHolder(SectionViewHolder holder, int position) {
        GrocerieGroup gp = items.get(position);
        holder.section.setText(Util.getSectionsLabel(context)[holder.type]);
        holder.add.setOnClickListener(view -> {
            listener.onItemClick(gp, OnAdapterAction.ACTION_ADD);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private static class SectionViewHolder extends RecyclerView.ViewHolder {

        TextView section;
        ImageButton add;
        int type;

        public SectionViewHolder(int viewType, @NonNull View itemView) {
            super(itemView);
            type = viewType;
            section = itemView.findViewById(R.id.text_title);
            add = itemView.findViewById(R.id.button_add);
        }
    }

    private static class GroceriesViewHolder extends RecyclerView.ViewHolder {

        ImageButton checkBox;
        TextView label;
        TextView labelValueUnit;
        ImageButton buttonAction;
        RecyclerView recyclerView;

        public GroceriesViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox);
            label = itemView.findViewById(R.id.text_label);
            labelValueUnit = itemView.findViewById(R.id.text_label_value_unit);
            buttonAction = itemView.findViewById(R.id.button_action);
            recyclerView = itemView.findViewById(R.id.recycler_view);
        }
    }

    public void setOnAdapterActionListener(OnAdapterAction<GrocerieGroup> listener) {
        this.listener = listener;
    }

    public void updateGroceries(ArrayList<GrocerieGroup> newItems) {
        DiffUtilCallback<GrocerieGroup> diffCallback = new DiffUtilCallback<>(items, prepareItems(newItems), new DiffUtilCallback.DifferenceCallback<GrocerieGroup>() {
            @Override
            public boolean sameItem(GrocerieGroup oldElement, GrocerieGroup newElement) {
                return oldElement.product.id == newElement.product.id;
            }

            @Override
            public boolean sameContent(GrocerieGroup oldElement, GrocerieGroup newElement) {
                return oldElement.equals(newElement);
            }
        });
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(diffCallback);
        items = prepareItems(newItems);
        viewTypes = prepareViewTypes(newItems);

        result.dispatchUpdatesTo(this);
    }

    public GrocerieGroup getGroup(int position) {
        if (getItemViewType(position) == VIEW_GROCERY) {
            return items.get(position);
        } else {
            return null;
        }
    }

    private static class RecipeDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        List<GroceryEntity> items;
        Context context;

        public RecipeDetailsAdapter(Context context, List<GroceryEntity> items) {
            this.items = items;
            this.context = context;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new RecipeDetailsViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_recipe_quantity, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int position) {
            RecipeDetailsViewHolder holder = (RecipeDetailsViewHolder) h;
            GroceryEntity item = items.get(position);

            holder.textRecipe.setText(
                    item.recipeId >= 0 ? item.recipeLabel : context.getString(R.string.label_empty_recipe)
            );
            holder.textQuantity.setText(Util.formatIngredient(item.value, item.unit));

            Util.paintCheckText(holder.textRecipe, item.checked);
            Util.paintCheckText(holder.textQuantity, item.checked);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        private static class RecipeDetailsViewHolder extends RecyclerView.ViewHolder {

            TextView textRecipe;
            TextView textQuantity;

            public RecipeDetailsViewHolder(@NonNull View view) {
                super(view);
                textRecipe = view.findViewById(R.id.text_label_left);
                textQuantity = view.findViewById(R.id.text_label_right);
            }
        }
    }

}
