package com.pigeoff.menu;

import android.app.Application;

import androidx.room.Room;

import com.pigeoff.menu.database.MenuDatabase;

public class MenuApplication extends Application {

    public MenuDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        database = Room.databaseBuilder(this, MenuDatabase.class, "database")
                .build();
    }
}
