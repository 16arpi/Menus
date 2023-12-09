package com.pigeoff.menu.util;

import android.content.Context;
import android.graphics.Paint;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pigeoff.menu.R;
import com.pigeoff.menu.data.Ingredient;
import com.pigeoff.menu.database.ProductEntity;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Util {

    public static boolean stringMatchSearch(String origin, String search) {
        if (origin == null || search == null) return false;

        origin = origin.toLowerCase(Locale.ROOT);
        search = search.toLowerCase(Locale.ROOT);

        origin = StringUtils.stripAccents(origin);
        search = StringUtils.stripAccents(search);

        origin = origin.replaceAll("\\W", "");
        search = search.replaceAll("\\W", "");

        return origin.contains(search);
    }

    public static String[] getRecipesTypes(Context context) {
        return context.getResources().getStringArray(R.array.meals);
    }

    public static String getRecipesTypes(Context context, int position) {
        String[] types = context.getResources().getStringArray(R.array.meals);
        return types[position];
    }

    public static String[] getUnitsLabel(Context context) {
        return context.getResources().getStringArray(R.array.units);
    }

    public static String[] getSectionsLabel(Context context) {
        return context.getResources().getStringArray(R.array.section);
    }

    public static void selectRecipeTypeAutoCompleteItem(AutoCompleteTextView view, int position) {
        String[] types = getRecipesTypes(view.getContext());

        view.setText(types[position], false);
    }

    public static void selectUnitAutoCompleteItem(AutoCompleteTextView view, int position) {
        String[] types = getUnitsLabel(view.getContext());

        view.setText(types[position], false);
    }

    public static String formatIngredient(Context context, Ingredient ingredient) {
        String[] labels = Util.getUnitsLabel(context);

        String value = (ingredient.value % 1.0 != 0) ?
                String.format(Locale.getDefault(), "%.2f", ingredient.value) :
                String.format(Locale.getDefault(), "%.0f", ingredient.value);

        String unit = labels[ingredient.unit];

        String label;
        if (ingredient.product != null) {
            label = ingredient.product.label;
        } else {
            label = "";
        }

        return String.format(Locale.getDefault(), "%s %s %s", value, unit, label);
    }


    public static String formatIngredient(Context context, float value, int unit) {
        String[] labels = Util.getUnitsLabel(context);

        String v = (value % 1.0 != 0) ?
                String.format(Locale.getDefault(), "%.2f", value) :
                String.format(Locale.getDefault(), "%.0f", value);


        String u = labels[unit];

        return v.equals("0") ? "" : String.format(Locale.getDefault(), "%s %s", v, u);
    }

    public static String formatFloat(float value) {
        if (value % 1 == 0) {
            return String.valueOf((int) value);
        } else {
            return String.valueOf(value);
        }
    }

    public static void showKeyboard(View focusedView, Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(focusedView, 0);
    }

    public static void hideKeyboard(FragmentActivity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }

    }

    public static ArrayList<String> listFromJson(String json) {
        return new Gson().fromJson(json, new TypeToken<ArrayList<String>>(){}.getType());
    }

    public static String listToJson(ArrayList<String> list) {
        return new Gson().toJson(list);
    }

    public static String formatDate(long datetime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(datetime);

        SimpleDateFormat format = new SimpleDateFormat("E dd/MM", Locale.getDefault());
        return format.format(calendar.getTime());
    }

    public static HashMap<Long, ProductEntity> productsToDict(List<ProductEntity> items) {
        HashMap<Long, ProductEntity> dict = new HashMap<>();
        for (ProductEntity p : items) dict.put(p.id, p);
        return dict;
    }

    public static void paintCheckText(TextView view, boolean check) {
        if (check) {
            view.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        } else {
            view.setPaintFlags(Paint.ANTI_ALIAS_FLAG);
        }
    }
}
