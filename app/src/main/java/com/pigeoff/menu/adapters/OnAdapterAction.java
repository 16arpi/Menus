package com.pigeoff.menu.adapters;

public interface OnAdapterAction<Element> {
    int ACTION_GROCERY = 0;
    int ACTION_CHECK = 1;
    int ACTION_ADD = 2;
    void onItemClick(Element item);
    void onItemClick(Element item, int action);
    void onItemLongClick(Element item, int position);
}
