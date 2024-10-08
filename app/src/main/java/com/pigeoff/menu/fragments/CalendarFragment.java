package com.pigeoff.menu.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.pigeoff.menu.R;
import com.pigeoff.menu.activities.RecipeActivity;
import com.pigeoff.menu.adapters.DayAdapter;
import com.pigeoff.menu.database.CalendarWithRecipe;
import com.pigeoff.menu.database.GroceryEntity;
import com.pigeoff.menu.database.ProductEntity;
import com.pigeoff.menu.models.WeekModel;
import com.pigeoff.menu.util.Constants;
import com.pigeoff.menu.util.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CalendarFragment extends Fragment {

    private WeekModel model;
    private TextView textTitle;
    private MaterialCardView cardPrev;
    private MaterialCardView cardNext;
    private MaterialCardView cardToday;
    private List<CalendarWithRecipe>[] items;
    private DayAdapter adapter;
    private Calendar calendar;
    private HashMap<Long, ProductEntity> products;


    public CalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        model = new WeekModel(getStartEnd(Calendar.getInstance()), requireActivity().getApplication());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        calendar = Calendar.getInstance();
        items = new List[] {
                null,
                null,
                null,
                null,
                null,
                null,
                null,
        };

        MaterialToolbar toolbar = view.findViewById(R.id.top_app_bar);
        textTitle = view.findViewById(R.id.text_title);
        cardPrev = view.findViewById(R.id.card_prev);
        cardNext = view.findViewById(R.id.card_next);
        cardToday = view.findViewById(R.id.card_today);

        cardPrev.setOnClickListener(v -> setLastWeek());
        cardNext.setOnClickListener(v -> setNextWeek());
        cardToday.setOnClickListener(v -> setThisWeek());

        toolbar.setOnMenuItemClickListener(m -> {
            if (m.getItemId() == R.id.item_help) {
                Intent intent = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://pigeoff.pw/atable/help.html")
                );
                startActivity(intent);
            }
            return true;
        });

        adapter = new DayAdapter(requireContext(), items, calendar, new DayAdapter.Callback() {

            @Override
            public void onAddToGroceries(List<CalendarWithRecipe> items) {

                List<GroceryEntity> blankEntities = model.prepareGroceries(products, items);
                CharSequence[] labels = new CharSequence[blankEntities.size()];
                boolean[] checked = new boolean[blankEntities.size()];
                for (int i = 0; i < blankEntities.size(); ++i) {

                    GroceryEntity g = blankEntities.get(i);
                    String quantity = g.quantity;
                    String label = !products.containsKey(g.ingredientId) ? "???" : products.get(g.ingredientId).label;
                    labels[i] = String.format("%s %s", quantity, label);
                    checked[i] = true;
                }

                if (labels.length == 0) {
                    Toast.makeText(requireContext(), R.string.calendar_product_no_more_groceries, Toast.LENGTH_SHORT).show();
                    return;
                }

                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle(R.string.dialog_add_title)
                        .setMultiChoiceItems(labels, checked, (dialog, which, isChecked) -> checked[which] = isChecked)
                        .setNegativeButton(R.string.dialog_add_cancel, (dialog, which) -> {

                        })
                        .setPositiveButton(R.string.dialog_add_submit, (dialog, which) -> {
                            List<GroceryEntity> finalItems = new ArrayList<>();
                            for (int i = 0; i < blankEntities.size(); ++i) {
                                if (checked[i]) finalItems.add(blankEntities.get(i));
                            }
                            model.addToGrocerie(finalItems);
                            Toast.makeText(
                                    requireContext(),
                                    requireContext().getString(R.string.calendar_product_added),
                                    Toast.LENGTH_SHORT
                            ).show();
                        })
                        .show();
            }

            @Override
            public void deleteEvent(CalendarWithRecipe item) {
                model.deleteEvent(item);
            }

            @Override
            public void onClick(CalendarWithRecipe item) {
                Intent intent = new Intent(requireContext(), RecipeActivity.class);
                intent.putExtra(Constants.CALENDAR_ID, item.calendar.id);
                requireContext().startActivity(intent);
            }

            @Override
            public void pickRecipe(int day) {
                RecipePickerFragment picker = new RecipePickerFragment();
                picker.show(requireActivity().getSupportFragmentManager(), Constants.EDIT_FRAGMENT_TAG);
                picker.setOnRecipePicked((recipe) -> {
                    picker.dismiss();
                    model.addEvent(getTimestampOfDay(calendar, day), recipe, false);
                });
            }

            @Override
            public void addCustomRecipe(int day) {
                RecipeEditFragment editFragment = RecipeEditFragment.newInstance();
                editFragment.showFullScreen(requireActivity().getSupportFragmentManager());
                editFragment.setActionListener(recipe -> {
                    editFragment.dismiss();
                    recipe.cookbook = false;
                    long moment = getTimestampOfDay(calendar, day);
                    model.addEvent(moment, recipe, true);
                });
            }
        });

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        recyclerView.setAdapter(adapter);

        setThisWeek();

        model.getEvents().observe(getViewLifecycleOwner(), this::updateItems);

        model.getProducts().observe(getViewLifecycleOwner(), productEntities ->
                products = Util.productsToDict(productEntities));
    }

    @Override
    public void onResume() {
        super.onResume();
        Util.hideKeyboard(requireActivity());
    }

    public void setThisWeek() {
        // Setting week timestamp
        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.clear(Calendar.MINUTE);
        calendar.clear(Calendar.SECOND);
        calendar.clear(Calendar.MILLISECOND);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());

        model.setStartEnd(getStartEnd(calendar));
    }

    public void setLastWeek() {
        calendar.add(Calendar.WEEK_OF_YEAR, -1);
        model.setStartEnd(getStartEnd(calendar));
    }

    public void setNextWeek() {
        calendar.add(Calendar.WEEK_OF_YEAR, 1);
        model.setStartEnd(getStartEnd(calendar));
    }

    private static long getTimestampOfDay(Calendar calendar, int day) {
        Calendar cal = (Calendar) calendar.clone();
        cal.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek() + day);
        return cal.getTimeInMillis();
    }

    private WeekModel.StartEnd getStartEnd(Calendar cal) {
        Calendar c = (Calendar) cal.clone();
        long start = c.getTimeInMillis();
        c.add(Calendar.WEEK_OF_YEAR, 1);
        long end = c.getTimeInMillis();
        return new WeekModel.StartEnd(start, end);
    }

    private String formatWeekDate() {
        SimpleDateFormat format = new SimpleDateFormat("E dd", Locale.FRANCE);
        Calendar cal = (Calendar) calendar.clone();
        String start = format.format(cal.getTime());
        cal.add(Calendar.DAY_OF_YEAR, 6);
        String end = format.format(cal.getTime());
        return start + " - " + end;
    }

    private void updateItems(List<CalendarWithRecipe> data) {
        textTitle.setText(formatWeekDate());

        Calendar cal = (Calendar) calendar.clone();
        for (int i = 0; i < 7; ++i) {
            Calendar today = (Calendar) cal.clone();
            long start = today.getTimeInMillis();
            today.add(Calendar.DAY_OF_YEAR, 1);
            long end = today.getTimeInMillis();

            //items[i] = database.calendarDAO().select(start, end);
            items[i] = new ArrayList<>();
            for (CalendarWithRecipe d : data) {
                if (d.calendar.datetime >= start && d.calendar.datetime < end)  items[i].add(d);
            }

            cal.add(Calendar.DAY_OF_YEAR, 1);
        }

        adapter.updateItems(calendar, items);

    }
}