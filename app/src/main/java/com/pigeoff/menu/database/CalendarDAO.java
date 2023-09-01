package com.pigeoff.menu.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CalendarDAO {
    @Query("SELECT * FROM CalendarEntity ORDER BY id DESC")
    List<CalendarEntity> select();

    @Query("SELECT * FROM CalendarEntity WHERE datetime >= :start AND datetime < :end ORDER BY id")
    List<CalendarEntity> select(long start, long end);

    @Query("SELECT * FROM CalendarEntity WHERE id = :id")
    CalendarEntity select(int id);

    @Update
    void update(CalendarEntity recipe);

    @Insert
    void insert(CalendarEntity recipe);

    @Delete
    void delete(CalendarEntity recipe);
}
