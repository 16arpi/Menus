package com.pigeoff.menu.database;


import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.Calendar;

public class CalendarWithRecipe {
    @Embedded public CalendarEntity calendar;
    @Relation(
            parentColumn = "recipe",
            entityColumn = "id"
    )
    public RecipeEntity recipe;
}
