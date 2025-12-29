package com.example.groceries.ui.data

import android.icu.text.MessagePattern
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GroceryDao {

    @Insert
    suspend fun insertItem(item: GroceryItem)

    @Query("SELECT * FROM grocery_items ORDER BY expiry ASC")
    fun getAllItems(): LiveData<List<GroceryItem>>

    @Query("SELECT * FROM grocery_items WHERE expiry <= :time")
    suspend fun getItemsExpiringBefore(time: Long) : List<GroceryItem>

    @Query("SELECT * FROM grocery_items")
    fun getAllItemsSync(): List<GroceryItem>

    @Delete
    suspend fun deleteItem(item: GroceryItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertItemMemory(item: ItemMemory)

    @Query("SELECT * FROM item_memory")
    fun getItemMemory(): LiveData<List<ItemMemory>>

    @Update
    suspend fun updateItem(item:GroceryItem)
}