package com.pigeoff.menu.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.pigeoff.menu.R;
import com.pigeoff.menu.database.ProductEntity;
import com.pigeoff.menu.util.DiffUtilCallback;
import com.pigeoff.menu.util.Util;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<ProductEntity> items;
    Context context;
    OnItemAction listener;
    String[] sections;

    public ProductAdapter(Context context, List<ProductEntity> items, OnItemAction listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
        this.sections = Util.getSectionsLabel(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ObjectListViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_recipe_inline, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int position) {
        ObjectListViewHolder holder = (ObjectListViewHolder) h;
        ProductEntity item = items.get(position);

        holder.buttonMore.setVisibility(View.VISIBLE);

        if (item.label.length() > 1) {
            String letter = item.label.substring(0, 1).toUpperCase();
            holder.itemLetter.setText(letter);
        } else {
            holder.itemLetter.setText("P");
        }

        holder.itemTitle.setText(item.label);
        holder.itemSubTitle.setText(sections[item.section]);
        holder.cardItem.setOnClickListener(view -> {
            if (listener != null) listener.onItemSelected(item);
        });

        holder.buttonMore.setOnClickListener(view -> {
            PopupMenu optionMenu = new PopupMenu(context, view);
            optionMenu.getMenuInflater().inflate(R.menu.product_menu, optionMenu.getMenu());
            optionMenu.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId() == R.id.item_delete) {
                    if (listener != null) listener.onItemDeleted(item);
                }
                return true;
            });
            optionMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public interface OnItemAction {
        void onItemSelected(ProductEntity product);
        void onItemDeleted(ProductEntity product);
    }

    public void updateItems(List<ProductEntity> newItems) {
        DiffUtilCallback<ProductEntity> diffCallback = new DiffUtilCallback<>(items, newItems, new DiffUtilCallback.DifferenceCallback<ProductEntity>() {
            @Override
            public boolean sameItem(ProductEntity oldElement, ProductEntity newElement) {
                return oldElement.id == newElement.id;
            }

            @Override
            public boolean sameContent(ProductEntity oldElement, ProductEntity newElement) {
                return oldElement.equals(newElement);
            }
        });
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(diffCallback);
        items = newItems;
        result.dispatchUpdatesTo(this);
    }
}
