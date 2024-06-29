package com.example.list_of_buy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.list_of_buy.ui.theme.List_of_buyTheme

class MainActivity : ComponentActivity() {
    private lateinit var dbHelper: ShoppingDbHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dbHelper = ShoppingDbHelper(this@MainActivity)
        enableEdgeToEdge()
        setContent {
            List_of_buyTheme {
                ShoppingApp(dbHelper)
            }
        }
    }
}

@Composable
fun ShoppingApp(dbHelper: ShoppingDbHelper) {
    var items by remember { mutableStateOf(getAllItems(dbHelper)) }
    var newItemName by remember { mutableStateOf("") }
    var newQuantity by remember { mutableStateOf("") }

    items = getAllItems(dbHelper)

    Scaffold { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            TextField(
                value = newItemName, onValueChange = { newItemName = it },
                label = { Text(text = "Item Name") },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = newQuantity, onValueChange = { newQuantity = it },
                label = { Text(text = "Quantity") },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    addItem(dbHelper, newItemName, newQuantity.toInt())
                    items = getAllItems(dbHelper)
                    newItemName = ""
                    newQuantity = ""
                }),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (newItemName.isNotBlank() && newQuantity.isNotBlank()) {
                        addItem(dbHelper, newItemName, newQuantity.toInt())
                        items = getAllItems(dbHelper)
                        newItemName = ""
                        newQuantity = ""
                    }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(text = "Add Item")
            }
            Spacer(modifier = Modifier.height(16.dp))
            ShoppingList(items = items, onDeleteItem = { id ->
                deleteItem(dbHelper, id)
                items = getAllItems(dbHelper)
            }, onUpdateItem = { id, itemName, quantity ->
                updateItem(dbHelper, id, itemName, quantity)
                items = getAllItems(dbHelper)
            })
        }
    }
}

@Composable
fun ShoppingList(
    items: List<ShoppingItem>,
    onDeleteItem: (Long) -> Unit,
    onUpdateItem: (Long, String, Int) -> Unit
) {
    LazyColumn {
        items(items) { item ->
            var editMode by remember { mutableStateOf(false) }
            var updatedName by remember { mutableStateOf(item.itemName) }
            var updatedQuantity by remember { mutableStateOf(item.quantity.toString()) }

            if (editMode) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextField(
                        value = updatedName,
                        onValueChange = { updatedName = it },
                        label = { Text("Item Name") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TextField(
                        value = updatedQuantity,
                        onValueChange = { updatedQuantity = it },
                        label = { Text("Quantity") },
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        onUpdateItem(item.id, updatedName, updatedQuantity.toInt())
                        editMode = false
                    }) {
                        Icon(Icons.Default.Check, contentDescription = "Save")
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "${item.itemName} - ${item.quantity}")
                    Row {
                        IconButton(onClick = { editMode = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = { onDeleteItem(item.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            }
            HorizontalDivider()
        }
    }
}

