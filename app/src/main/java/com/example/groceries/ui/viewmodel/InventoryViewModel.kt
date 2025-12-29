package com.example.groceries.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.launch

import com.example.groceries.ui.data.GroceryDao
import com.example.groceries.ui.data.GroceryItem
import com.example.groceries.ui.data.ItemMemory

class InventoryViewModel(
    private val dao: GroceryDao
) : ViewModel() {

    //Inventory List
    val inventory: LiveData<List<GroceryItem>> = dao.getAllItems()

    //memorymap for mapping the item name to the category and quantity
    val memoryMap: LiveData<Map<String, ItemMemory>> =
        dao.getItemMemory().map { list ->
            list.associateBy { it.name.lowercase() }
        }

    //add item to the grocery_items and item_memory tables
    fun addItem(item: GroceryItem) {
        viewModelScope.launch {
            dao.insertItem(item)

            dao.upsertItemMemory(
                ItemMemory(
                    name = item.name,
                    category = item.category,
                    defaultUnit = item.quantityUnit
                )
            )
        }
    }

    fun updateItem(item: GroceryItem) {
        viewModelScope.launch {
            dao.updateItem(item)

            dao.upsertItemMemory(
                ItemMemory(
                    name = item.name,
                    category = item.category,
                    defaultUnit = item.quantityUnit
                )
            )
        }
    }

    fun deleteItem(item: GroceryItem) {
        viewModelScope.launch {
            dao.deleteItem(item)
        }
    }
}