package com.pigeoff.menu.data;

import com.pigeoff.menu.database.GroceryEntity;
import com.pigeoff.menu.database.GroceryWithProduct;
import com.pigeoff.menu.database.ProductEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GrocerieGroup {

    public String label;
    public int section;
    public boolean checked;
    public ProductEntity product;
    public List<GroceryEntity> groceries;

    public GrocerieGroup(ProductEntity product, List<GroceryEntity> groceries, int section) {
        this.checked = true;
        for (GroceryEntity g : groceries)
            if (!g.checked) {
                this.checked = false;
                break;
            }

        this.product = product;
        this.label = product.label;
        this.groceries = groceries;

        this.section = section;

    }

    public void setChecked(boolean checked) {
        for (GroceryEntity g : groceries) {
            g.checked = checked;
        }
        this.checked = checked;
    }

    public static List<GrocerieGroup> fromList(List<GroceryWithProduct> items) {
        HashMap<ProductEntity, List<GroceryEntity>> dict = new HashMap<>();
        ArrayList<GrocerieGroup> group = new ArrayList<>();

        for (GroceryWithProduct gp : items) {
            ProductEntity p = gp.product;
            GroceryEntity g = gp.grocery;

            if (!dict.containsKey(p)) dict.put(p, new ArrayList<>());
            dict.get(p).add(g);
        }

        dict.forEach((key, val) -> group.add(new GrocerieGroup(key, val, key.section)));

        return group;
    }
}
