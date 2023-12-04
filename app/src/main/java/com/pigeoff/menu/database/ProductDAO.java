package com.pigeoff.menu.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ProductDAO {
    @Query("SELECT * FROM ProductEntity WHERE permanent = 1 ORDER BY LOWER(label)")
    LiveData<List<ProductEntity>> getAll();

    @Query("SELECT * FROM ProductEntity WHERE label = :name")
    ProductEntity selectByName(String name);

    @Update
    void updateProduct(ProductEntity product);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertProduct(ProductEntity product);

    @Query("DELETE FROM ProductEntity WHERE id = :id AND permanent = 0")
    void deleteTemporaryProduct(long id);

    @Query("DELETE FROM ProductEntity WHERE permanent = 0 AND id IN (SELECT ingredientId FROM GroceryEntity WHERE checked = 1)")
    void deleteTemporaryCheckedProducts();

    @Query("DELETE FROM ProductEntity WHERE permanent = 0")
    void deleteTemporaryProducts();

    @Delete
    void deleteProduct(ProductEntity product);
}
