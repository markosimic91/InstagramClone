package com.example.simic.instagramclone.Profile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.simic.instagramclone.R;
import com.example.simic.instagramclone.Utils.BottomNavigationViewHelper;
import com.example.simic.instagramclone.Utils.FirebaseMethods;
import com.example.simic.instagramclone.Utils.SectionsStatePagerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Simic on 15.3.2018..
 */

public class AccountSettingsActivity extends AppCompatActivity{

    private static final String TAG = "AccountSettingsActivity";
    private static final int ACTIVITY_NUM = 4;

    private Context mContext;
    public SectionsStatePagerAdapter pagerAdapter;

    //regionwidgetsSetup
    @BindView(R.id.container) ViewPager mViewPager;
    @BindView(R.id.relLayout1) RelativeLayout mRelativeLayout;
    //endregion

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountsettings);
        Log.d(TAG, "onCreate: started");
        mContext = AccountSettingsActivity.this;
        ButterKnife.bind(this);

        //regionMethods
        setupSettingsList();
        setupFragments();
        setupBottomNavigationView();
        getIncomingIntent();
        //endregion
        
        
        //region setup the back arrow for navigation back to "ProfileActivity"

        ImageView backArrow = findViewById(R.id.backArrow);
        
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigation back to 'ProfileActivity'");
                finish();
            }
        });
        //endregion
    }
    //regiongetIncomingIntent
    private void getIncomingIntent(){
        Intent intent = getIntent();

        if (intent.hasExtra(getString(R.string.selected_image))
                || intent.hasExtra(getString(R.string.selected_bitmap))) {
            //if there is an imageUrl attached as an extra, then it was chosen from the gallery/photo fragment
            Log.d(TAG, "getIncomingIntent: new incoming imgUrl");
            if (intent.getStringExtra(getString(R.string.return_to_fragment)).equals(getString(R.string.editProfile))) {

                if (intent.hasExtra(getString(R.string.selected_image))) {

                    //set the new profile picture
                    FirebaseMethods firebaseMethods = new FirebaseMethods(this);
                    firebaseMethods.uploadNewPhoto(getString(R.string.profile_photo), null, 0,
                            intent.getStringExtra(getString(R.string.selected_image)), null);

                } else if (intent.hasExtra(getString(R.string.selected_bitmap))) {
                    FirebaseMethods firebaseMethods = new FirebaseMethods(this);
                    firebaseMethods.uploadNewPhoto(getString(R.string.profile_photo), null, 0,
                            null, (Bitmap) intent.getParcelableExtra(getString(R.string.selected_bitmap)));

                }

            }
        }

            if (intent.hasExtra(getString(R.string.calling_activity))) {
                Log.d(TAG, "getIncomingIntent: received incoming intent from " + getString(R.string.profile_activity));
                setViewPager(pagerAdapter.getFragmentNumbers(getString(R.string.editProfile)));
            }
        }

    //endregion

    //region SetupFragments
    private void setupFragments() {

        pagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());

        pagerAdapter.addFragment(new EditProfileFragment(),getString(R.string.editProfile));

        pagerAdapter.addFragment(new SignOutFragment(),getString(R.string.signOut));

    }
    //endregion

    //region SetViewPager
    public void setViewPager(int fragmentNumber){
        mRelativeLayout.setVisibility(View.GONE);
        Log.d(TAG, "setViewPager: navigiation to fragment " + fragmentNumber);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setCurrentItem(fragmentNumber);
    }


    //endregion

    //region SetupSettingsList
    private void setupSettingsList() {
        Log.d(TAG, "setupSettingsList: initializing 'Account Settings");

        ListView listView = findViewById(R.id.lvAccountSettings);

        ArrayList<String> options = new ArrayList<>();
        options.add(getString(R.string.editProfile));
        options.add(getString(R.string.signOut));

        ArrayAdapter adapter = new ArrayAdapter(mContext,android.R.layout.simple_list_item_1,options);

        listView.setAdapter(adapter);
        
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: navigation to fragments " + position);

                setViewPager(position);
            }
        });

    }
    //endregion

    //region bottomNavigationSetup
    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext,this,bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);

    }
    //endregion

}
