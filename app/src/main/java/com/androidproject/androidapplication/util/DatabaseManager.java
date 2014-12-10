package com.androidproject.androidapplication.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DatabaseManager
{
    protected static final String TAG = "DatabaseManager";

    private final Context mContext;
    private SQLiteDatabase mDb;
    private DataBaseHelper mDbHelper;

    public DatabaseManager(Context context)
    {
        this.mContext = context;
        mDbHelper = new DataBaseHelper(mContext);
    }

    public DatabaseManager createDatabase() throws SQLException
    {
        try
        {
            mDbHelper.createDataBase();
        }
        catch (IOException mIOException)
        {
            Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase");
            throw new Error("UnableToCreateDatabase");
        }
        return this;
    }

    public DatabaseManager open() throws SQLException
    {
        try
        {
            mDbHelper.openDataBase();
            mDbHelper.close();
            mDb = mDbHelper.getReadableDatabase();
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, "open >>" + mSQLException.toString());
            throw mSQLException;
        }
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public ArrayList<String> getCategories()
    {
        try
        {
            String sql ="SELECT * FROM Categories";
            ArrayList<String> result = new ArrayList<String>();

            Cursor mCur = mDb.rawQuery(sql, null);
            while(mCur.moveToNext()){
                result.add(mCur.getString(mCur.getColumnIndex("catName")));
            }
            return result;
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, mSQLException.toString());
            throw mSQLException;
        }
    }

    public ArrayList<String> getLiquidsByCategoryId(int id) {
        try
        {
            String sql ="SELECT * FROM Cat_spec WHERE c_id = " + id;
            ArrayList<String> result = new ArrayList<String>();

            Cursor mCur = mDb.rawQuery(sql, null);
            while(mCur.moveToNext()){
                result.add(mCur.getString(mCur.getColumnIndex("name")));
            }
            return result;
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, mSQLException.toString());
            throw mSQLException;
        }
    }

    public ArrayList<Integer> performSearch(ArrayList<Integer> searchItems) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        try
        {
            for(int i = 0; i < searchItems.size(); i++) {
                String sql = "SELECT drink_id FROM Connection WHERE spec_id = " + searchItems.get(i);
                Cursor mCur = mDb.rawQuery(sql, null);
                while (mCur.moveToNext()) {
                    result.add(mCur.getInt(mCur.getColumnIndex("drink_id")));
                }
            }
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, mSQLException.toString());
            throw mSQLException;
        }

        ArrayList<Integer> finalResult = new ArrayList<Integer>();
        ArrayList<Integer> theKeys = new ArrayList<Integer>();
        JSONArray freq = new JSONArray();

        Set<Integer> unique = new HashSet<Integer>(result);

        for(int key : unique) {
            try{
                freq.put(key, Collections.frequency(result, key));
                theKeys.add(key);
                Log.d("JSON :  ", key + ": " + Collections.frequency(result, key));
            }
            catch(JSONException e) {
                Log.d("ERROR: ", e.toString());
            }
        }

        int holder = -1;

        for(int key : theKeys) {
            Log.d("CURRENT KEY: ", " " +key);
            try{
                if((Integer)freq.get(key) > holder) {
                    Log.d("CURRENT KEY 2: ", " " + freq.get(key));
                    holder = (Integer)freq.get(key);
                    finalResult.add(0, key);
                }
                else {
                    finalResult.add(key);
                }
            }
            catch(JSONException e) {
                Log.d("ERROR IN KEYS STUFF: ", e.toString());
            }
        }
        Log.d("EEEEEEEEEEEEEEEEEEEEEEEE: ", finalResult.toString());
        Log.d("DKGLJDSLKJFK", result.toString());
        return finalResult;

    }

    public ArrayList<Integer> performTextSearch(String input) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        try
        {
            String sql = "SELECT id FROM Drinks WHERE name = %" + input + "%";
            Cursor mCur = mDb.rawQuery(sql, null);
            while (mCur.moveToNext()) {
                result.add(mCur.getInt(mCur.getColumnIndex("id")));
            }
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, mSQLException.toString());
            throw mSQLException;
        }
        return result;
    }
}

