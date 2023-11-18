package com.pigeoff.menu.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pigeoff.menu.R;
import com.pigeoff.menu.database.ProductEntity;

import java.security.Policy;
import java.util.ArrayList;
import java.util.List;

public class ProductSectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<ProductEntity> items;
    private ProductAdapter.OnItemAction listener;

    public ProductSectionAdapter(Context context, List<ProductEntity> items, ProductAdapter.OnItemAction listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductSectionViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_recycler, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int position) {
        ArrayList<ProductEntity> sectionProducts = new ArrayList<>();

        for (ProductEntity p : items) if (p.secion == position) sectionProducts.add(p);

        ProductSectionViewHolder holder = (ProductSectionViewHolder) h;

        if (sectionProducts.size() > 0) {
            holder.recyclerView.setAdapter(new ProductAdapter(context, sectionProducts, new ProductAdapter.OnItemAction() {
                @Override
                public void onItemSelected(ProductEntity product) {
                    listener.onItemSelected(product);
                }

                @Override
                public void onItemDeleted(ProductEntity product) {
                    listener.onItemDeleted(product);
                }
            }));
        } else {
            holder.recyclerView.setAdapter(null);
        }
    }


    @Override
    public int getItemCount() {
        return 6;
    }

    public void updateData(List<ProductEntity> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    private class ProductSectionViewHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerView;

        public ProductSectionViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.recycler_view);
            recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
        }
    }
}
