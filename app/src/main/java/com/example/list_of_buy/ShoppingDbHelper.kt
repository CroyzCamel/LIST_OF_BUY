package com.example.list_of_buy

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

class ShoppingDbHelper(context: Context) : SQLiteOpenHelper(context,DATABASE_NAME, null,DATABASE_VERSION, ) {

    companion object {
        const val DATABASE_NAME = "shopping.db"
        const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val SQL_CREATE_ENTRIES =
            "CREATE TABLE ${ShoppingContract.ShoppingEntry.TABLE_NAME} (" +
                    "${BaseColumns._ID} INTERGER PRIMARY KEY," +
                    "${ShoppingContract.ShoppingEntry.COLUMN_ITEM_NAME} TEXT," +
                    "${ShoppingContract.ShoppingEntry.COLUMN_QUANTITY} INTEGER)"
        db?.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS ${ShoppingContract.ShoppingEntry.TABLE_NAME}")
        onCreate(db)
    }
}

fun addItem(dbHelper: ShoppingDbHelper, itemName: String, quantity: Int) : Long {
    val db = dbHelper.writableDatabase

    val values = ContentValues().apply {
        put(ShoppingContract.ShoppingEntry.COLUMN_ITEM_NAME, itemName)
        put(ShoppingContract.ShoppingEntry.COLUMN_ITEM_NAME, quantity)
    }

    val id = db.insert(ShoppingContract.ShoppingEntry.TABLE_NAME, null, values)
    db.close()
    return id
}

fun getAllItems(dbHelper: ShoppingDbHelper) : List<ShoppingItem> {
    val db = dbHelper.readableDatabase
    val projection = arrayOf(BaseColumns._ID, ShoppingContract.ShoppingEntry.COLUMN_ITEM_NAME)

    val cursor = db.query(
        ShoppingContract.ShoppingEntry.TABLE_NAME,
        null,null,null,null,null,null
    )
    val items = mutableListOf<ShoppingItem>()
    with(cursor) {
        while (moveToNext()) {
            val id = getLong(getColumnIndexOrThrow(BaseColumns._ID))
            val itemName = getString(getColumnIndexOrThrow(ShoppingContract.ShoppingEntry.COLUMN_ITEM_NAME))
            val quantity = getInt(getColumnIndexOrThrow(ShoppingContract.ShoppingEntry.COLUMN_QUANTITY))
            items.add(ShoppingItem(id,itemName,quantity))
        }
    }
    cursor.close()
    db.close()
    return items
}

fun updateItem(dbHelper: ShoppingDbHelper, id:Long, itemName: String, quantity: Int) {
    val db = dbHelper.writableDatabase

    val values = ContentValues().apply {
        put(ShoppingContract.ShoppingEntry.COLUMN_ITEM_NAME, itemName)
        put(ShoppingContract.ShoppingEntry.COLUMN_QUANTITY, quantity)
    }

    db.update(ShoppingContract.ShoppingEntry.TABLE_NAME, values, "${BaseColumns._ID} = ?", arrayOf(id.toString()) )
    db.close()
}

fun deleteItem(dbHelper: ShoppingDbHelper, id: Long) {
    val db = dbHelper.writableDatabase
    db.delete(ShoppingContract.ShoppingEntry.TABLE_NAME,"${BaseColumns._ID} = ? ", arrayOf(id.toString()))
    db.close()
}

