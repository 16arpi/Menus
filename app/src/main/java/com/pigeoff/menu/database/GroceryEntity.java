package com.pigeoff.menu.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class GroceryEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public boolean checked = false;

    // Ingredient
    public long ingredientId = 0;
    public float value = 0.0f;
    public int unit = 0;

    // Bounds
    public long eventId = 0;
    public long recipeId = 0;
    public String recipeLabel = "";
    public long datetime = 0;
}
