package com.pigeoff.menu.adapters;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.pigeoff.menu.R;

public class CardListViewHolder extends RecyclerView.ViewHolder {

    public MaterialCardView cardItem;
    public TextView itemLetter;
    public TextView itemTitle;
    public TextView itemSubTitle;
    public ImageButton buttonMore;

    public CardListViewHolder(@NonNull View itemView) {
        super(itemView);
        cardItem = itemView.findViewById(R.id.card_item);
        itemLetter = itemView.findViewById(R.id.text_letter);
        itemTitle = itemView.findViewById(R.id.text_recipe);
        itemSubTitle = itemView.findViewById(R.id.text_recipe_sub);
        buttonMore = itemView.findViewById(R.id.button_more);
    }

}
