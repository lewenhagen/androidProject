package com.androidproject.androidapplication.util;

import java.io.IOException;
import java.lang.reflect.Array;
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
import android.nfc.Tag;
import android.os.Bundle;
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

    public ArrayList<String> performSearch(ArrayList<Integer> searchItems) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        Log.d("Performsearch args", searchItems.toString());
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

        Log.d("Found drinks: ", getNamesById(finalResult).toString());
        return getNamesById(finalResult);

    }

    public ArrayList<String> performTextSearch(String input) {
        ArrayList<String> result = new ArrayList<String>();
        try
        {
            String sql = "SELECT name FROM Drinks WHERE name = %'" + input + "'%";
            Cursor mCur = mDb.rawQuery(sql, null);
            while (mCur.moveToNext()) {
                result.add(mCur.getString(mCur.getColumnIndex("name")));
            }
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, mSQLException.toString());
            throw mSQLException;
        }
        return result;
    }

    public Bundle selectRowFromDrinks(int id, String table) {
        Bundle returnBundle = new Bundle();

        try
        {
            String sql = "SELECT * FROM " + table + " WHERE id = " + id + " LIMIT 1";
            Cursor mCur = mDb.rawQuery(sql, null);
            while (mCur.moveToNext()) {
                returnBundle.putInt("id",mCur.getInt(mCur.getColumnIndex("id")));
                returnBundle.putString("name",mCur.getString(mCur.getColumnIndex("name")));
                returnBundle.putInt("stars",mCur.getInt(mCur.getColumnIndex("starts")));
                returnBundle.putString("image",mCur.getString(mCur.getColumnIndex("image")));
                returnBundle.putString("howtodo",mCur.getString(mCur.getColumnIndex("howtodo")));
            }
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, mSQLException.toString());
            throw mSQLException;
        }


        return returnBundle;
    }

    public ArrayList<Integer> getIdByName(ArrayList<String> args) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        Log.d("GET ID BY NAME ARGS: ", args.toString());
        try
        {   for(String name : args) {
            Log.d("SHIT 1: ", name);
                String sql = "SELECT id FROM Cat_spec WHERE name = '" + name + "'";
                Cursor mCur = mDb.rawQuery(sql, null);
            Log.d("SHIT 2: ", "in raw");
                while (mCur.moveToNext()) {
                    Log.d("INSIDE WHILE: ", ""+ mCur.getInt(mCur.getColumnIndex("id")));
                    result.add(mCur.getInt(mCur.getColumnIndex("id")));
                }
            }
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, mSQLException.toString());
            throw mSQLException;
        }

        return result;
    }

    public ArrayList<String> getNamesById(ArrayList<Integer> args) {
        ArrayList<String> result = new ArrayList<String>();
        Log.d("GET NAMES BY ID ARGS: ", args.toString());
        try
        {   for(int id : args) {
                String sql = "SELECT name FROM Drinks WHERE id = " + id + " LIMIT 1";
                Cursor mCur = mDb.rawQuery(sql, null);
                while (mCur.moveToNext()) {
                    result.add(mCur.getString(mCur.getColumnIndex("name")));
                }
            }
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, mSQLException.toString());
            throw mSQLException;
        }
        Log.d("GET NAMES BY ID RESULT: ", result.toString());
        return result;
    }


}

