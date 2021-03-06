package com.androidproject.androidapplication.util;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
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
            String sql ="SELECT * FROM ingredientCategories";
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
            String sql ="SELECT * FROM Ingredient WHERE catagory_id = " + id;
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
                String sql = "SELECT recipie_id FROM RecepieIngredientConnection WHERE ingredient_id = " + searchItems.get(i);
                Cursor mCur = mDb.rawQuery(sql, null);
                while (mCur.moveToNext()) {
                    result.add(mCur.getInt(mCur.getColumnIndex("recipie_id")));
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

        if(input.charAt(input.length()-1) == ' ') {
            input = input.substring(0, input.length()-1);
        }

        try
        {
            String sql = "SELECT name FROM Recepie WHERE name LIKE '%" + input + "%'";

            Cursor mCur = mDb.rawQuery(sql, null);
            Log.d(TAG,"Result from query: " + mCur.toString());
            while (mCur.moveToNext()) {
                result.add(mCur.getString(mCur.getColumnIndex("name")));
                Log.d(TAG,"Query found: " + mCur.getString(mCur.getColumnIndex("name")));
            }
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, mSQLException.toString());
            throw mSQLException;
        }


        return result;
    }

    public Bundle getDrink(String name) {
        Bundle returnBundle = new Bundle();
        Log.d("Drink: ", name);
        try
        {
            String sql = "SELECT * FROM Recepie WHERE name = '" + name + "'";
            Cursor mCur = mDb.rawQuery(sql, null);
            Log.d("MCUR",mCur.toString());
            while (mCur.moveToNext()) {

                returnBundle.putInt("id", mCur.getInt(mCur.getColumnIndex("id")));
                returnBundle.putString("name", mCur.getString(mCur.getColumnIndex("name")));
                returnBundle.putInt("stars", mCur.getInt(mCur.getColumnIndex("stars")));
                returnBundle.putString("image", mCur.getString(mCur.getColumnIndex("image")));
                returnBundle.putString("howtodo", mCur.getString(mCur.getColumnIndex("howtodo")));

				String recepieID = mCur.getString(mCur.getColumnIndex("id"));
				String sql2 = "SELECT ingredient_id AS id, amount, name FROM RecepieIngredientConnection INNER JOIN Ingredient ON ingredient_id = Ingredient.id WHERE RecepieIngredientConnection.recipie_id = '"+recepieID+"'";
				Cursor mCur2 = mDb.rawQuery(sql2, null);
				Bundle ingredientBundle = new Bundle();
				Log.d("MCUR",mCur2.toString());
				int pos = 0;
				while (mCur2.moveToNext()) {
					ArrayList<String> Ingredient = new ArrayList<String>();
					Ingredient.add(mCur2.getString(mCur2.getColumnIndex("id")));
					Ingredient.add(mCur2.getString(mCur2.getColumnIndex("name")));
					Ingredient.add(mCur2.getString(mCur2.getColumnIndex("amount")));
					ingredientBundle.putStringArrayList(""+pos, Ingredient);
					pos++;
				}
				returnBundle.putBundle("Ingredients", ingredientBundle);
                Log.d("RETURNBUNDLE: ", returnBundle.toString());
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
        try
        {   for(String name : args) {
                String sql = "SELECT id FROM Ingredient WHERE name = '" + name + "'";
                Cursor mCur = mDb.rawQuery(sql, null);
                while (mCur.moveToNext()) {
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
        try
        {   for(int id : args) {
                String sql = "SELECT name FROM Recepie WHERE id = " + id + " LIMIT 1";
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

        return result;
    }

    public ArrayList<String> getAllFavorites() {
        ArrayList<String> result = new ArrayList<String>();
        try
        {
            String sql = "SELECT name FROM Recepie WHERE isFavorite = 1";
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

    public void removeFromFav(String drinkToRemove) {
        try
        {
            Log.d("Removes from DB", drinkToRemove);
            String sql = "UPDATE Recepie SET isFavorite=0 WHERE name = '" + drinkToRemove + "'";
            mDb.execSQL(sql);
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, mSQLException.toString());
            throw mSQLException;
        }
    }

    public void addToFav(String drinkToAdd) {
        try
        {
            Log.d("Adds to DB", drinkToAdd);
            String sql = "UPDATE Recepie SET isFavorite=1 WHERE name = '" + drinkToAdd + "'";
            mDb.execSQL(sql);
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, mSQLException.toString());
            throw mSQLException;
        }
    }

    public int isItFavorite(String checkThis) {
        int isFav = -1;

        try
        {
            String sql = "SELECT isFavorite FROM Recepie WHERE name = '" + checkThis + "'";
            Cursor mCur = mDb.rawQuery(sql, null);
            while (mCur.moveToNext()) {
                isFav = mCur.getInt(mCur.getColumnIndex("isFavorite"));
            }
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, mSQLException.toString());
            throw mSQLException;
        }

        return isFav;
    }

    public void setStars(String drinkName, float nrOfStars) {

        try
        {
            String sql = "UPDATE Recepie SET stars=" + nrOfStars + " WHERE name = '" + drinkName + "'";
            mDb.execSQL(sql);
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, mSQLException.toString());
            throw mSQLException;
        }
    }
}

