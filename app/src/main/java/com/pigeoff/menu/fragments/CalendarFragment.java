package com.pigeoff.menu.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.pigeoff.menu.R;
import com.pigeoff.menu.adapters.WeekInterface;
import com.pigeoff.menu.util.Util;

public class CalendarFragment extends Fragment {

    // Toolbar

    MaterialToolbar toolbar;
    CollapsingToolbarLayout toolbarLayout;

    // RecyclerView
    RecyclerView recyclerViewMonday;
    RecyclerView recyclerViewTuesday;
    RecyclerView recyclerViewWednesday;
    RecyclerView recyclerViewThursday;
    RecyclerView recyclerViewFriday;
    RecyclerView recyclerViewSaturday;
    RecyclerView recyclerViewSunday;

    // Today icons
    ImageView iconTodayMonday;
    ImageView iconTodayTuesday;
    ImageView iconTodayWednesday;
    ImageView iconTodayThursday;
    ImageView iconTodayFriday;
    ImageView iconTodaySaturday;
    ImageView iconTodaySunday;

    // Add buttons
    ImageButton buttonAddMonday;
    ImageButton buttonAddTuesday;
    ImageButton buttonAddWednesday;
    ImageButton buttonAddThursday;
    ImageButton buttonAddFriday;
    ImageButton buttonAddSaturday;
    ImageButton buttonAddSunday;

    // Groceries buttons
    ImageButton buttonGroceriesMonday;
    ImageButton buttonGroceriesTuesday;
    ImageButton buttonGroceriesWednesday;
    ImageButton buttonGroceriesThursday;
    ImageButton buttonGroceriesFriday;
    ImageButton buttonGroceriesSaturday;
    ImageButton buttonGroceriesSunday;

    WeekInterface weekInterface;

    public CalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar = view.findViewById(R.id.top_app_bar);
        toolbarLayout = view.findViewById(R.id.top_app_bar_layout);

        recyclerViewMonday = view.findViewById(R.id.recycler_view_monday);
        recyclerViewTuesday = view.findViewById(R.id.recycler_view_tuesday);
        recyclerViewWednesday = view.findViewById(R.id.recycler_view_wednesday);
        recyclerViewThursday = view.findViewById(R.id.recycler_view_thursday);
        recyclerViewFriday = view.findViewById(R.id.recycler_view_friday);
        recyclerViewSaturday = view.findViewById(R.id.recycler_view_saturday);
        recyclerViewSunday = view.findViewById(R.id.recycler_view_sunday);

        iconTodayMonday = view.findViewById(R.id.iconTodayMonday);
        iconTodayTuesday = view.findViewById(R.id.iconTodayTuesday);
        iconTodayWednesday = view.findViewById(R.id.iconTodayWednesday);
        iconTodayThursday = view.findViewById(R.id.iconTodayThursday);
        iconTodayFriday = view.findViewById(R.id.iconTodayFriday);
        iconTodaySaturday = view.findViewById(R.id.iconTodaySaturday);
        iconTodaySunday = view.findViewById(R.id.iconTodaySunday);

        buttonAddMonday = view.findViewById(R.id.button_add_monday);
        buttonAddTuesday = view.findViewById(R.id.button_add_tuesday);
        buttonAddWednesday = view.findViewById(R.id.button_add_wednesday);
        buttonAddThursday = view.findViewById(R.id.button_add_thursday);
        buttonAddFriday = view.findViewById(R.id.button_add_friday);
        buttonAddSaturday = view.findViewById(R.id.button_add_saturday);
        buttonAddSunday = view.findViewById(R.id.button_add_sunday);

        buttonGroceriesMonday = view.findViewById(R.id.button_groceries_monday);
        buttonGroceriesTuesday = view.findViewById(R.id.button_groceries_tuesday);
        buttonGroceriesWednesday = view.findViewById(R.id.button_groceries_wednesday);
        buttonGroceriesThursday = view.findViewById(R.id.button_groceries_thursday);
        buttonGroceriesFriday = view.findViewById(R.id.button_groceries_friday);
        buttonGroceriesSaturday = view.findViewById(R.id.button_groceries_saturday);
        buttonGroceriesSunday = view.findViewById(R.id.button_groceries_sunday);

        weekInterface = new WeekInterface(
                requireActivity(),
                new RecyclerView[] {
                    recyclerViewMonday,
                    recyclerViewTuesday,
                    recyclerViewWednesday,
                    recyclerViewThursday,
                    recyclerViewFriday,
                    recyclerViewSaturday,
                    recyclerViewSunday
                },
                new ImageView[] {
                        iconTodayMonday,
                        iconTodayTuesday,
                        iconTodayWednesday,
                        iconTodayThursday,
                        iconTodayFriday,
                        iconTodaySaturday,
                        iconTodaySunday
                },
                new ImageButton[] {
                        buttonAddMonday,
                        buttonAddTuesday,
                        buttonAddWednesday,
                        buttonAddThursday,
                        buttonAddFriday,
                        buttonAddSaturday,
                        buttonAddSunday
                },
                new ImageButton[]{
                        buttonGroceriesMonday,
                        buttonGroceriesTuesday,
                        buttonGroceriesWednesday,
                        buttonGroceriesThursday,
                        buttonGroceriesFriday,
                        buttonGroceriesSaturday,
                        buttonGroceriesSunday,
                },
                toolbar,
                toolbarLayout
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        Util.hideKeyboard(requireActivity());
    }
}