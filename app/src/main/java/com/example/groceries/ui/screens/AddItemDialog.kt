package com.example.groceries.ui.screens

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.DatePicker
import androidx.compose.material3.Button
import androidx.compose.material3.DatePickerDialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults

import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.LaunchedEffect

import androidx.compose.ui.Modifier

import androidx.compose.ui.unit.dp
import com.example.groceries.ui.data.GroceryItem
import com.example.groceries.ui.data.ItemMemory

val typeCategories = listOf("Dairy","Fruits","Vegetables","Grains","Snacks")
val unitOptions = listOf("g","kg","ml","L","pcs")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemDialog(
    onDismiss: () -> Unit,
    memoryMap: Map<String, ItemMemory>,
    itemToEdit: GroceryItem? = null,
    onConfirm: (GroceryItem) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }

    var quantityValue by remember { mutableStateOf("") }
    var quantityUnit by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<Long?>(null) }

    var unitExpanded by remember { mutableStateOf(false) }

    //for autosuggesting the category and quantity unit
    LaunchedEffect(name) {
        memoryMap[name.lowercase()]?.let {
            if (category.isBlank()) category = it.category
            if (quantityUnit.isBlank()) quantityUnit = it.defaultUnit
        }
    }

    //for editing an item
    LaunchedEffect(itemToEdit) {
        itemToEdit?.let {
            name = it.name
            category = it.category
            quantityValue = it.quantityValue.toString()
            quantityUnit = it.quantityUnit
            selectedDate = it.expiry
        }
    }

    val isValid =
        name.isNotBlank() &&
        category.isNotBlank() &&
        quantityValue.toIntOrNull() != null &&
        quantityUnit.isNotBlank() &&
        selectedDate != null

    var expanded by remember { mutableStateOf(false) }

    //date picker dalogue
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedDate = datePickerState.selectedDateMillis
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false}) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    //the dialog for adding or editing an item
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    val item = if (itemToEdit == null) { //checks for the condition (editing or adding)
                        GroceryItem(
                            name = name,
                            category = category,
                            expiry = selectedDate!!,
                            quantityValue = quantityValue.toInt(),
                            quantityUnit = quantityUnit
                        )
                    } else {
                        itemToEdit.copy(
                            name = name,
                            category = category,
                            expiry = selectedDate!!,
                            quantityValue = quantityValue.toInt(),
                            quantityUnit = quantityUnit
                        )
                    }

                    onConfirm(item)
                },
                enabled = isValid
            ) {
                Text(if (itemToEdit == null) "Add" else "Save")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        title = {
            Text(if (itemToEdit == null) "Add Item" else "Edit Item")
        },
        text = { //the fields
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                //the item Name field
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Item name") }
                )
                //the category field
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {
                            category = it
                            expanded = typeCategories.any { cat ->
                                cat.startsWith(it, ignoreCase = true)
                            }
                        },
                        label = { Text("Category") },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                        }
                    )

                    ExposedDropdownMenu(
                        expanded = expanded && typeCategories.isNotEmpty(),
                        onDismissRequest = { expanded = false }
                    ) {
                        typeCategories.forEach { suggestion ->
                            DropdownMenuItem(
                                text = {Text(suggestion) },
                                onClick = {
                                    category = suggestion
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        //the quantity value field
                        OutlinedTextField(
                            value = quantityValue,
                            onValueChange = {
                                if (it.all { ch -> ch.isDigit() }) quantityValue = it
                            },
                            label = { Text("Qty") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        //the quantity unit field
                        ExposedDropdownMenuBox(
                            expanded = unitExpanded,
                            onExpandedChange = { unitExpanded = !unitExpanded }
                        ) {
                            OutlinedTextField(
                                value = quantityUnit,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Unit") },
                                modifier = Modifier
                                    .menuAnchor()
                                    .weight(1f),
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(unitExpanded)
                                }
                            )

                            ExposedDropdownMenu(
                                expanded = unitExpanded,
                                onDismissRequest = { unitExpanded = false }
                            ) {
                                unitOptions.forEach { unit ->
                                    DropdownMenuItem(
                                        text = { Text(unit) },
                                        onClick = {
                                            quantityUnit = unit
                                            unitExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                //the expiry date picker
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = selectedDate?.let {
                            android.text.format.DateFormat
                                .format("dd MM yyyy",it)
                                .toString()
                        } ?: "Expiry Date"
                    )
                }

            }
        }
    )
}
