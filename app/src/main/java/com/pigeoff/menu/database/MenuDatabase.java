package com.pigeoff.menu.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {
                RecipeEntity.class,
                CalendarEntity.class,
                GroceryEntity.class,
                ProductEntity.class
        },
        version = 1
)
public abstract class MenuDatabase extends RoomDatabase {
    public abstract RecipeDAO recipeDAO();
    public abstract CalendarDAO calendarDAO();
    public abstract GroceryDAO groceryDAO();
    public abstract ProductDAO productDAO();
}
