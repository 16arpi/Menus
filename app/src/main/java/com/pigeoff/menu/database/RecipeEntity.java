package com.pigeoff.menu.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class RecipeEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public boolean cookbook = true;
    public int category = 0;
    public String title;

    public int portions = 1;
    public String ingredients = "[]"; // JSON
    public String steps = "[]"; // JSON
    public String note; // Not used at this point
    public String picturePath = "";
}
