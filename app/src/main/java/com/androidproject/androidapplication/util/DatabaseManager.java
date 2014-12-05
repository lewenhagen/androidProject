package com.androidproject.androidapplication.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.nfc.Tag;
import android.util.Log;

import java.io.IOException;
import android.database.SQLException;

/**
 * Used to send requests to the database and return the result to the activity whom called it.
 */
public final class DatabaseManager {

    private final String TAG = getClass().getSimpleName();
    //The Android's default system path of your application database.
    private final String DB_PATH = "/data/data/com.androidproject.androidapplication/databases/";

    private final String DB_NAME = "drinkhelper";

    private SQLiteDatabase myDataBase;

    public DatabaseManager() {

    }

    public void openDataBase() throws SQLiteException {
        try {
            String myPath = DB_PATH + DB_NAME;
            myDataBase = SQLiteDatabase.openOrCreateDatabase(myPath, null);
            Log.i(TAG, "Database is open!");
        } catch (android.database.SQLException e) {
            e.printStackTrace();
        }
    }


}
