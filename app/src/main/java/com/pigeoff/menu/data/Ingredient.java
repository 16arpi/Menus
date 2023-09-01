package com.pigeoff.menu.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pigeoff.menu.util.Unit;
import com.pigeoff.menu.util.Util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Locale;

public class Ingredient {
    public float value;
    public int unit;
    public String label;

    public Ingredient(float value, int unit, String label) {
        this.value = value;
        this.unit = unit;
        this.label = label;
    }

    public static ArrayList<Ingredient> fromJson(String json) {
        return new Gson().fromJson(json, new TypeToken<ArrayList<Ingredient>>(){}.getType());
    }

    public static String toJson(ArrayList<Ingredient> ingredients) {
        return new Gson().toJson(ingredients);
    }

    public String format() {
        Unit[] units = Unit.getUnits();
        return String.format(Locale.getDefault(), "%f %s %s", this.value, units[this.unit].label, this.label);
    }

    public static String format(float value, int unit, String label) {
        Unit[] units = Unit.getUnits();
        return String.format(Locale.getDefault(), "%f %s %s", value, units[unit].label, label);
    }
}
