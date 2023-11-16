package com.pigeoff.menu.util;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

public class DiffUtilCallback<T> extends DiffUtil.Callback {

    List<T> oldList;
    List<T> newList;

    public DiffUtilCallback(List<T> oldList, List<T> newList) {
        super();

        this.oldList = oldList;
        this.newList = newList;
    }
    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getClass() == newList.get(newItemPosition).getClass();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }
}
