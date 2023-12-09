package com.pigeoff.menu.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface GroceryDAO {
    @Transaction
    @Query("SELECT * FROM GroceryEntity ORDER BY id")
    LiveData<List<GroceryWithProduct>> getGroceries();

    @Transaction
    @Query("SELECT * FROM GroceryEntity WHERE eventId >= 0 ORDER BY id")
    LiveData<List<GroceryWithProduct>> getEventsGroceries();

    @Query("UPDATE GroceryEntity SET checked = :check WHERE id = :id")
    void checkGrocery(long id, boolean check);

    @Query("DELETE FROM GroceryEntity WHERE checked = 1")
    void deleteAllChecked();

    @Query("DELETE FROM GroceryEntity")
    void deleteAllItems();

    @Query("DELETE FROM GroceryEntity WHERE eventId = :eventId")
    void deleteGroceriesForCalendar(long eventId);

    @Query("DELETE FROM GroceryEntity WHERE eventId = :eventId AND ingredientId = :ingredienId")
    void deleteGroceriesForCalendar(long ingredienId, long eventId);

    @Query("DELETE FROM GroceryEntity WHERE recipeId = :recipeId")
    void deleteGroceriesForRecipe(long recipeId);

    @Insert
    void addGrocery(GroceryEntity product);

    @Delete
    void deleteGrocery(GroceryEntity product);

}
