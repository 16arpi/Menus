package com.pigeoff.menu.adapters;

public interface OnAdapterAction<Element> {
    static int ACTION_GROCERY = 0;
    static int ACTION_CHECK = 1;
    void onItemClick(Element item);
    void onItemClick(Element item, int action);
    void onItemLongClick(Element item, int position);
}
