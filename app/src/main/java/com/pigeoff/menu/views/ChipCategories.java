package com.pigeoff.menu.views;

import android.content.Context;
import android.util.AttributeSet;

import com.google.android.material.chip.ChipGroup;
import com.pigeoff.menu.R;
import com.pigeoff.menu.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class ChipCategories extends ChipGroup {

    public static final int[] ids = {
            R.id.chip_filter_groceries,
            R.id.chip_filter_fruits,
            R.id.chip_filter_meat,
            R.id.chip_filter_fresh,
            R.id.chip_filter_drinks,
            R.id.chip_filter_divers
    };

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
