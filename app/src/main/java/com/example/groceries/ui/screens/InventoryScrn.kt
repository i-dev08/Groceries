package com.example.groceries.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material3.*

import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.draw.clip

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel

import java.text.SimpleDateFormat

import java.util.concurrent.TimeUnit
import java.util.*

import com.example.groceries.ui.data.DatabaseProvider
import com.example.groceries.ui.data.GroceryItem
import com.example.groceries.ui.data.ItemMemory
import com.example.groceries.ui.viewmodel.InventoryViewModel
import com.example.groceries.ui.viewmodel.InventoryViewModelFactory

val categories = listOf("All","Dairy","Fruits","Vegetables","Grains","Snacks")

fun formatDate(millis: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy",Locale.getDefault())
    return sdf.format(Date(millis))
}

//function for getting the color corresponding to the card
fun expiryColor(expiryMillis: Long): Color {
   val now = System.currentTimeMillis()
    val daysLeft = TimeUnit.MILLISECONDS.toDays(expiryMillis - now)

    return when {
        daysLeft < 0 -> Color(0xFFFFCDD2)
        daysLeft <= 2 -> Color(0xFFFFE0B2)
        daysLeft <= 5 -> Color(0xFFFFF9C4)
        else -> Color(0xFFC8E6C9)
    }
}

@Composable
fun InventoryScrn() {

    val context = LocalContext.current
    val db = remember { DatabaseProvider.getDatabase(context) }
    val dao = db.groceryDao()

    val viewModel: InventoryViewModel = viewModel(
        factory = InventoryViewModelFactory(dao)
    )

    val inventory = viewModel.inventory
        .observeAsState(emptyList())
        .value ?: emptyList()

    val memoryMap = viewModel.memoryMap
        .observeAsState(emptyMap())
        .value ?: emptyMap()


    var showDialog by remember { mutableStateOf(false)  }
    var selectedCategory by remember { mutableStateOf("All") }
    var itemToEdit by remember { mutableStateOf<GroceryItem?>(null) }

    val filteredItems = if (selectedCategory == "All") { //for filtering
        inventory
    } else {
        inventory.filter { it.category == selectedCategory }
    }.sortedBy { it.expiry }

    //condition for showing add/edit dialog
    if (showDialog || itemToEdit != null) {
        AddItemDialog(
            onDismiss = {
                showDialog = false
                itemToEdit = null
            },
            memoryMap = memoryMap,
            itemToEdit = itemToEdit,
            onConfirm = { item ->
                if (itemToEdit == null) {
                    viewModel.addItem(item)
                } else {
                    viewModel.updateItem(item)
                }
                showDialog = false
                itemToEdit = null
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true }
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add item"
                )
            }
        }
    ) { paddingValues ->

        InventoryContent(
            modifier = Modifier.padding(paddingValues),
            inventory = filteredItems,
            onEdit = { itemToEdit = it },
            selectedCategory = selectedCategory,
            onCategorySelected = { selectedCategory = it },
            onDelete = { viewModel.deleteItem(it) }
        )
    }
}

//main content of the inventory tab
@Composable
fun InventoryContent(
    modifier: Modifier = Modifier,
    inventory: List<GroceryItem>,
    onEdit: (GroceryItem) -> Unit,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    onDelete: (GroceryItem) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        InventoryHeader()
        Spacer(modifier = modifier.height(12.dp))

        CategoryFilters(
            selectedCategory = selectedCategory,
            onCategorySelected = onCategorySelected
        )
        Spacer(modifier = Modifier.height(24.dp))

        if (inventory.isEmpty()) { //checks for state of inve3ntory
            EmptyInventoryState()
        } else {
            InventoryList(
                items = inventory,
                onEdit = onEdit,
                onDelete = onDelete
            )
        }
    }
}


//header
@Composable
fun InventoryHeader() {
    Text(
        text = "Inventory",
        style = MaterialTheme.typography.headlineSmall
    )
}


//category based filtering
@Composable
fun CategoryFilters(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            FilterChip(
                selected = category == selectedCategory,
                label = category,
                onClick = { onCategorySelected(category) }
            )
        }
    }
}


//the filter chips used for filtering based on category
@Composable
fun FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    AssistChip(
        onClick = onClick,
        label = { Text(label) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (selected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        )
    )
}


//content to be shown when inventory is empty
@Composable
fun EmptyInventoryState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No items yet." +
                    "Tap + to add groceries.",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryList(
    items: List<GroceryItem>,
    onEdit: (GroceryItem) -> Unit,
    onDelete: (GroceryItem) -> Unit
) {
    LazyColumn {
        items(
            items = items,
            key = { it.id }
        ) { item ->

            //the swipe to dismiss feature
            val dismissState = rememberSwipeToDismissBoxState(
                confirmValueChange = { value ->
                    if (value == SwipeToDismissBoxValue.EndToStart) {
                        onDelete(item)
                        true
                    } else {
                        false
                    }
                }
            )

            Box(modifier = Modifier
                .padding(8.dp)
                .clip(RoundedCornerShape(12.dp))
            ) {
                SwipeToDismissBox(
                    state = dismissState,
                    backgroundContent = {
                        DeleteBackground(dismissState)
                    },
                    content = {
                        InventoryCard(
                            item =item,
                            onClick = { onEdit(item) }
                        )
                    },
                    enableDismissFromStartToEnd = false
                )
            }
        }
    }
}

@Composable
fun InventoryCard(
    item: GroceryItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = expiryColor(item.expiry)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = item.category
            )
            Text(
                text = "${item.quantityValue} ${item.quantityUnit}"
            )
            Text(
                text = "Expiry: ${formatDate(item.expiry)}"
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteBackground(dismissState: SwipeToDismissBoxState) {

    val isSwiping =
        dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart ||
        dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd

    if(!isSwiping) return

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.error)
            .clip(RoundedCornerShape(12.dp))
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete",
            tint = Color.White
        )
    }
}