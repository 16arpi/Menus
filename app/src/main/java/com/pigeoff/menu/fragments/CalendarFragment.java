package com.pigeoff.menu.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.pigeoff.menu.R;
import com.pigeoff.menu.adapters.EventAdapter;
import com.pigeoff.menu.models.WeekModel;

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

    WeekModel weekModel;

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

        toolbar = view.findViewById(R.id.topAppBar);
        toolbarLayout = view.findViewById(R.id.topAppBarLayout);

        recyclerViewMonday = view.findViewById(R.id.recyclerViewMonday);
        recyclerViewTuesday = view.findViewById(R.id.recyclerViewTuesday);
        recyclerViewWednesday = view.findViewById(R.id.recyclerViewWednesday);
        recyclerViewThursday = view.findViewById(R.id.recyclerViewThursday);
        recyclerViewFriday = view.findViewById(R.id.recyclerViewFriday);
        recyclerViewSaturday = view.findViewById(R.id.recyclerViewSaturday);
        recyclerViewSunday = view.findViewById(R.id.recyclerViewSunday);

        buttonAddMonday = view.findViewById(R.id.buttonAddMonday);
        buttonAddTuesday = view.findViewById(R.id.buttonAddTuesday);
        buttonAddWednesday = view.findViewById(R.id.buttonAddWednesday);
        buttonAddThursday = view.findViewById(R.id.buttonAddThursday);
        buttonAddFriday = view.findViewById(R.id.buttonAddFriday);
        buttonAddSaturday = view.findViewById(R.id.buttonAddSaturday);
        buttonAddSunday = view.findViewById(R.id.buttonAddSunday);

        buttonGroceriesMonday = view.findViewById(R.id.buttonGroceriesMonday);
        buttonGroceriesTuesday = view.findViewById(R.id.buttonGroceriesTuesday);
        buttonGroceriesWednesday = view.findViewById(R.id.buttonGroceriesWednesday);
        buttonGroceriesThursday = view.findViewById(R.id.buttonGroceriesThursday);
        buttonGroceriesFriday = view.findViewById(R.id.buttonGroceriesFriday);
        buttonGroceriesSaturday = view.findViewById(R.id.buttonGroceriesSaturday);
        buttonGroceriesSunday = view.findViewById(R.id.buttonGroceriesSunday);

        weekModel = new WeekModel(
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
}