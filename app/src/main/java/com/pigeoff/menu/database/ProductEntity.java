package com.pigeoff.menu.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;
import com.pigeoff.menu.util.Serializable;

@Entity
public class ProductEntity extends Serializable {
    @PrimaryKey(autoGenerate = true)
    public long id;
    // Ingredient label
    public String label = "";
    // Default unit of the ingredient
    public int defaultUnit = 0;
    // Which section of the supermarket
    public int section = 0;
    // Permanent or temporary product
    public boolean permanent = true;

    @NonNull
    public String toString() {
        return this.label;
    }

    @Override
    public String toSerialize() {
        return new Gson().toJson(this);
    }

    public static ProductEntity toObject(String json) {
        return new Gson().fromJson(json, ProductEntity.class);
    }
}
