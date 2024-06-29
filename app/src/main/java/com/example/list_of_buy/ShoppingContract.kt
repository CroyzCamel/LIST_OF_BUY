package com.example.list_of_buy

import android.provider.BaseColumns

object ShoppingContract  {
    object ShoppingEntry : BaseColumns {
        const val TABLE_NAME = "shopping_list"
        const val COLUMN_ITEM_NAME = "item_name"
        const val COLUMN_QUANTITY = "quantity"
    }
}