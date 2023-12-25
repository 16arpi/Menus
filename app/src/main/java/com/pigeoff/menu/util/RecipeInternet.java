package com.pigeoff.menu.util;


import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RecipeInternet {

    private static final String CSS_JSON_LD = "script[type=\"application/ld+json\"]";
    private static final String GRAPH = "@graph";
    private static final String TYPE = "@type";
    private static final String RECIPE_TYPE = "Recipe";
    private static final String NAME = "name";
    private static final String HOW_TO_STEP = "HowToStep";
    private static final String RECIPE_YIELD = "recipeYield";
    private static final String RECIPE_INGREDIENT = "recipeIngredient";
    private static final String RECIPE_INSTRUCTIONS = "recipeInstructions";
    private static final String TEXT = "text";

    public Model recipe;

    public static class RecipeInternetException extends Exception {
        public RecipeInternetException(String message) {
            super(message);
        }
    }

    // JSON Objects

    public static class Model {
        public String title = "";
        public int portions = 1;
        public List<IngredientParser> ingredients = new ArrayList<>();
        public List<String> steps = new ArrayList<>();
    }

    public RecipeInternet(String url) throws RecipeInternetException {
        try {
            Document doc = Jsoup.connect(url).get();
            Elements scripts = doc.select(CSS_JSON_LD);
            Model model = null;
            for (Element s : scripts) {
                String content = s.html();
                if (content.trim().indexOf("[") == 0) model = treatJSONList(new JSONArray(content));
                if (content.trim().indexOf("{") == 0) model = treatJSONSingleton(new JSONObject(content));
                if (model != null) {
                    this.recipe = model;
                    return;
                }
            }
            throw new RecipeInternetException("Unable to find recipe");
        } catch (IOException | JSONException e) {
            throw new RecipeInternetException(e.getMessage());
        }
    }

    private RecipeInternet.Model treatJSONList(JSONArray array) throws JSONException {
        for (int i = 0; i < array.length(); ++i) {
            Model m = treatJSONSingleton(array.getJSONObject(i));
            if (m != null) return m;
        }
        return null;
    }

    private RecipeInternet.Model treatJSONSingleton(JSONObject object) throws JSONException {
        if (object.has(GRAPH)) {
            JSONArray array = object.optJSONArray(GRAPH);
            if (array == null) return null;
            return treatJSONList(array);
        }

        if (!object.optString(TYPE).equals(RECIPE_TYPE)) {
            return null;
        }

        Model model = new Model();

        model.title = object.optString(NAME);

        // Ingredients
        model.ingredients = new ArrayList<>();
        JSONArray arrayIngredients = object.optJSONArray(RECIPE_INGREDIENT);
        if (arrayIngredients != null) {
            for (int i = 0; i < arrayIngredients.length(); ++i) {
                String ingr = arrayIngredients.optString(i);
                if (ingr.trim().isEmpty()) continue;
                model.ingredients.add(IngredientParser.from(ingr));
            }
        }

        // Steps
        model.steps = new ArrayList<>();
        JSONArray arraySteps = object.optJSONArray(RECIPE_INSTRUCTIONS);
        if (arraySteps == null) {
            model.steps.add(object.optString(RECIPE_INSTRUCTIONS));
        } else {
            for (int i = 0; i < arraySteps.length(); ++i) {
                JSONObject howToObject = arraySteps.optJSONObject(i);
                if (howToObject == null) {
                    String step = arraySteps.optString(i);
                    if (!step.trim().isEmpty()) model.steps.add(step);
                } else {
                    if (howToObject.optString(TYPE).equals(HOW_TO_STEP)) {
                        String step = howToObject.optString(TEXT);
                        if (!step.trim().isEmpty()) model.steps.add(step);
                    }
                }
            }
        }

        model.portions = Util.findNumberInString(object.optString(RECIPE_YIELD), 1);

        return model;

    }
    public static RecipeInternet.Model from(String json) {
        return new Gson().fromJson(json, RecipeInternet.Model.class);
    }
}
