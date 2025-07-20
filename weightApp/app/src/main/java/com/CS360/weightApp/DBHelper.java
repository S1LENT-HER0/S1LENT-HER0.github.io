package com.CS360.weightApp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, "AppDatabase.db", null, 1); // either make or open the database
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // set up both the users and the weight entries tables
        db.execSQL("CREATE TABLE users(id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT UNIQUE, password TEXT)");
        db.execSQL("CREATE TABLE items(id INTEGER PRIMARY KEY AUTOINCREMENT, item_name TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        // if things change, wipe and recreate (not ideal for prod, fine for this)
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS items");
        onCreate(db);
    }

    // used when I make a new account
    public boolean insertUser(String username, String password) {
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);
        return getWritableDatabase().insert("users", null, values) != -1;
    }

    // check if login info actually matches anything in DB
    public boolean checkLogin(String username, String password) {
        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT * FROM users WHERE username=? AND password=?", new String[]{username, password});
        return cursor.getCount() > 0;
    }

    // add a new weight entry to the table
    public void insertItem(String item) {
        ContentValues values = new ContentValues();
        values.put("item_name", item);
        getWritableDatabase().insert("items", null, values);
    }

    // get all items in a format the recycler adapter expects
    public Cursor getAllItems() {
        return getReadableDatabase().rawQuery("SELECT id AS _id, item_name FROM items", null);
    }
}
