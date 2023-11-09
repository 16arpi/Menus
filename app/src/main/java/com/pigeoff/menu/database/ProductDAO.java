package com.pigeoff.menu.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ProductDAO {
    @Query("SELECT * FROM ProductEntity ORDER BY label")
    List<ProductEntity> getAll();

    @Query("SELECT * FROM ProductEntity WHERE secion = :section ORDER BY label")
    List<ProductEntity> getAllFromSection(int section);

    @Query("SELECT * FROM ProductEntity WHERE id = :id")
    ProductEntity getProduct(long id);

    @Update
    void updateProduct(ProductEntity product);

    @Insert
    long insertProduct(ProductEntity product);

    @Delete
    void deleteProduct(ProductEntity product);
}
