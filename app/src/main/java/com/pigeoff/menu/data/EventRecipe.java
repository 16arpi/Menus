package com.pigeoff.menu.data;

import com.pigeoff.menu.database.GroceryEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventRecipe {
    public long eventId;
    public long recipeId;

    public String recipeLabel;
    public long datetime;

    public List<GroceryEntity> groceries;

    public EventRecipe(long eventId, long recipeId, String recipeLabel, long datetime, List<GroceryEntity> groceries) {
        this.eventId = eventId;
        this.recipeId = recipeId;
        this.recipeLabel = recipeLabel;
        this.datetime = datetime;
        this.groceries = groceries;
    }

    public static List<EventRecipe> fromGroceries(List<GroceryEntity> groceries) {
        HashMap<Long, EventRecipe> eventRecipes = new HashMap<>();

        for (GroceryEntity g : groceries) {
            if (!eventRecipes.containsKey(g.eventId)) {
                List<GroceryEntity> gro = new ArrayList<>();
                gro.add(g);
                eventRecipes.put(g.eventId, new EventRecipe(
                        g.eventId,
                        g.recipeId,
                        g.recipeLabel,
                        g.datetime,
                        gro
                ));
            } else {
                EventRecipe r = eventRecipes.get(g.eventId);
                r.groceries.add(g);
            }
        }

        return new ArrayList<>(eventRecipes.values());
    }
}
