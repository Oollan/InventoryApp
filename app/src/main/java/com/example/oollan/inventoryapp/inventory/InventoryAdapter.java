package com.example.oollan.inventoryapp.inventory;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.oollan.inventoryapp.R;
import com.example.oollan.inventoryapp.activities.EditorActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.oollan.inventoryapp.utils.Contract.*;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> {

    private Context context;
    private CursorAdapter cursorAdapter;

    public InventoryAdapter(final Context context, final Cursor c) {
        this.context = context;
        this.cursorAdapter = new CursorAdapter(this.context, c, 0) {

            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                return LayoutInflater.from(context).inflate
                        (R.layout.recycler_item, parent, false);
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                InventoryViewHolder holder = new InventoryViewHolder(view);
                holder.id = cursorAdapter.getCursor().getLong(cursor.getColumnIndex(_ID));
                holder.name.setText(cursor.getString(cursor.getColumnIndex(PRODUCT_NAME)));
                holder.price.setText(context.getString(R.string.price) + COLON +
                        cursor.getString(cursor.getColumnIndex(PRICE)) + "$");
                holder.quantity.setText(context.getString(R.string.quantity) + COLON +
                        cursor.getString(cursor.getColumnIndex(QUANTITY)));
            }
        };
    }

    @Override
    public InventoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new InventoryViewHolder(cursorAdapter.newView
                (context, cursorAdapter.getCursor(), parent)) {
        };
    }

    @Override
    public void onBindViewHolder(InventoryViewHolder holder, int position) {
        cursorAdapter.getCursor().moveToPosition(position);
        cursorAdapter.bindView(holder.itemView, context, cursorAdapter.getCursor());
    }

    @Override
    public int getItemCount() {
        return cursorAdapter.getCount();
    }

    public void swapCursor(Cursor c) {
        cursorAdapter.swapCursor(c);
        notifyDataSetChanged();
    }

    class InventoryViewHolder extends RecyclerView.ViewHolder {

        long id;
        @BindView(R.id.product_name)
        TextView name;
        @BindView(R.id.price)
        TextView price;
        @BindView(R.id.quantity)
        TextView quantity;
        @BindView(R.id.btn_order)
        Button order;

        InventoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick
        void onClick() {
            Intent intent = new Intent(context, EditorActivity.class);
            intent.setData(ContentUris.withAppendedId
                    (CONTENT_URI, id));
            context.startActivity(intent);
        }

        @OnClick(R.id.btn_order)
        void btnClick() {
            String[] splits = quantity.getText().toString().split(COLON);
            int results = Integer.parseInt(splits[1]) - 1;
            if (results > -1) {
                ContentValues values = new ContentValues();
                values.put(QUANTITY, results);
                context.getContentResolver().update(ContentUris.withAppendedId
                        (CONTENT_URI, id), values, null, null);
            }
        }
    }
}