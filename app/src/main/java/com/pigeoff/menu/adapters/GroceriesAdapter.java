package com.pigeoff.menu.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.color.MaterialColors;
import com.pigeoff.menu.R;
import com.pigeoff.menu.data.GrocerieGroup;
import com.pigeoff.menu.database.ProductEntity;
import com.pigeoff.menu.util.DiffUtilCallback;
import com.pigeoff.menu.util.Util;

import java.util.ArrayList;
import java.util.List;


public class GroceriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

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

            if (p.secion != lastHeader) {
                result.add(i, new GrocerieGroup(new ProductEntity(), new ArrayList<>(), gp.section));
                result.add(i + 1, gp);
                lastHeader = p.secion;
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

            if (p.secion != lastHeader) {
                viewTypes[i] = p.secion;
                viewTypes[i+1] = VIEW_GROCERY;
                lastHeader = p.secion;
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
        GrocerieGroup gp = items.get(position);
        if (holder instanceof GroceriesViewHolder) {
            ProductEntity product = gp.product;

            GroceriesViewHolder groceriesHolder = (GroceriesViewHolder) holder;

            groceriesHolder.label.setText(product.label);
            groceriesHolder.labelValueUnit.setText(gp.quantity);

            int colorAttr;
            if (gp.checked) {
                colorAttr = MaterialColors.getColor(context, com.google.android.material.R.attr.colorPrimary, Color.BLACK);
                groceriesHolder.checkBox.setImageDrawable(
                        AppCompatResources.getDrawable(context, R.drawable.ic_check)
                );
                groceriesHolder.checkBox.setColorFilter(colorAttr);
            } else {
                colorAttr = MaterialColors.getColor(context, com.google.android.material.R.attr.colorSurfaceInverse, Color.BLACK);
                groceriesHolder.checkBox.setImageDrawable(
                        AppCompatResources.getDrawable(context, R.drawable.ic_uncheck)
                );
                groceriesHolder.checkBox.setColorFilter(colorAttr);
            }

            paintCheckText(groceriesHolder.label, gp.checked);
            paintCheckText(groceriesHolder.labelRecipe, gp.checked);

            groceriesHolder.labelRecipe.setVisibility(View.GONE);

            groceriesHolder.checkBox.setOnClickListener(view -> {
                gp.setChecked(!gp.checked);
                listener.onItemClick(gp, OnAdapterAction.ACTION_CHECK);
            });

            groceriesHolder.buttonAction.setVisibility(View.GONE);
        } else {
            SectionViewHolder sectionViewHolder = (SectionViewHolder) holder;
            sectionViewHolder.section.setText(Util.getSectionsLabel(context)[sectionViewHolder.type]);
            sectionViewHolder.add.setOnClickListener(view -> {
                System.out.println("Add product section " + gp.section);
                listener.onItemClick(gp, OnAdapterAction.ACTION_ADD);

            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void switchItems() {

    }

    private static class GroceriesViewHolder extends RecyclerView.ViewHolder {

        ImageButton checkBox;
        TextView label;
        TextView labelValueUnit;
        TextView labelRecipe;
        ImageButton buttonAction;

        public GroceriesViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox);
            label = itemView.findViewById(R.id.text_label);
            labelValueUnit = itemView.findViewById(R.id.text_label_value_unit);
            labelRecipe = itemView.findViewById(R.id.text_sub_label);
            buttonAction = itemView.findViewById(R.id.button_action);
        }
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

    private void paintCheckText(TextView view, boolean check) {
        if (check) {
            view.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        } else {
            view.setPaintFlags(Paint.ANTI_ALIAS_FLAG);
        }
    }

    public void setOnAdapterActionListener(OnAdapterAction<GrocerieGroup> listener) {
        this.listener = listener;
    }

    public void updateGroceries(ArrayList<GrocerieGroup> newItems) {
        DiffUtilCallback<GrocerieGroup> diffCallback = new DiffUtilCallback<>(items, prepareItems(newItems), new DiffUtilCallback.DifferenceCallback<GrocerieGroup>() {
            @Override
            public boolean sameItem(GrocerieGroup oldElement, GrocerieGroup newElement) {
                return oldElement.product.id == oldElement.product.id;
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

}
