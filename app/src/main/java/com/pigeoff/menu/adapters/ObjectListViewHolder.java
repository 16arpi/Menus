package com.pigeoff.menu.adapters;

import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pigeoff.menu.R;

public class ObjectListViewHolder extends RecyclerView.ViewHolder {

    public LinearLayout cardItem;
    public TextView itemLetter;
    public TextView itemTitle;
    public TextView itemSubTitle;
    public ImageButton buttonMore;

    public ObjectListViewHolder(@NonNull View itemView) {
        super(itemView);
        cardItem = itemView.findViewById(R.id.card_item);
        itemLetter = itemView.findViewById(R.id.text_letter);
        itemTitle = itemView.findViewById(R.id.text_recipe);
        itemSubTitle = itemView.findViewById(R.id.text_recipe_sub);
        buttonMore = itemView.findViewById(R.id.button_more);
    }

}
