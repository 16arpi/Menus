package com.pigeoff.menu.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pigeoff.menu.R;
import com.pigeoff.menu.database.CalendarWithRecipe;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class DayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<CalendarWithRecipe>[] items;
    Calendar calendar;
    Callback callback;
    public DayAdapter(Context context, List<CalendarWithRecipe>[] items, Calendar calendar, Callback callback) {
        this.context = context;
        this.items = items;
        this.calendar = calendar;
        this.callback = callback;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DayViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_day, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int position) {
        DayViewHolder holder = (DayViewHolder) h;
        Calendar cal = (Calendar) calendar.clone();
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek() + position);
        cal.set(Calendar.HOUR_OF_DAY, 0);

        int dayNb = cal.get(Calendar.DAY_OF_WEEK) - 1;
        String labelDay = String.format(
                Locale.getDefault(),
                "%s %d",
                context.getResources().getStringArray(R.array.days)[dayNb],
                cal.get(Calendar.DAY_OF_MONTH)
        );

        long thisDay = cal.getTimeInMillis();

        holder.buttonGroceries.setVisibility(View.VISIBLE);
        holder.buttonAdd.setVisibility(View.VISIBLE);

        Calendar thisDayCalendar = Calendar.getInstance();
        thisDayCalendar.setTimeInMillis(thisDay);

        Calendar nowDayCalendar = Calendar.getInstance();

        if (thisDayCalendar.get(Calendar.DAY_OF_YEAR) == nowDayCalendar.get(Calendar.DAY_OF_YEAR)
                && thisDayCalendar.get(Calendar.YEAR) == nowDayCalendar.get(Calendar.YEAR)) {
            holder.iconToday.setVisibility(View.VISIBLE);
        } else {
            holder.iconToday.setVisibility(View.GONE);
        }

        holder.textDay.setText(labelDay);

        // Recycler view

        EventAdapter adapter = new EventAdapter(
                context,
                items[position] == null ? new ArrayList<>() : items[position]
        );
        adapter.setOnAdapterAction(new OnAdapterAction<CalendarWithRecipe>() {
            @Override
            public void onItemClick(CalendarWithRecipe item) {
                if (callback != null) callback.onClick(item);
            }

            @Override
            public void onItemClick(CalendarWithRecipe item, int action) {
                if (action == OnAdapterAction.ACTION_GROCERY) {
                    if (callback != null) callback.onAddToGroceries(Collections.singletonList(item));
                }
            }

            @Override
            public void onItemLongClick(CalendarWithRecipe item, int position) {
                if (callback != null) callback.deleteEvent(item);
            }
        });
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        holder.recyclerView.setAdapter(adapter);

        holder.buttonAdd.setOnClickListener(v -> {
            if (callback != null) callback.pickRecipe(holder.getAdapterPosition());
        });

        holder.buttonAdd.setOnLongClickListener(v -> {
            if (callback != null) callback.addCustomRecipe(holder.getAdapterPosition());
            return true;
        });

        holder.buttonGroceries.setOnClickListener(v -> {
            if (callback != null) callback.onAddToGroceries(items[holder.getAdapterPosition()]);
        });

    }

    @Override
    public int getItemCount() {
        return 7;
    }

    private static class DayViewHolder extends RecyclerView.ViewHolder {
        TextView textDay;
        ImageView iconToday;
        RecyclerView recyclerView;
        ImageButton buttonGroceries;
        ImageButton buttonAdd;

        public DayViewHolder(@NonNull View view) {
            super(view);
            textDay = view.findViewById(R.id.text_day);
            iconToday = view.findViewById(R.id.icon_today);
            recyclerView = view.findViewById(R.id.recycler_view);
            buttonGroceries = view.findViewById(R.id.button_groceries);
            buttonAdd = view.findViewById(R.id.button_add);
        }
    }

    public void updateItems(Calendar calendar, List<CalendarWithRecipe>[] items) {
        this.items = items;
        this.calendar = calendar;
        notifyItemRangeChanged(0, 7);
    }

    public interface Callback {
        void onAddToGroceries(List<CalendarWithRecipe> items);
        void deleteEvent(CalendarWithRecipe item);
        void onClick(CalendarWithRecipe item);
        void pickRecipe(int day);
        void addCustomRecipe(int day);
    }
}
