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
    List<ProductEntity> getProducts();

    @Query("UPDATE ProductEntity SET checked = :check WHERE id = :id")
    void checkProduct(long id, boolean check);

    @Query("DELETE FROM productentity WHERE checked = 1")
    void deleteAllChecked();

    @Query("DELETE FROM productentity")
    void deleteAllItems();

    @Insert
    void addProduct(ProductEntity product);

    @Update
    void updateProduct(ProductEntity product);

    @Delete
    void deleteProduct(ProductEntity product);
}
