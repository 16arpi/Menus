package com.pigeoff.menu.database;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class RecipeEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public boolean cookbook = true;
    public int category = 0;
    public String title;

    @ColumnInfo(defaultValue = "1") // Need to be deleted when and replace with public int portions = 1;
    public int portions = 1;
    public String ingredients = "[]"; // JSON
    public String steps = "[]"; // JSON
    public String note;
}
