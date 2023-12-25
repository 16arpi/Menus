package com.pigeoff.menu.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pigeoff.menu.database.ProductEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Ingredient {
    public ProductEntity product;
    public String quantity;

    public Ingredient(ProductEntity product, String quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public static ArrayList<Ingredient> fromJson(HashMap<Long, ProductEntity> products, String json) {
        ArrayList<Ingredient.Serialized> serialized = new Gson().fromJson(
                json,
                new TypeToken<ArrayList<Ingredient.Serialized>>(){}.getType()
        );
        ArrayList<Ingredient> result = new ArrayList<>();

        for (Ingredient.Serialized s : serialized) {
            result.add(new Ingredient(
                    products.get(s.ingredientId),
                    s.quantity
            ));
        }
        return result;
    }

    public static ArrayList<Ingredient> fromJson(
            HashMap<Long, ProductEntity> products,
            String json,
            int defaultPortion,
            int customPortion) {
        ArrayList<Ingredient.Serialized> serialized = new Gson().fromJson(
                json,
                new TypeToken<ArrayList<Ingredient.Serialized>>(){}.getType()
        );
        ArrayList<Ingredient> result = new ArrayList<>();
        for (Ingredient.Serialized s : serialized) {
            QuantityPortion qtPt = new QuantityPortion(s.quantity, defaultPortion, false);
            result.add(new Ingredient(
                    products.get(s.ingredientId),
                    qtPt.formatAsPortion(customPortion)
            ));
        }
        return result;
    }

    public static String toJson(List<Ingredient> ingredients) {
        ArrayList<Ingredient.Serialized> serialized = new ArrayList<>();
        for (Ingredient s : ingredients) serialized.add(new Serialized(s.product.id, s.quantity));
        return new Gson().toJson(serialized);
    }

    public String format() {
        String quantity = this.quantity;


        String label;
        if (this.product != null) {
            label = this.product.label;
        } else {
            label = "";
        }

        return String.format(Locale.getDefault(), "%s %s", quantity, label);
    }

    private static class QuantityPortion {

        private final static int FRACTIONS_SIZE = 9;
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
        private final static String[] STR_FRACTIONS = {
                "1/2",
                "1/3",
                "2/3",
                "1/4",
                "3/4",
                "1/5",
                "2/5",
                "3/5",
                "4/5",
        };
        private final static float[] DECIMALS = {
                0.5f,
                0.33f,
                0.66f,
                0.25f,
                0.75f,
                0.2f,
                0.4f,
                0.6f,
                0.8f
        };

        private final Pattern numberPattern;
        private final boolean charFractionDisplay;
        private final int defaultPortion;

        private final String leftLabel;
        private final String rightLabel;
        private final float value;

        private static class MatchFind<T> {

            public int start;
            public int size;
            public String str;
            public T value;

            public MatchFind(int start, int size, String str, T value) {
                this.start = start;
                this.size = size;
                this.str = str;
                this.value = value;
            }
        }

        public QuantityPortion(String quantity, int defaultPortion, boolean charFractionDisplay) {
            this.numberPattern = Pattern.compile("(\\d+(?:[.,]\\d+)?)");
            this.defaultPortion = defaultPortion;
            this.charFractionDisplay = charFractionDisplay;

            MatchFind<Float> find = parseQuantity(quantity);
            this.leftLabel = quantity.substring(0, find.start);
            this.rightLabel = quantity.substring(find.start + find.size);
            this.value = find.value;
        }

        private MatchFind<Float> parseQuantity(String quantity) {
            for (int i = 0; i < FRACTIONS_SIZE; ++i) {
                String s = CHAR_FRACTIONS[i];
                int pos = quantity.indexOf(s);

                if (pos < 0) continue;

                int size = s.length();
                float number = DECIMALS[i];
                return new MatchFind<>(pos, size, s, number);
            }

            for (int i = 0; i < FRACTIONS_SIZE; ++i) {
                String s = STR_FRACTIONS[i];
                int pos = quantity.indexOf(s);

                if (pos < 0) continue;

                int size = s.length();
                float number = DECIMALS[i];
                return new MatchFind<>(pos, size, s, number);
            }

            Matcher matcher = numberPattern.matcher(quantity);
            while (matcher.find()) {
                String s = matcher.group();
                int pos = quantity.indexOf(s);

                if (pos < 0) continue;

                int size = s.length();
                try {
                    float f = Float.parseFloat(s);
                    return new MatchFind<>(pos, size, s, f);
                } catch (Exception e) {
                    return new MatchFind<>(0, 0, "", 0.0f);
                }
            }

            return new MatchFind<>(0, 0, "", 0.0f);
        }

        private String formatAsPortion(int customPortion) {
            float calcul = this.value * ((float) customPortion / (this.defaultPortion > 0 ? this.defaultPortion : 1));
            String strValue = formatFloat(calcul);

            return String.format("%s%s%s", leftLabel, strValue, rightLabel);
        }

        private String formatFloat(float f) {
            if (f == 0.0f) return "";

            String str = String.format(Locale.getDefault(), (f % 1 != 0) ? "%.2f" : "%.0f", f);

            for (int i = 0; i < FRACTIONS_SIZE; ++i) {
                float dec = DECIMALS[i];
                String strDec = String.format(Locale.getDefault(), "%.2f", dec);
                if (str.equals(strDec)) return charFractionDisplay ? CHAR_FRACTIONS[i] : STR_FRACTIONS[i];
            }

            return str;
        }

    }

    public static class Serialized {
        public long ingredientId;
        public String quantity;

        public Serialized(long id, String quantity) {
            this.ingredientId = id;
            this.quantity = quantity;
        }
    }
}
