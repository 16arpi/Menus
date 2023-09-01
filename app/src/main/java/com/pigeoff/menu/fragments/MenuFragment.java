package com.pigeoff.menu.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.pigeoff.menu.MenuApplication;
import com.pigeoff.menu.database.MenuDatabase;

public class MenuFragment extends Fragment {

    public MenuApplication application;
    public MenuDatabase database;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        application = (MenuApplication) requireActivity().getApplication();
        database = application.database;
    }
}
