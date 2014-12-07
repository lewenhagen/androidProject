package com.androidproject.androidapplication.util;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

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
}