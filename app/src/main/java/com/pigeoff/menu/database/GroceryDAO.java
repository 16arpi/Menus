package com.pigeoff.menu.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface GroceryDAO {
    @Query("SELECT * FROM GroceryEntity ORDER BY id")
    List<GroceryEntity> getGroceries();

    @Query("UPDATE GroceryEntity SET checked = :check WHERE id = :id")
    void checkGrocery(long id, boolean check);

    @Query("DELETE FROM GroceryEntity WHERE checked = 1")
    void deleteAllChecked();

    @Query("DELETE FROM GroceryEntity")
    void deleteAllItems();

    @Query("DELETE FROM GroceryEntity WHERE eventId = :eventId")
    void deleteGroceriesForCalendar(long eventId);

    @Query("DELETE FROM GroceryEntity WHERE recipeId = :recipeId")
    void deleteGroceriesForRecipe(long recipeId);

    @Insert
    void addGrocery(GroceryEntity product);

    @Update
    void updateGrocery(GroceryEntity product);

    @Delete
    void deleteGrocery(GroceryEntity product);
}
