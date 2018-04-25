package com.example.simic.instagramclone.Profile;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.simic.instagramclone.Models.Photo;
import com.example.simic.instagramclone.R;
import com.example.simic.instagramclone.Utils.ViewPostFragment;

/**
 * Created by Simic on 13.3.2018..
 */

public class ProfileActivity extends AppCompatActivity implements ProfileFragment.OnGridImageSelectedListener{
    
    private static final String TAG = "ProfileActivity";

    @Override
    public void onGridImageSelected(Photo photo, int activityNumber) {

        Log.d(TAG, "onGridImageSelected: selected an image gridview " + photo.toString());

        ViewPostFragment fragment = new ViewPostFragment();

        Bundle args = new Bundle();

        args.putParcelable(getString(R.string.photo),photo);
        args.putInt(getString(R.string.activity_number),activityNumber);
        fragment.setArguments(args);

        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container,fragment);
        transaction.addToBackStack(getString(R.string.view_post_fragment));
        transaction.commit();

    }

    private static final int ACTIVITY_NUM = 4;
    private static final int NUM_GRID_COLUMNS = 3;
    private Context mContext = ProfileActivity.this;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Log.d(TAG, "onCreate: started");
        //ButterKnife.bind(this);
        //mProgressBar.setVisibility(View.GONE);
        init();

        //regionMethods

        //endregion

    }

    private void init(){
        Log.d(TAG, "init: inflating " + getString(R.string.profile_fragment));

        ProfileFragment fragment = new ProfileFragment();

        android.support.v4.app.FragmentTransaction transaction = ProfileActivity.this.getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.container,fragment);

        transaction.addToBackStack(getString(R.string.profile_fragment));
        transaction.commit();

    }

}
