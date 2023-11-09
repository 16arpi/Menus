package com.pigeoff.menu.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pigeoff.menu.database.MenuDatabase;
import com.pigeoff.menu.database.ProductEntity;
import com.pigeoff.menu.util.Unit;
import com.pigeoff.menu.util.Util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class Ingredient {
    public ProductEntity product;
    public float value;
    public int unit;

    public Ingredient(ProductEntity product, float value, int unit) {
        this.product = product;
        this.value = value;
        this.unit = unit;
    }

    public static ArrayList<Ingredient> fromJson(HashMap<Long, ProductEntity> products, String json) {
        ArrayList<Ingredient.Serialized> serialized = new Gson().fromJson(
                json,
                new TypeToken<ArrayList<Ingredient.Serialized>>(){}.getType()
        );
        ArrayList<Ingredient> result = new ArrayList<>();
        for (Ingredient.Serialized s : serialized) {
            result.add(new Ingredient(
                    products.get(s.ingredientId),
                    s.value,
                    s.unit
            ));
        }
        return result;
    }

    public static String toJson(ArrayList<Ingredient> ingredients) {
        ArrayList<Ingredient.Serialized> serialized = new ArrayList<>();
        for (Ingredient s : ingredients) serialized.add(new Serialized(s.product.id, s.value, s.unit));
        return new Gson().toJson(serialized);
    }

    public String format(MenuDatabase database) {

        Unit[] units = Unit.getUnits();
        return String.format(Locale.getDefault(), "%f %s %s", this.value, units[this.unit].label, "");
    }

    public static String format(float value, int unit, String label) {
        Unit[] units = Unit.getUnits();
        return String.format(Locale.getDefault(), "%f %s %s", value, units[unit].label, label);
    }

    public static class Serialized {
        public long ingredientId;
        public float value;
        public int unit;

        public Serialized(long id, float value, int unit) {
            this.ingredientId = id;
            this.value = value;
            this.unit = unit;
        }
    }
}
