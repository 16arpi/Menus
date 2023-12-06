package com.pigeoff.menu.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.ViewGroup;

import androidx.core.view.ViewCompat;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.pigeoff.menu.R;
import com.pigeoff.menu.util.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChipCategories extends ChipGroup {

    public static final int DEFAULT_SECTION = Constants.SECTION_DIVERS;
    public static final int[] ids = {
            R.id.chip_filter_groceries,
            R.id.chip_filter_fruits,
            R.id.chip_filter_meat,
            R.id.chip_filter_fresh,
            R.id.chip_filter_drinks,
            R.id.chip_filter_divers
    };

    private int section = DEFAULT_SECTION;

    private static final int[] strings = {
            R.string.tab_ingredient_groceries,
            R.string.tab_ingredient_fruits,
            R.string.tab_ingredient_meat,
            R.string.tab_ingredient_fresh,
            R.string.tab_ingredient_drinks,
            R.string.tab_ingredient_divers
    };

    HashMap<Integer, Integer> idsToSection = new HashMap<>();
    HashMap<Integer, Integer> sectionToIds = new HashMap<>();

    public ChipCategories(Context context) {
        super(context);

    }

    public ChipCategories(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public ChipCategories(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }


    @Override
    public void check(int section) {
        super.check(ids[section]);
    }

    public int getSelectedSection() {
        List<Integer> checked = getCheckedChipIds();
        for (int i = 0; i < ids.length; ++i) if (checked.contains(ids[i])) return i;
        return Constants.SECTION_EMPTY;
    }

    public List<Integer> getSelectedSections() {
        List<Integer> checked = getCheckedChipIds();
        List<Integer> sections = new ArrayList<>();
        for (int i = 0; i < ids.length; ++i) if (checked.contains(ids[i])) sections.add(i);
        return sections;
    }
}
