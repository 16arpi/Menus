package com.pigeoff.menu.util;

import java.util.Objects;

public class Unit {
    public String label;
    public float ratio;
    public int parent;

    public Unit(String label, float ration, int parent) {
        this.label = label;
        this.ratio = ration;
        this.parent = parent;
    }

    public static Unit[] getUnits() {
        return new Unit[] {
                new Unit("", 1.0f, 0),
                new Unit("g", 1.0f, 1),
                new Unit("kg", 1000.0f, 1),
                new Unit("L", 1.0f, 3),
                new Unit("cl", 0.01f, 3),
                new Unit("ml", 0.001f, 3),
                new Unit("c.s.", 1.0f, 6),
                new Unit("c.c.", 1.0f, 7),
                new Unit("pincée(s)", 1.0f, 8),
                new Unit("sachet(s)", 1.0f, 9),
                new Unit("pot(s)", 1.0f, 10)
        };
    }
}

