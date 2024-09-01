package com.pigeoff.menu.util;

import android.content.Context;
import android.graphics.Paint;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pigeoff.menu.R;
import com.pigeoff.menu.database.ProductEntity;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static boolean stringEqualsSearch(String origin, String search) {
        if (origin == null || search == null) return false;
        origin = origin.toLowerCase(Locale.ROOT);
        search = search.toLowerCase(Locale.ROOT);

        origin = StringUtils.stripAccents(origin);
        search = StringUtils.stripAccents(search);

        origin = origin.replaceAll("\\W", "");
        search = search.replaceAll("\\W", "");

        return origin.equals(search);
    }

    public static int findNumberInString(String str, int defaultValue) {
        Pattern p = Pattern.compile("(\\d+)");
        Matcher m = p.matcher(str);
        while (m.find()) {
            try {
                return Integer.parseInt(m.group());
            } catch (Exception ignored) {

            }
        }
        return defaultValue;
    }

    public static String findURLinString(String str) {
        Matcher matcher = Patterns.WEB_URL.matcher(str);
        while (matcher.find()) {
            return matcher.group();
        }
        return "";
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

    public static void clearFocus(FragmentActivity activity) {
        if (activity.getCurrentFocus() != null) activity.getCurrentFocus().clearFocus();
    }

    public static ArrayList<String> listFromJson(String json) {
        return new Gson().fromJson(json, new TypeToken<ArrayList<String>>(){}.getType());
    }

    public static String listToJson(List<String> list) {
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

    public static void injectImage(Context context, String path, ImageView imageView) {
        Glide.with(context)
                .load(path)
                .placeholder(R.drawable.recipe_preview)
                .error(R.drawable.recipe_preview)
                .centerCrop()
                .into(imageView);
    }
}
