package com.pigeoff.menu.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.pigeoff.menu.R;
import com.pigeoff.menu.database.CalendarEntity;

import java.util.ArrayList;

public class EventAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    ArrayList<CalendarEntity> items;
    OnAdapterAction<CalendarEntity> listener;

    public EventAdapter(Context context, ArrayList<CalendarEntity> items) {
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
        CalendarEntity event = items.get(position);
        eventHolder.textEvent.setText(event.label);

        eventHolder.cardEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(event);
            }
        });

        /*
        eventHolder.cardEvent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onItemLongClick(event, eventHolder.getAdapterPosition());
                return true;
            }
        });
        */

        eventHolder.buttonEventOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMenu(view, R.menu.event_menu, event, eventHolder.getAdapterPosition());
            }
        });
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

    private void showMenu(View view, int resource, CalendarEntity event, int position) {
        PopupMenu menu = new PopupMenu(context, view);
        menu.getMenuInflater().inflate(resource, menu.getMenu());
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_groceries:{
                        listener.onItemClick(event, listener.ACTION_GROCERY);
                        return true;
                    }
                    case R.id.item_delete:{
                        listener.onItemLongClick(event, position);
                        return true;
                    }
                }
                return true;
            }
        });
        menu.show();
    }

    public void setOnAdapterAction(OnAdapterAction<CalendarEntity> listener) {
        this.listener = listener;
    }


    public void deleteItem(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }

    public void updateItems(ArrayList<CalendarEntity> items) {
        this.items = items;
        notifyDataSetChanged();
    }
}
