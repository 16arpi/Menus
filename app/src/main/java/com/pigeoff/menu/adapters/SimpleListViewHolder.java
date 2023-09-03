package com.pigeoff.menu.adapters;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pigeoff.menu.R;

public class SimpleListViewHolder extends RecyclerView.ViewHolder {
    TextView textItem;
    ImageButton buttonClose;

    public SimpleListViewHolder(@NonNull View itemView) {
        super(itemView);
        textItem = itemView.findViewById(R.id.text_item);
        buttonClose = itemView.findViewById(R.id.button_close);
    }
}