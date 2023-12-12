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
    public String quantity = "";
    //public int unit = 0; DEPRECATED
    //public float value = 0.0f; DEPRECATED

    // Bounds
    public long eventId = -1;
    public long recipeId = -1;
    public String recipeLabel = "";
    public long datetime = 0;
}
