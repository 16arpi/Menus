package com.pigeoff.menu.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pigeoff.menu.R;
import com.pigeoff.menu.holders.SimpleListViewHolder;

import java.util.ArrayList;
import java.util.Collections;

public class StepAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    ArrayList<String> steps;
    boolean editable;

    public StepAdapter(Context context, ArrayList<String> steps, boolean editable) {
        this.context = context;
        this.steps = steps;
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
        String preparation = steps.get(position);
        itemHolder.textItem.setText(preparation);

        if (editable) {
            itemHolder.buttonClose.setVisibility(View.VISIBLE);
            itemHolder.checkBox.setVisibility(View.GONE);
        }
        else {
            itemHolder.buttonClose.setVisibility(View.GONE);
            itemHolder.checkBox.setVisibility(View.VISIBLE);
        }


        itemHolder.buttonClose.setOnClickListener(view -> {
            int pos = itemHolder.getAdapterPosition();
            deleteItem(pos);
        });
    }

    @Override
    public int getItemCount() {
        return steps.size();
    }

    // TODO switch items
    public void switchItems(int start, int end) {
        Collections.swap(steps, start, end);
        notifyItemMoved(start, end);
    }

    public void addItem(String item) {
        steps.add(item);
        notifyItemInserted(steps.size());
    }

    public void deleteItem(int position) {
        steps.remove(position);
        notifyItemRemoved(position);
    }

    public ArrayList<String> getSteps() {
        return steps;
    }
}
