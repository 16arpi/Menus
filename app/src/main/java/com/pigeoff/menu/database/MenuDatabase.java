package com.pigeoff.menu.database;

import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {
                RecipeEntity.class,
                CalendarEntity.class,
                GroceryEntity.class,
                ProductEntity.class
        },
        version = 5,
        autoMigrations = {
                @AutoMigration(from = 1, to = 2),
                @AutoMigration(from = 2, to = 3),
                @AutoMigration(from = 3, to = 4)
        }
)
public abstract class MenuDatabase extends RoomDatabase {
    public abstract RecipeDAO recipeDAO();
    public abstract CalendarDAO calendarDAO();
    public abstract GroceryDAO groceryDAO();
    public abstract ProductDAO productDAO();
}
