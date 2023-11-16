package com.pigeoff.menu.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CalendarDAO {
    @Transaction
    @Query("SELECT * FROM CalendarEntity ORDER BY id")
    LiveData<List<CalendarWithRecipe>> select();

    @Transaction
    @Query("SELECT * FROM CalendarEntity WHERE datetime >= :start AND datetime < :end ORDER BY id")
    LiveData<List<CalendarWithRecipe>> select(long start, long end);

    @Transaction
    @Query("SELECT * FROM CalendarEntity WHERE id = :id")
    LiveData<CalendarWithRecipe> select(int id);

    @Update
    void update(CalendarEntity recipe);

    @Insert
    void insert(CalendarEntity recipe);

    @Query("DELETE FROM CalendarEntity WHERE recipe = :recipeId")
    void deleteForRecipe(long recipeId);

    @Delete
    void delete(CalendarEntity recipe);
}
