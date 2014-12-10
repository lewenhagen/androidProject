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
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteException;
import android.graphics.drawable.ColorDrawable;
import android.nfc.Tag;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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


public class MainActivity extends Activity {

    private final String TAG = MainActivity.class.getSimpleName();
    private ArrayList<String> allSelected = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        TabHost mTabHost = (TabHost) findViewById(R.id.tabHost);
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

        mTabHost.setCurrentTab(1);

        initFavorites();
        initSearchView();

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
        String[] values = new String[]{
                "Drink1",
                "Drink2",
                "Drink3",
                "Drink4"
        };

        ArrayAdapter<String> adapt = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1 ,values);
        favorites.setAdapter(adapt);

        favorites.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition     = position;

                // ListView Clicked item value
                String  itemValue    = (String) favorites.getItemAtPosition(position);

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
        ArrayList<Integer> temp2 = new ArrayList<Integer>(5);
        temp2.add(1);temp2.add(1);temp2.add(2);temp2.add(3);temp2.add(4);
        mDbHelper.performSearch(temp2);
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
        ExpandableListAdapter listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        searchListView.setAdapter(listAdapter);
        searchListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Log.d("ListView", "Clicked: " + ((TextView) v.findViewById(R.id.lblListItem)).getText());

                toggleSelected(((TextView) v.findViewById(R.id.lblListItem)));
                return false;
            }
        });

        Button searchButton = (Button) findViewById(R.id.doSearch);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Perform search.");
                doSearch();
            }
        });
    }

    private void toggleSelected(TextView v) {

        if(!v.isSelected()) {
            v.setTextColor(getResources().getColor(R.color.green));
            v.setSelected(true);
            allSelected.add(v.getText().toString());
        }
        else{
            v.setTextColor(getResources().getColor(R.color.defaultTextcolor));
            v.setSelected(false);
            allSelected.remove(v.getText().toString());
        }
    }

    private void reset() {
        ExpandableListView searchListView = (ExpandableListView) findViewById(R.id.search_categories);
        for(int i = 0; i < searchListView.getCount(); i++) {
            if(searchListView.getChildAt(i).isSelected()) {
                searchListView.getChildAt(i).setSelected(false);
            }
        }
    }

    private void doSearch() {
        DatabaseManager mDbHelper = new DatabaseManager(getApplicationContext());
        mDbHelper.createDatabase();
        mDbHelper.open();
        EditText searchInput = (EditText)findViewById(R.id.search_input_field);
        String currInput = searchInput.getText().toString();
        ArrayList<String> resultOfSearch = new ArrayList<String>();

        if(currInput != null && !currInput.isEmpty()) {
            resultOfSearch = mDbHelper.performTextSearch(currInput);
        }
        else {
            resultOfSearch = mDbHelper.performSearch(mDbHelper.getIdByName(allSelected));
            reset();
            allSelected.clear();
        }

        Intent startResultView = new Intent(this, ResultView.class);
        Bundle args = new Bundle();
        args.putStringArrayList("result",resultOfSearch);
        startResultView.putExtras(args);
        startActivity(startResultView);
    }
}
