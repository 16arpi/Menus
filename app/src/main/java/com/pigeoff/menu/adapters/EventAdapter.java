package com.pigeoff.menu.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.pigeoff.menu.R;
import com.pigeoff.menu.database.CalendarWithRecipe;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<CalendarWithRecipe> items;
    OnAdapterAction<CalendarWithRecipe> listener;

    public EventAdapter(Context context, List<CalendarWithRecipe> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EventViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_event, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        EventViewHolder eventHolder = (EventViewHolder) holder;
        CalendarWithRecipe event = items.get(position);
        eventHolder.textEvent.setText(event.calendar.label);

        eventHolder.cardEvent.setOnClickListener(view ->
                listener.onItemClick(event));

        eventHolder.buttonEventOptions.setOnClickListener(view ->
                showMenu(view, event, eventHolder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private static class EventViewHolder extends RecyclerView.ViewHolder {

        MaterialCardView cardEvent;
        TextView textEvent;
        ImageButton buttonEventOptions;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            cardEvent = itemView.findViewById(R.id.card_event);
            textEvent = itemView.findViewById(R.id.text_event);
            buttonEventOptions = itemView.findViewById(R.id.button_event_options);
        }
    }

    private void showMenu(View view, CalendarWithRecipe event, int position) {
        PopupMenu menu = new PopupMenu(context, view);
        menu.getMenuInflater().inflate(R.menu.event_menu, menu.getMenu());
        menu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.item_groceries) {
                listener.onItemClick(event, listener.ACTION_GROCERY);
            } else if (item.getItemId() == R.id.item_delete) {
                listener.onItemLongClick(event, position);
            }
            return true;
        });
        menu.show();
    }

    public void setOnAdapterAction(OnAdapterAction<CalendarWithRecipe> listener) {
        this.listener = listener;
    }
}
