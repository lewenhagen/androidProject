package com.androidproject.androidapplication;

import com.androidproject.androidapplication.util.DatabaseManager;
import com.androidproject.androidapplication.util.ExpandableListAdapter;
import com.androidproject.androidapplication.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class DetailedView extends Activity {

    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private static final boolean TOGGLE_ON_CLICK = true;
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;
    private String newMail = "";
    private SystemUiHider mSystemUiHider;
    private String recipeTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detailed_view);

        final View contentView = findViewById(R.id.fullscreen_content);
        recipeTitle = getIntent().getAction();

        Log.d("DetailedView", getIntent().getAction());
        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {

                    @Override
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                    public void onVisibilityChange(boolean visible) {

                        if (visible && AUTO_HIDE) {
                            // Schedule a hide().
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });

        // Set up the user interaction to manually show or hide the system UI.
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                }
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        //findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);

        ActionBar actionBar = getActionBar();
        actionBar.hide();

        setupDetailedView();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }


    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private void setupDetailedView() {
        final DatabaseManager mDbHelper = new DatabaseManager(getApplicationContext());
        mDbHelper.createDatabase();
        mDbHelper.open();
        final Bundle drinkInfo = mDbHelper.getDrink(this.recipeTitle);
        final CheckBox chbox = ((CheckBox) findViewById(R.id.detailedview_addtofavorites));
        final RatingBar rating = ((RatingBar) findViewById(R.id.detailedview_ratingBar));


        if(mDbHelper.isItFavorite(drinkInfo.getString("name")) == 1) {
            chbox.setChecked(true);
        }

        ((TextView) findViewById(R.id.recipe_title)).setText(drinkInfo.getString("name"));
        rating.setNumStars(5);
        rating.setRating(drinkInfo.getInt("stars"));

        rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            public void onRatingChanged(RatingBar ratingBar, float ratings,  boolean fromUser) {

                Log.d("NEW STARS: ", rating.getRating() + "");
                mDbHelper.setStars(drinkInfo.getString("name"), rating.getRating());
                Toast.makeText(getApplicationContext(), "New rating: " + rating.getRating() + " stars!", Toast.LENGTH_SHORT).show();
            }

        });

        chbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chbox.isChecked()) {
                    Log.d("Checkbox", "CHECKED! " + drinkInfo.getString("name"));
                    mDbHelper.addToFav(drinkInfo.getString("name"));
                    Toast.makeText(getApplicationContext(), drinkInfo.getString("name") + " added to favorites!", Toast.LENGTH_SHORT).show();

                } else {
                    Log.d("Checkbox", "UNCHECKED!");
                    mDbHelper.removeFromFav(drinkInfo.getString("name"));
                    Toast.makeText(getApplicationContext(), drinkInfo.getString("name") + " removed to favorites!", Toast.LENGTH_SHORT).show();
                }

            }
        });


		ExpandableListView specListView = (ExpandableListView) findViewById(R.id.ExpandableRecipieSpecs);
		List<String> listDataHeader = new ArrayList<String>();
		HashMap<String, List<String>> listDataChild = new HashMap<String, List<String>>();
		List<String> ingredientList = new ArrayList<String>();
		List<String> howto = new ArrayList<String>();

		for(int i = 0; i < drinkInfo.getBundle("Ingredients").size(); i++){
			String pos = ""+i;
			ingredientList.add(drinkInfo.getBundle("Ingredients").getStringArrayList(""+pos).get(2)+" "+drinkInfo.getBundle("Ingredients").getStringArrayList(""+pos).get(1));
		}
		howto.add(drinkInfo.getString("howtodo"));

		listDataHeader.add("Ingredients");
		listDataChild.put("Ingredients", ingredientList);
		listDataHeader.add("Description");
		listDataChild.put("Description", howto);

		final ExpandableListAdapter listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
		specListView.setAdapter(listAdapter);

        ((Button) findViewById(R.id.forwardButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_SUBJECT, "Try this drink!");
                email.putExtra(Intent.EXTRA_TEXT, drinkInfo.getString("name") + "\nI gave this: " + rating.getRating() + " stars!" + "\n\n" + drinkInfo.getString("howtodo"));
                email.setType("message/rfc822");
                startActivity(Intent.createChooser(email, "Choose an Email client :"));
            }
        });

    }
}
