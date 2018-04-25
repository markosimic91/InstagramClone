package com.example.simic.instagramclone.Share;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.simic.instagramclone.R;
import com.example.simic.instagramclone.Utils.FirebaseMethods;
import com.example.simic.instagramclone.Utils.UniversalImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Simic on 2.4.2018..
 */

public class NextActivity extends AppCompatActivity{
    private static final String TAG = "NextActivity";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;

    @BindView(R.id.ivBackArrow) ImageView ivBackArrow;
    @BindView(R.id.tvShare) TextView tvShare;
    @BindView(R.id.imageShare) ImageView imageShare;
    @BindView(R.id.caption) EditText mCaption;

    //vars
    private String mAppend = "file:/";
    private int imageCount = 0;
    private String imgUrl;
    private Intent intent;
    private Bitmap bitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);
        ButterKnife.bind(this);
        setupFirebaseAuth();
        mFirebaseMethods = new FirebaseMethods(NextActivity.this);
        ivBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing share activity");
                finish();
            }
        });

        tvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigation to the final share screen");
                //upload the image to firebase
                Toast.makeText(NextActivity.this, "Attempting to upload new photo!", Toast.LENGTH_SHORT).show();
                String description = mCaption.getText().toString();

                if (intent.hasExtra(getString(R.string.selected_image))){
                    imgUrl = intent.getStringExtra(getString(R.string.selected_image));
                    mFirebaseMethods.uploadNewPhoto(getString(R.string.new_photo),description,imageCount,imgUrl,null);


                }else if (intent.hasExtra(getString(R.string.selected_bitmap))) {
                    bitmap = intent.getParcelableExtra(getString(R.string.selected_bitmap));
                    mFirebaseMethods.uploadNewPhoto(getString(R.string.new_photo),description,imageCount,null,bitmap);


                }

            }
        });
        setImage();
    }

    private void someMethod(){
        // Step 1: Create a data model for Photos;

        // Step 2: Add properties to the Photo Object (caption, date,imageUri, photo_id,tags,user_id

        // Step 3: Count the number of photos the user already has

        // Step 4: Upload the photo to Firebase Storage and insert two new nodes in the firebase
            // a) insert into 'photo_node'
            // b) insert into 'user_photo' node



    }

    //region gets the image url from the incoming intent and displays the chosen image
    private void setImage(){
        intent = getIntent();
        imgUrl = intent.getStringExtra(getString(R.string.selected_image));

        if (intent.hasExtra(getString(R.string.selected_image))){
            imgUrl = intent.getStringExtra(getString(R.string.selected_image));
            Log.d(TAG, "setImage: got new image url " + imgUrl);
            UniversalImageLoader.setImage(imgUrl,imageShare,null,mAppend);
        }else if (intent.hasExtra(getString(R.string.selected_bitmap))){
            bitmap = intent.getParcelableExtra(getString(R.string.selected_bitmap));
            Log.d(TAG, "setImage: got new bitmap");
            imageShare.setImageBitmap(bitmap);
        }

    }
    //endregion

    //regionFirebase

    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: setting up firebaseAuth");
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null){
                    Log.d(TAG, "onAuthStateChanged: signed_in:" + user.getUid());
                }else {
                    Log.d(TAG, "onAuthStateChanged: signed_out!");
                }
            }
        };

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                imageCount = mFirebaseMethods.getImageCount(dataSnapshot);
                Log.d(TAG, "onDataChange: image count " + imageCount);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }

    //endregion
}
