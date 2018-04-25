package com.example.simic.instagramclone.Share;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.simic.instagramclone.R;
import com.example.simic.instagramclone.Utils.BottomNavigationViewHelper;
import com.example.simic.instagramclone.Utils.Permissions;
import com.example.simic.instagramclone.Utils.SectionsPagerAdapter;
import com.example.simic.instagramclone.Utils.SectionsStatePagerAdapter;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Simic on 13.3.2018..
 */

public class ShareActivity extends AppCompatActivity{

    private static final String TAG = "ShareActivity";

    //constants
    private static final int ACTIVITY_NUM = 2;
    private static final int VERIFY_PERMISSIONS_REQUEST = 1;


    private Context mContext = ShareActivity.this;

    @BindView(R.id.container) ViewPager mViewPager;
    @BindView(R.id.tabsBottom) TabLayout tabLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        Log.d(TAG, "onCreate: started");
        ButterKnife.bind(this);

            if (checkPermissionsArray(Permissions.PERMISSIONS)){

                    setUpViewPager();

            }else {
                verifyPermissions(Permissions.PERMISSIONS);
            }

        //regionMethods

        //endregion

    }
    //region return the current tab number
    //0 = GalleryFragment
    //1 = PhotoFragment
    public int getCurrentTabNumber(){
        return  mViewPager.getCurrentItem();
    }
    //endregion

    @SuppressLint("WrongConstant")
    public int getTask(){
        Log.d(TAG, "getTask  " + getIntent().getFlags());
        return getIntent().getFlags();
    }

    //region Setup View Pager For Gallery/Photo Fragments
    private void setUpViewPager(){
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new GalleryFragment());
        adapter.addFragment(new PhotoFragment());
        mViewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setText(getString(R.string.gallery));
        tabLayout.getTabAt(1).setText(getString(R.string.photo));

    }
    //endregion
    //regionPERMISSIONS
    //region verify all the permissions passed to the array
    public void verifyPermissions(String[] permissions) {
        Log.d(TAG, "verifyPermissions: verify Permissions ");

        ActivityCompat.requestPermissions(
                ShareActivity.this,
                permissions,
                VERIFY_PERMISSIONS_REQUEST
        );
    }
    //endregion


    //region Check an array of PERMISSIONS
    private boolean checkPermissionsArray(String[] permissions) {
        Log.d(TAG, "checkPermissionsArray: checking permissions array");

        for (int i = 0; i< permissions.length; i++){
            String check = permissions[i];
            if (!checkPermissions(check)){
                return false;
            }
        }
        return true;
    }
    //endregion

    //region Check a single permission is it has been verified
    public boolean checkPermissions(String permission){
        Log.d(TAG, "checkPermissions: checking permission : " + permission);
        int permissionRequest = ActivityCompat.checkSelfPermission(ShareActivity.this,permission);

        if (permissionRequest != PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "checkPermissions: \n Permission was not granted for: " + permission);
            return false;
        }else {
            Log.d(TAG, "checkPermissions: \n Permission was granted for: " + permission);
            return true;
        }
    }
    //endregion

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
