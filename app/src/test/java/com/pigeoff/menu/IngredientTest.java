package com.pigeoff.menu;

import static org.junit.Assert.assertEquals;

import com.pigeoff.menu.util.IngredientParser;

import org.junit.Test;

public class IngredientTest {

    public static String[][] ingredients = {
            new String[] {
                    "450 g de merlans",
                    "450 g",
                    "merlans",
            },
            new String[] {
                    "100 g de beurre",
                    "100 g",
                    "beurre",
            },
            new String[] {
                    "½ citron",
                    "½",
                    "citron",
            },
            new String[] {
                    "25 g de beurre",
                    "25 g",
                    "beurre",
            },
            new String[] {
                    "24 g de crème fraîche",
                    "24 g",
                    "crème fraîche",
            },
            new String[] {
                    "Sel ou sel fin",
                    "",
                    "Sel ou sel fin",
            },
            new String[] {
                    "Poivre",
                    "",
                    "Poivre",
            },
            new String[] {
                    "3 patates",
                    "3",
                    "patates",
            },
    };
    @Test
    public void testIngredientsParse() {
        for (String[] r : ingredients) {
            String ingr = r[0];
            String left = r[1];
            String right = r[2];

            IngredientParser parsed = IngredientParser.from(ingr);
            assertEquals(left, parsed.quantity);
            assertEquals(right, parsed.label);
        }
    }
}
