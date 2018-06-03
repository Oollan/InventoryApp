package com.example.oollan.inventoryapp.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.oollan.inventoryapp.utils.Contract.*;

public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "inventory.db";
    private static final int DATABASE_VERSION = 1;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_PETS_TABLE = "CREATE TABLE " + TABLE_NAME + " ( " + _ID +
                " INTEGER PRIMARY KEY, " + PRODUCT_NAME +
                " TEXT NOT NULL, " + PRICE +
                " INTEGER NOT NULL, " + QUANTITY +
                " INTEGER NOT NULL, " + SUPPLIER_NAME + "" +
                " TEXT NOT NULL, " + SUPPLIER_PHONE_NUMBER +
                " TEXT NOT NULL" + ");";
        db.execSQL(SQL_CREATE_PETS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}