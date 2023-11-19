package com.pigeoff.menu.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ProductEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    // Ingredient label
    public String label = "";
    // Default unit of the ingredient
    public int defaultUnit = 0;
    // Which section of the supermarket
    public int secion = 0; // TODO secion -> section

    @NonNull
    public String toString() {
        return this.label;
    }
}
