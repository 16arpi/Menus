package com.pigeoff.menu.util;

public class Unit {
    public float ratio;
    public int parent;

    public Unit(float ration, int parent) {
        this.ratio = ration;
        this.parent = parent;
    }

    public static Unit[] getUnits() {
        return new Unit[] {
                new Unit(1.0f, 0), // Blank
                new Unit(1.0f, 1), // g
                new Unit(1000.0f, 1), // kg
                new Unit(1.0f, 3), // L
                new Unit(0.01f, 3), // cl
                new Unit(0.001f, 3), // ml
                new Unit(1.0f, 6), // c.s.
                new Unit(1.0f, 7), // c.c.
                new Unit(1.0f, 8), // pincée(s)
                new Unit(1.0f, 9), // sachet(s)
                new Unit(1.0f, 10) // pot(s)
        };
    }
}

