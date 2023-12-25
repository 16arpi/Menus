package com.pigeoff.menu.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pigeoff.menu.R;
import com.pigeoff.menu.holders.SimpleListViewHolder;

import java.util.List;

public class StepAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<String> steps;
    boolean editable;

    public StepAdapter(Context context, List<String> steps, boolean editable) {
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
    }

    @Override
    public int getItemCount() {
        return steps.size();
    }

    public List<String> getSteps() {
        return steps;
    }
}
