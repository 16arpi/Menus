package com.pigeoff.menu.database;

import androidx.room.Embedded;
import androidx.room.Relation;

public class GroceryWithProduct {
    @Embedded public GroceryEntity grocery;
    @Relation(
            parentColumn = "ingredientId",
            entityColumn = "id"
    )
    public ProductEntity product;
}
