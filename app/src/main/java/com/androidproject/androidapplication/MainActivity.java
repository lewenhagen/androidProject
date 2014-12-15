package com.androidproject.androidapplication;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteException;
import android.graphics.drawable.ColorDrawable;
import android.nfc.Tag;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.androidproject.androidapplication.util.DatabaseManager;
import com.androidproject.androidapplication.util.ExpandableListAdapter;

import org.w3c.dom.Text;


public class MainActivity extends Activity {

    private final String TAG = MainActivity.class.getSimpleName();
    private ArrayList<String> allSelected = new ArrayList<String>();
    private int currentTab = 1;
    private TabHost mTabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTabHost = (TabHost) findViewById(R.id.tabHost);
        mTabHost.setup();
        TabHost.TabSpec mTabContent = mTabHost.newTabSpec("Search");
        mTabContent.setContent(R.id.searchTab);
        mTabContent.setIndicator("Search");
        mTabHost.addTab(mTabContent);

        mTabContent = mTabHost.newTabSpec("Home");
        mTabContent.setContent(R.id.homeTab);
        mTabContent.setIndicator("Home");
        mTabHost.addTab(mTabContent);

        mTabContent = mTabHost.newTabSpec("Favorites");
        mTabContent.setContent(R.id.favoritesTab);
        mTabContent.setIndicator("Favorites");
        mTabHost.addTab(mTabContent);

        mTabHost.setCurrentTab(currentTab);

        initFavorites();
        initSearchView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        this.onCreate(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initFavorites() {
        final ListView favorites = (ListView) findViewById(R.id.favorites_listview);
        DatabaseManager mDbHelper = new DatabaseManager(getApplicationContext());
        mDbHelper.createDatabase();
        mDbHelper.open();
        List<String> values = mDbHelper.getAllFavorites();
        mDbHelper.close();

        ArrayAdapter<String> adapt = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1 ,values);
        favorites.setAdapter(adapt);

        favorites.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition = position;

                // ListView Clicked item value
                String itemValue = (String) favorites.getItemAtPosition(position);

                // Show Alert
                Intent openDetailedView = new Intent(MainActivity.this.getApplicationContext(), DetailedView.class);
                openDetailedView.setAction(itemValue);
                MainActivity.this.startActivity(openDetailedView);
            }

        });
    }

    private void initSearchView() {
        DatabaseManager mDbHelper = new DatabaseManager(getApplicationContext());
        mDbHelper.createDatabase();
        mDbHelper.open();
        ArrayList<String> result = mDbHelper.getCategories();
        ExpandableListView searchListView = (ExpandableListView) findViewById(R.id.search_categories);
        List<String> listDataHeader = new ArrayList<String>();
        HashMap<String, List<String>> listDataChild = new HashMap<String, List<String>>();
        ArrayList<List> content = new ArrayList<List>();

        for(int i=0;i<result.size();i++) {
            ArrayList<String> temp = mDbHelper.getLiquidsByCategoryId(i+1);
            listDataChild.put(result.get(i), temp);
            listDataHeader.add(result.get(i));
        }
        mDbHelper.close();
        final ExpandableListAdapter listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        searchListView.setAdapter(listAdapter);
        searchListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Log.d("ListView", "Clicked: " + ((TextView) v.findViewById(R.id.lblListItem)).getText());

                toggleSelected(((TextView) v.findViewById(R.id.lblListItem)));
                return false;
            }
        });

        Button searchButton = (Button) findViewById(R.id.do_search);
        Button resetButton = (Button) findViewById(R.id.reset_search);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Perform search.");
                doSearch();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Reset search");
                reset();
            }
        });

    }

    private void toggleSelected(TextView v) {

        if(!v.isSelected()) {
            v.setBackgroundColor(getResources().getColor(R.color.green));
            v.setSelected(true);
            allSelected.add(v.getText().toString());
            Toast.makeText(getApplicationContext(), v.getText() + " added", Toast.LENGTH_SHORT).show();
        }
        else{
            v.setBackgroundColor(getResources().getColor(R.color.white));
            v.setSelected(false);
            allSelected.remove(v.getText().toString());
            Toast.makeText(getApplicationContext(), v.getText() + " removed", Toast.LENGTH_SHORT).show();
        }
        Log.d("CONTENT OF ALLSELECTED: ", allSelected.toString());
    }

    private void reset() {
        this.recreate();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("tab", mTabHost.getCurrentTab());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentTab = savedInstanceState.getInt("tab");
    }

    private void doSearch() {
        DatabaseManager mDbHelper = new DatabaseManager(getApplicationContext());
        mDbHelper.createDatabase();
        mDbHelper.open();
        EditText searchInput = (EditText)findViewById(R.id.search_input);
        String currInput = searchInput.getText().toString();
        ArrayList<String> resultOfSearch = new ArrayList<String>();

        if(currInput != null && !currInput.isEmpty()) {
            resultOfSearch = mDbHelper.performTextSearch(currInput);
        }
        else {
            resultOfSearch = mDbHelper.performSearch(mDbHelper.getIdByName(allSelected));
        }
        mDbHelper.close();

        Intent startResultView = new Intent(this, ResultView.class);
        Bundle args = new Bundle();
        args.putStringArrayList("result",resultOfSearch);
        startResultView.putExtras(args);
        startActivity(startResultView);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){

        int action = MotionEventCompat.getActionMasked(event);

        switch(action) {
            case (MotionEvent.ACTION_DOWN) :
                Log.d(TAG,"Action was DOWN");
                return true;
            case (MotionEvent.ACTION_MOVE) :
                Log.d(TAG,"Action was MOVE");
                return true;
            case (MotionEvent.ACTION_UP) :
                Log.d(TAG,"Action was UP");
                return true;
            case (MotionEvent.ACTION_CANCEL) :
                Log.d(TAG,"Action was CANCEL");
                return true;
            case (MotionEvent.ACTION_OUTSIDE) :
                Log.d(TAG,"Movement occurred outside bounds " +
                        "of current screen element");
                return true;
            default :
                return super.onTouchEvent(event);
        }
    }


}
