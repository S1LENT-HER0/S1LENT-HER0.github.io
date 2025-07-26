package com.CS360.weightApp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, "AppDatabase.db", null, 2); // version increased to 2
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // create both tables
        db.execSQL("CREATE TABLE users(id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT UNIQUE, password TEXT)");
        db.execSQL("CREATE TABLE weights(id INTEGER PRIMARY KEY AUTOINCREMENT, dateIso TEXT, weight REAL, goalHit INTEGER, username TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        // handle migration from version 1 to 2
        if (oldV == 1) {
            // drop old items table if exists
            db.execSQL("DROP TABLE IF EXISTS items");
            // create new weights table
            db.execSQL("CREATE TABLE weights(id INTEGER PRIMARY KEY AUTOINCREMENT, dateIso TEXT, weight REAL, goalHit INTEGER, username TEXT)");
        }
    }

    // used when making a new account
    public boolean insertUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);
        return db.insert("users", null, values) != -1;
    }

    // check login credentials
    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM users WHERE username=? AND password=?",
                new String[]{username, password});
        boolean result = cursor.getCount() > 0;
        cursor.close();
        return result;
    }

    // add new weight entry
    public boolean insertWeight(String date, float weight, boolean goalHit, String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("dateIso", date);
        values.put("weight", weight);
        values.put("goalHit", goalHit ? 1 : 0);
        values.put("username", username);
        return db.insert("weights", null, values) != -1;
    }

    // get all weights for user
    public Cursor getWeightsForUser(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT id AS _id, dateIso, weight, goalHit FROM weights WHERE username=? ORDER BY dateIso DESC",
                new String[]{username});
    }
}