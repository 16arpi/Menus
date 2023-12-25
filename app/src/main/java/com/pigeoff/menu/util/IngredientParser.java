package com.pigeoff.menu.util;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

public class IngredientParser {

    public static final String[] QUANTITY_TEXT = {
            // FRENCH
            "coupes",
            "coupe",
            "cp.",
            "cp",
            "cps",
            "cps.",
            "gallons",
            "gallon",
            "gal.",
            "gal",
            "onces",
            "once",
            "oz.",
            "oz",
            "oc.",
            "oc",
            "pintes",
            "pinte",
            "pt.",
            "pt",
            "cuilleres a soupe",
            "cuillere a soupe",
            "c.a soupe",
            "c. a s.",
            "c. a soupe",
            "c a soupe",
            "cas.",
            "c a s",
            "c.a s.",
            "c.a.s",
            "cuilleres a cafe",
            "cuillere a cafe",
            "c a cafe",
            "c.a cafe",
            "c. a cafe",
            "c.a c.",
            "c. a c.",
            "c.a.c",
            "cac.",
            "c a c",
            "cac",
            "c.c.",
            "cuilleres a the",
            "cuillere a the",
            "c a the",
            "c.a the",
            "c. a the",
            "c.a t.",
            "c. a t.",
            "cat.",
            "c a t",
            "c.t.",
            "cat",
            "c.a.t",
            "c.s.",
            "grammes",
            "gramme",
            "gr.",
            "gr",
            "g.",
            "g",
            "c.",
            "kilogrammes",
            "kilogramme",
            "kilos",
            "kilo",
            "kg.",
            "kg",
            "litres",
            "litre",
            "lit.",
            "l.",
            "L.",
            "centilitres",
            "centilitre",
            "cl",
            "cl.",
            "milligrammes",
            "milligramme",
            "mlg.",
            "mlg",
            "mg.",
            "mg",
            "millilitres",
            "millilitre",
            "mlt",
            "ml.",
            "ml",
            "paquet",
            "paquets",
            "baton",
            "batons",
            "morceau",
            "morceaux",
            "pincee",
            "pincees",
            "pince",
            "gousse",
            "gousses",
            "tranche",
            "tranches",
            "conserve",
            "conserves",
            "boite",
            "boites",
            "sac",
            "sacs",
            "feuille",
            "feuilles",
            "brin",
            "brins",
            "brin",
            "brins",
            "sachet",
            "sachets",
            "dose",
            "doses",
            "goutte",
            "gouttes",
            "carré",
            "carrés",
            "gobelet doseur",
            "gobelets doseur",
            "petit",
            "moyen",
            "grand",
            "un",
            "une",
            "deux",
            "trois",
            "quatre",
            "cinq",
            "six",
            "sept",
            "huit",
            "neuf",
            "dix",
            "onze",
            "douze",
            "dizaine",
            "dizaines",
            "douzaine",
            "douzaines",
            "vingtaine",
            "vingtaines",
            // ENGLISH
            "cup",
            "cups",
            "ounce",
            "ounces",
            "pint",
            "pints",
            "pound",
            "pounds",
            "quart",
            "quarts",
            "tbs",
            "tbs.",
            "tbsp",
            "tbspn",
            "T.",
            "tablespoon",
            "tablespoons",
            "coffeespoon",
            "coffeespoons",
            "tsp",
            "tsp.",
            "tspn",
            "teaspoon",
            "teaspoons",
            "gram",
            "grams",
            "kilogram",
            "kilograms",
            "liter",
            "liters",
            "centiliter",
            "centiliters",
            "milligram",
            "milligrams",
            "milliliter",
            "milliliters",
            "pkg",
            "pkgs",
            "packages",
            "package",
            "stick",
            "sticks",
            "pcs",
            "pcs.",
            "piece",
            "pieces",
            "pinch",
            "pinches",
            "clove",
            "cloves",
            "slice",
            "slices",
            "can",
            "cans",
            "box",
            "boxes",
            "bag",
            "bags",
            "leaf",
            "leaves",
            "sprig",
            "sprigs",
            "unit",
            "units",
            "packet",
            "packets",
            "dose",
            "doses",
            "drop",
            "drops",
            "cube",
            "cubes",
            "measuring cups",
            "measuring cup",
            "dosing cup",
            "dosing cups",
            "dosing beaker",
            "dosing beakers",
            "small",
            "medium",
            "large",
            "tiers",
            "demi",
            "half",
            "third",
            // One or two letters
            "T",
            "l",
            "L",
            "c",
    };

    private final static String[] CHAR_FRACTIONS = {
            "½",
            "⅓",
            "⅔",
            "¼",
            "¾",
            "⅕",
            "⅖",
            "⅗",
            "⅘",
    };

    public static final String[] ARTICLES = {
            // FR
            "de",
            "d",
            // EN
            "of"
    };
    public String quantity;
    public String label;
    public IngredientParser(String quantity, String label) {
        this.quantity = quantity;
        this.label = label;
    }

    public static IngredientParser from(String ingr) {
        // Nettoyage
        String cleaned = StringUtils.trim(ingr);

        // Division récursive
        Gap gap = new Gap(0, 0, false);

        // Fonctionnement
        // Boucle max 200 arbitraire, au cas où...
        for (int i = 0; i < 200; ++i) {
            Gap result = updateGap(cleaned, gap);
            if (gap.end) break;
            gap = result;
        }

        // Résultat
        String quantity = StringUtils.trim(cleaned.substring(0, gap.x));
        String label = StringUtils.trim(cleaned.substring(gap.y));

        return new IngredientParser(quantity, label);
    }

    private static Gap updateGap(String ingr, Gap gap) {
        String right = ingr.substring(gap.y).trim();
        int posRight = ingr.indexOf(right);

        right = clean(right);

        String[] elmnts = right.split("[ ']");
        if (elmnts.length < 2) return new Gap(gap.x, gap.y, true);

        // Number
        String firstElmnt = elmnts[0];
        firstElmnt = clean(firstElmnt);

        Pattern p = Pattern.compile("(\\d+(?:[.,]\\d+)?)");
        if (p.matcher(firstElmnt).find()) {
            return new Gap(posRight + firstElmnt.length(), posRight + firstElmnt.length(), false);
        }

        // Char fractions
        for (String frac : CHAR_FRACTIONS) {
            if (firstElmnt.contains(frac)) {
                return new Gap(posRight + firstElmnt.length(), posRight + firstElmnt.length(), false);
            }
        }

        // Text quantity
        for (String pre : QUANTITY_TEXT) {
            if (right.indexOf(pre + " ") == 0) {
                return new Gap(posRight + pre.length(), posRight + pre.length(), false);
            }
        }

        // Articles
        for (String art : ARTICLES) {
            if (art.equals(firstElmnt)) {
                return new Gap(posRight, posRight + firstElmnt.length() + 1, true);
            }
        }

        return new Gap(posRight, posRight, true);
    }

    private static String clean(String str) {
        return StringUtils.stripAccents(str.toLowerCase());
    }

    public static class Gap {
        public int x;
        public int y;
        public boolean end;
        public Gap(int x, int y, boolean end) {
            this.x = x;
            this.y = y;
            this.end = end;
        }
    }

}