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

import com.google.android.material.card.MaterialCardView;
import com.pigeoff.menu.R;
import com.pigeoff.menu.database.ProductEntity;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<ProductEntity> items;
    Context context;
    OnItemAction listener;

    public ProductAdapter(Context context, List<ProductEntity> items, OnItemAction listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int position) {
        ProductViewHolder holder = (ProductViewHolder) h;
        ProductEntity item = items.get(position);

        holder.label.setText(item.label);
        holder.card.setOnClickListener(view -> {
            if (listener != null) listener.onItemSelected(item);
        });

        holder.options.setOnClickListener(view -> {
            PopupMenu optionMenu = new PopupMenu(context, view);
            optionMenu.getMenuInflater().inflate(R.menu.product_menu, optionMenu.getMenu());
            optionMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    if (menuItem.getItemId() == R.id.item_delete) {
                        if (listener != null) listener.onItemDeleted(item);
                    }
                    return true;
                }
            });
            optionMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        public TextView label;
        public MaterialCardView card;
        public ImageButton options;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            label = itemView.findViewById(R.id.text_view_label);
            card = itemView.findViewById(R.id.card);
            options = itemView.findViewById(R.id.button_options);
        }
    }

    public interface OnItemAction {
        void onItemSelected(ProductEntity product);
        void onItemDeleted(ProductEntity product);
    }
}
