package com.example.oollan.inventoryapp.inventory;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.example.oollan.inventoryapp.utils.DbHelper;

import java.util.Objects;

import static com.example.oollan.inventoryapp.utils.Contract.*;

public class InventoryProvider extends ContentProvider {

    private DbHelper dbHelper;
    private static final int ITEMS = 0;
    private static final int ITEM_ID = 1;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(CONTENT_AUTHORITY, INVENTORY_PATH, ITEMS);
        uriMatcher.addURI(CONTENT_AUTHORITY, INVENTORY_PATH + "/#", ITEM_ID);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        switch (uriMatcher.match(uri)) {
            case ITEMS:
                cursor = dbHelper.getReadableDatabase()
                        .query(TABLE_NAME, projection, selection, selectionArgs,
                                null, null, sortOrder);
                break;
            case ITEM_ID:
                selection = _ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = dbHelper.getReadableDatabase()
                        .query(TABLE_NAME, projection, selection, selectionArgs,
                                null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(Objects.requireNonNull(getContext()).getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        if (uriMatcher.match(uri) == ITEMS) {
            return insertItem(uri, contentValues);
        } else {
            throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues,
                      String selection, String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case ITEMS:
                return updateItem(uri, contentValues, selection, selectionArgs);
            case ITEM_ID:
                selection = _ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateItem(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int rowDeleted;
        switch (uriMatcher.match(uri)) {
            case ITEMS:
                rowDeleted = dbHelper.getWritableDatabase()
                        .delete(TABLE_NAME, selection, selectionArgs);
                break;
            case ITEM_ID:
                selection = _ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowDeleted = dbHelper.getWritableDatabase()
                        .delete(TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowDeleted != 0) {
            Objects.requireNonNull(getContext()).getContentResolver()
                    .notifyChange(uri, null);
        }
        return rowDeleted;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)) {
            case ITEMS:
                return CONTENT_LIST_TYPE;
            case ITEM_ID:
                return CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException
                        ("Unknown URI " + uri + " with match " + uriMatcher.match(uri));
        }
    }

    private Uri insertItem(Uri uri, ContentValues values) {
        long id = dbHelper.getWritableDatabase().insert(TABLE_NAME, null, values);
        if (id == -1) {
            return null;
        }
        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    private int updateItem(Uri uri, ContentValues values,
                          String selection, String[] selectionArgs) {
        if (values.size() == 0) {
            return 0;
        }
        if (dbHelper.getWritableDatabase()
                .update(TABLE_NAME, values, selection, selectionArgs) != 0) {
            Objects.requireNonNull(getContext()).getContentResolver()
                    .notifyChange(uri, null);
        }
        return dbHelper.getWritableDatabase()
                .update(TABLE_NAME, values, selection, selectionArgs);
    }
}