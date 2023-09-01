package com.pigeoff.menu.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pigeoff.menu.R;
import com.pigeoff.menu.data.Ingredient;
import com.pigeoff.menu.util.Util;

import java.util.ArrayList;

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
        }
        else {
            itemHolder.buttonClose.setVisibility(View.GONE);
        }


        itemHolder.buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = itemHolder.getAdapterPosition();
                deleteItem(pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return steps.size();
    }

    public void switchItems(int start, int end) {
        String iStart = steps.get(start);
        String iEnd = steps.get(end);

        steps.set(end, iStart);
        steps.set(start, iEnd);

        notifyItemChanged(start);
        notifyItemChanged(end);
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
