package com.androidproject.androidapplication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteException;
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
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.androidproject.androidapplication.util.DatabaseManager;


public class MainActivity extends Activity {


    DatabaseManager dbm = new DatabaseManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
       /* mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(1); */

        HomeFragment test = new HomeFragment();

        TabHost mTabHost = (TabHost) findViewById(R.id.tabHost);
        mTabHost.setup();
        TabHost.TabSpec mTabContent = mTabHost.newTabSpec("Search");
        mTabContent.setContent(R.id.tab1);
        mTabContent.setIndicator("Search");
        mTabHost.addTab(mTabContent);

        mTabContent = mTabHost.newTabSpec("Home");
        mTabContent.setContent(R.id.tab2);
        mTabContent.setIndicator("Home");
        mTabHost.addTab(mTabContent);

        mTabContent = mTabHost.newTabSpec("Favorites");
        mTabContent.setContent(R.id.favorites_tab);
        mTabContent.setIndicator("Favorites");
        mTabHost.addTab(mTabContent);

        mTabHost.setCurrentTab(1);

        initFavorites();
        dbm.openDataBase();

        String appPath = getApplicationContext().getFilesDir().getAbsolutePath();

        Log.d("DIRECTORY",appPath);


        //getFragmentManager().beginTransaction().add(R.id.search_pager, new HomeFragment());
        /* getFragmentManager().beginTransaction().add(R.id.search_pager, new SearchFragment());
        getFragmentManager().beginTransaction().add(R.id.favorites_pager, new FavoritesFragment()); */
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
        final ListView favorites = (ListView) findViewById(R.id.listView);
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

}
