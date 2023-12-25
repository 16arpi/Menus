package com.pigeoff.menu;

import android.util.Log;

import com.pigeoff.menu.util.IngredientParser;
import com.pigeoff.menu.util.RecipeInternet;

import org.junit.Test;

public class RecipeInternetTest {

    public static final String URL_TEST = "https://www.marmiton.org/recettes/recette_sushi-californien-maki-inverse_64650.aspx";
    @Test
    public void TestRecipe() {
        try {
            RecipeInternet recipeInternet = new RecipeInternet(URL_TEST);
            RecipeInternet.Model model = recipeInternet.recipe;
            System.out.println(model.title);
            for (IngredientParser i : model.ingredients) System.out.println(String.format("%s | %s", i.quantity, i.label));
            for (String s : model.steps) System.out.println(s);
        } catch (RecipeInternet.RecipeInternetException e) {
            e.printStackTrace();
        }
    }
}
