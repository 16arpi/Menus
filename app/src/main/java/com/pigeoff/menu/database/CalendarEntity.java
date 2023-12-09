package com.pigeoff.menu.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class CalendarEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public long recipe;
    public long datetime;
    public String label;
    public int portions;
}
