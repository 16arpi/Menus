package com.pigeoff.menu.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.pigeoff.menu.R;

import java.util.ArrayList;
import java.util.Collections;

public class StepEditAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    ArrayList<String> steps;
    boolean editable;

    public StepEditAdapter(Context context, ArrayList<String> steps, boolean editable) {
        this.context = context;
        this.steps = steps;
        this.editable = editable;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StepEditViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_step, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        StepEditViewHolder itemHolder = (StepEditViewHolder) holder;
        String preparation = steps.get(position);

        itemHolder.editStep.setText(preparation);

        itemHolder.editStep.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                steps.set(itemHolder.getAdapterPosition(), s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        itemHolder.buttonClose.setOnClickListener(view -> {
            itemHolder.editStep.clearFocus();
            deleteItem(itemHolder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return steps.size();
    }

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

    private static class StepEditViewHolder extends RecyclerView.ViewHolder {

        TextInputEditText editStep;
        ImageButton buttonClose;

        public StepEditViewHolder(@NonNull View itemView) {
            super(itemView);

            editStep = itemView.findViewById(R.id.edit_step);
            buttonClose = itemView.findViewById(R.id.button_close);
        }
    }
}
