package com.pigeoff.menu.util;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

public class DiffUtilCallback<T> extends DiffUtil.Callback {

    List<T> oldList;
    List<T> newList;
    DifferenceCallback<T> callback;

    public DiffUtilCallback(List<T> oldList, List<T> newList, DifferenceCallback<T> callback) {
        super();

        this.oldList = oldList;
        this.newList = newList;
        this.callback = callback;
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
        return callback.sameItem(this.oldList.get(oldItemPosition), this.newList.get(newItemPosition));
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return callback.sameContent(this.oldList.get(oldItemPosition), this.newList.get(newItemPosition));
    }

    public interface DifferenceCallback<T> {
        boolean sameItem(T oldElement, T newElement);
        boolean sameContent(T oldElement, T newElement);
    }
}
