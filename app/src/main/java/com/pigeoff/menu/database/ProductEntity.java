package com.pigeoff.menu.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ProductEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public boolean checked = false;

    // Ingredient
    public float value = 0.0f;
    public int unit = 0;
    public String label = "";

    // Bounds
    public long eventId = 0;
    public long recipeId = 0;
    public String recipeLabel = "";
    public long datetime = 0;
}
