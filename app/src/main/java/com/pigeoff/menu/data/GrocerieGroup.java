package com.pigeoff.menu.data;

import com.pigeoff.menu.database.GroceryEntity;
import com.pigeoff.menu.database.GroceryWithProduct;
import com.pigeoff.menu.database.ProductEntity;
import com.pigeoff.menu.util.Unit;
import com.pigeoff.menu.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GrocerieGroup {

    public String label;
    public String quantity;
    public boolean checked;

    public ProductEntity product;
    public List<GroceryEntity> groceries;

    public GrocerieGroup(ProductEntity product, List<GroceryEntity> groceries) {
        Unit[] units = Unit.getUnits();

        HashMap<Integer, Integer> counter = new HashMap<>();
        HashMap<Integer, ValueUnit> valueUnits = new HashMap<>();

        for (GroceryEntity g : groceries) counter.merge(units[g.unit].parent, 1, Integer::sum);

        for (GroceryEntity g : groceries) {
            int parent = units[g.unit].parent;
            if (counter.get(parent) == 1) {
                valueUnits.put(g.unit, new ValueUnit(g.value, g.unit));
            } else {
                Unit fromUnit = units[g.unit];
                float fromValue = g.value;
                float toValue = fromValue * fromUnit.ratio;

                if (!valueUnits.containsKey(parent)) {
                    valueUnits.put(parent, new ValueUnit(toValue, parent));
                } else {
                    valueUnits.get(parent).value += toValue;
                }
            }
        }

        this.checked = true;
        for (GroceryEntity g : groceries)
            if (!g.checked) {
                this.checked = false;
                break;
            }

        this.quantity = ValueUnit.format(valueUnits);
        this.product = product;
        this.label = product.label;
        this.groceries = groceries;

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

        dict.forEach((key, val) -> {
            group.add(new GrocerieGroup(key, val));
        });

        return group;
    }

    private static class ValueUnit {
        public float value;
        public int unit;
        public ValueUnit(float value, int unit) {
            this.value = value;
            this.unit = unit;
        }

        public void balance() {
            Unit[] units = Unit.getUnits();
            float value = this.value;
            int unit = this.unit;

            int best = 99;
            float bestValue = value;
            int bestUnit = unit;
            for (int i = 0; i < units.length; ++i) {
                Unit u = units[i];

                if (u.parent == unit) {
                    float toValue = value / u.ratio;
                    int left = toValue > 0 ? (int) Math.log10((int) toValue) + 1 : 1;
                    int right = String.format(String.valueOf(toValue), "%.2f").split("\\.")[1].length();
                    int equilibrum = left + right;

                    // TODO Find a better way to choose unit

                    System.out.println("BALANCE");
                    System.out.println(u.label);
                    System.out.println(toValue);
                    System.out.println(equilibrum);

                    if (equilibrum < best) {
                        best = equilibrum;
                        bestValue = toValue;
                        bestUnit = i;
                    }
                }
            }

            this.value = bestValue;
            this.unit = bestUnit;
        }

        public static String format(HashMap<Integer, ValueUnit> set) {
            List<String> strings = new ArrayList<>();

            set.forEach((k, v) -> {
                v.balance();
                strings.add(Util.formatIngredient(v.value, v.unit));
            });

            return String.join("\n", strings);
        }
    }
}
