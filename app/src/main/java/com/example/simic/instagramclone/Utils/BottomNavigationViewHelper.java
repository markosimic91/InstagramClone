package com.example.simic.instagramclone.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.MenuItem;

import com.example.simic.instagramclone.Home.HomeActivity;
import com.example.simic.instagramclone.Likes.LikesActivity;
import com.example.simic.instagramclone.Profile.ProfileActivity;
import com.example.simic.instagramclone.R;
import com.example.simic.instagramclone.Search.SearchActivity;
import com.example.simic.instagramclone.Share.ShareActivity;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

/**
 * Created by Simic on 12.3.2018..
 */

public class BottomNavigationViewHelper {
    private static final String TAG = "BottomNavigationViewHel";

    public static void setupBottomNavigationView(BottomNavigationViewEx bottomNavigationViewEx){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        
        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.enableItemShiftingMode(false);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.setTextVisibility(false);
    }

    public static void enableNavigation(final Context context, final Activity callingActivity, BottomNavigationViewEx viewEx){
        viewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.ic_house:
                        Intent home = new Intent(context, HomeActivity.class);
                        context.startActivity(home);
                        callingActivity.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        break;
                    case R.id.ic_search:
                        Intent search = new Intent(context, SearchActivity.class);
                        context.startActivity(search);
                        callingActivity.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        break;
                    case R.id.ic_circle:
                        Intent share = new Intent(context, ShareActivity.class);
                        context.startActivity(share);
                        callingActivity.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        break;
                    case R.id.ic_alert:
                        Intent likes = new Intent(context, LikesActivity.class);
                        context.startActivity(likes);
                        callingActivity.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        break;
                    case R.id.ic_android:
                        Intent profile = new Intent(context, ProfileActivity.class);
                        context.startActivity(profile);
                        callingActivity.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        break;
                }

                return false;
            }
        });

    }

}
