package com.pigeoff.menu.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ProductDAO {
    @Query("SELECT * FROM ProductEntity ORDER BY label")
    LiveData<List<ProductEntity>> getAll();

    @Update
    void updateProduct(ProductEntity product);

    @Insert
    long insertProduct(ProductEntity product);

    @Delete
    void deleteProduct(ProductEntity product);
}
