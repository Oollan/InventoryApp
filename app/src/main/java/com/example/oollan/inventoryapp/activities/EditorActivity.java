package com.example.oollan.inventoryapp.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oollan.inventoryapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.oollan.inventoryapp.utils.Contract.*;

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private int quantityNumber;
    private boolean itemHasChanged = false;
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            itemHasChanged = true;
            return false;
        }
    };
    @BindView(R.id.product_name)
    EditText productName;
    @BindView(R.id.price)
    EditText price;
    @BindView(R.id.quantity)
    TextView quantity;
    @BindView(R.id.supplier_name)
    EditText supplierName;
    @BindView(R.id.supplier_phone_number)
    EditText supplierPhoneNumber;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        ButterKnife.bind(this);
        if (getIntent().getData() == null) {
            setTitle(R.string.title_new_item);
            quantityNumber = 0;
            quantity.setText(String.valueOf(quantityNumber));
        } else {
            setTitle(R.string.title_edit_item);
            getLoaderManager().initLoader(INVENTORY_LOADER, null, this);
        }
        productName.setOnTouchListener(touchListener);
        price.setOnTouchListener(touchListener);
        quantity.setOnTouchListener(touchListener);
        supplierName.setOnTouchListener(touchListener);
        supplierPhoneNumber.setOnTouchListener(touchListener);
    }

    @OnClick({R.id.increment, R.id.decrement})
    public void onClick(View view) {
        if (view.getId() == R.id.increment && quantityNumber < 100) {
            quantity.setText(String.valueOf(++quantityNumber));
        } else if (view.getId() == R.id.decrement && quantityNumber > 0) {
            quantity.setText(String.valueOf(--quantityNumber));
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (getIntent().getData() == null) {
            menu.findItem(R.id.action_delete).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            if (!productName.getText().toString().trim().isEmpty() &&
                    !price.getText().toString().trim().isEmpty()) {
                saveItem();
                finish();
                return true;
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_item_invalid),
                        Toast.LENGTH_SHORT).show();
            }
        } else if (item.getItemId() == R.id.action_delete) {
            showDeleteConfirmationDialog();
        } else if (item.getItemId() == R.id.action_dial){
            if (!supplierPhoneNumber.getText().toString().trim().isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" +
                        supplierPhoneNumber.getText().toString().trim()));
                startActivity(intent);
            } else {
                Toast.makeText(this, getString(R.string.editor_dial_supplier_invalid),
                        Toast.LENGTH_SHORT).show();
            }
        } else if (item.getItemId() == android.R.id.home) {
            if (!itemHasChanged) {
                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                return true;
            }
            DialogInterface.OnClickListener discardButtonClickListener =
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            NavUtils.navigateUpFromSameTask(EditorActivity.this);
                        }
                    };
            showUnsavedChangesDialog(discardButtonClickListener);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if (!itemHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {_ID, PRODUCT_NAME, PRICE,
                QUANTITY, SUPPLIER_NAME, SUPPLIER_PHONE_NUMBER};
        return new CursorLoader(this, getIntent().getData(), projection,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            productName.setText(cursor.getString(cursor.getColumnIndex(PRODUCT_NAME)));
            price.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex(PRICE))));
            quantityNumber = cursor.getInt(cursor.getColumnIndex(QUANTITY));
            quantity.setText(String.valueOf(quantityNumber));
            supplierName.setText(cursor.getString(cursor.getColumnIndex(SUPPLIER_NAME)));
            supplierPhoneNumber.setText(cursor.getString
                    (cursor.getColumnIndex(SUPPLIER_PHONE_NUMBER)));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        productName.setText("");
        price.setText("");
        quantityNumber = 0;
        quantity.setText(String.valueOf(quantityNumber));
        supplierName.setText("");
        supplierPhoneNumber.setText("");
    }

    private void saveItem() {
        ContentValues values = new ContentValues();
        values.put(PRODUCT_NAME, productName.getText().toString().trim());
        values.put(PRICE, String.valueOf((price.getText().toString().trim())));
        values.put(QUANTITY, String.valueOf((quantity.getText().toString().trim())));
        values.put(SUPPLIER_NAME, supplierName.getText().toString().trim());
        values.put(SUPPLIER_PHONE_NUMBER, supplierPhoneNumber.getText().toString().trim());
        if (getIntent().getData() == null) {
            if (getContentResolver().insert(CONTENT_URI, values) == null) {
                Toast.makeText(this, getString(R.string.editor_insert_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            if (getContentResolver().update(getIntent().getData(), values,
                    null, null) == 0) {
                Toast.makeText(this, getString(R.string.editor_upgrade_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener
                                                  discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                deleteItem();

            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteItem() {
        if (getIntent().getData() != null) {
            if (getContentResolver().delete(getIntent().getData(),
                    null, null) == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }
}