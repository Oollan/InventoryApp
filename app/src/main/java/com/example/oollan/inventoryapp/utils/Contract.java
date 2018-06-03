package com.example.oollan.inventoryapp.utils;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class Contract {

    private Contract() {
    }

    public static final String CONTENT_AUTHORITY = "com.example.oollan.inventoryapp";
    public static final String INVENTORY_PATH = "inventory";
    public static final Uri CONTENT_URI = Uri.withAppendedPath
            (Uri.parse("content://" + CONTENT_AUTHORITY), INVENTORY_PATH);
    public static final String CONTENT_LIST_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + INVENTORY_PATH;
    public static final String CONTENT_ITEM_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + INVENTORY_PATH;
    public static final String TABLE_NAME = "Inventory";
    public static final String _ID = BaseColumns._ID;
    public static final String PRODUCT_NAME = "product_name";
    public static final String PRICE = "price";
    public static final String QUANTITY = "quantity";
    public static final String SUPPLIER_NAME = "supplier_name";
    public static final String SUPPLIER_PHONE_NUMBER = "supplier_phone_number";
    public static final int INVENTORY_LOADER = 0;
    public static final String COLON = " :  ";
}