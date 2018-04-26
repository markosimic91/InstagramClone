package com.example.simic.instagramclone.Utils;

import android.content.Context;
import android.net.ParseException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.simic.instagramclone.Models.Like;
import com.example.simic.instagramclone.Models.Photo;
import com.example.simic.instagramclone.Models.User;
import com.example.simic.instagramclone.Models.UserAccountSettings;
import com.example.simic.instagramclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Simic on 17.4.2018..
 */

public class ViewPostFragment extends Fragment {

    private static final String TAG = "ViewPostFragment";

    public interface  OnCommentThreadSelectedListener{
        void onCommentThreadSelectedListener(Photo photo);
    }

    OnCommentThreadSelectedListener mOnCommentThreadSelectedListener;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;

    //region Widgets
    @BindView(R.id.post_image) SquareImageView mPostImage;
    @BindView(R.id.bottomNavViewBar) BottomNavigationViewEx mBottomNavigationViewEx;

    @BindView(R.id.tvBackLabel) TextView mBackLabel;
    @BindView(R.id.image_caption) TextView mCaption;
    @BindView(R.id.username) TextView mUserName;
    @BindView(R.id.image_time_posted) TextView mTimestamp;
    @BindView(R.id.image_likes) TextView mLikes;

    @BindView(R.id.backArrow) ImageView mBackArrow;
    @BindView(R.id.ivEllipses) ImageView mEllipses;
    @BindView(R.id.image_heart_red) ImageView mHeartRed;
    @BindView(R.id.image_heart) ImageView mHeartWhite;
    @BindView(R.id.profile_photo) ImageView mProfileImage;

    @BindView(R.id.speech_bubble) ImageView mComments;
    //endregion


    public ViewPostFragment() {
        super();
        setArguments(new Bundle());
    }

    //vars
    private Photo mPhoto;
    private int mActivityNumber = 0;
    private String photoUserName;
    private String profileUrl;
    private UserAccountSettings mUserAccountSettings;
    private android.view.GestureDetector mGestureDetector;
    private Heart mHeart;
    private Boolean mLikedByCurrentUser;
    private StringBuilder mUsers;
    private String mLikesString = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_post,container,false);
        ButterKnife.bind(this,view);

        mHeart = new Heart(mHeartWhite,mHeartRed);
        mGestureDetector = new android.view.GestureDetector(getActivity(),new GestureListener());

        try{
            mPhoto = getPhotoFromBundle();
            UniversalImageLoader.setImage(mPhoto.getImage_path(),mPostImage,null,"");
            mActivityNumber = getActivityNamFromBundle();
            getPhotoDetails();
            getLikeString();

        }catch (NullPointerException e){
            Log.d(TAG, "onCreateView: NullPointerException " + e.getMessage());
        }

        setupFirebaseAuth();
        setupBottomNavigationView();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        
        try{

            mOnCommentThreadSelectedListener = (OnCommentThreadSelectedListener) getActivity();

            
        }catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException " + e.getMessage() );
        }
    }

    //region GetLikeString
    private void getLikeString(){

        Log.d(TAG, "getLikesString: getting likes string");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_photos))
                .child(mPhoto.getPhoto_id())
                .child(getString(R.string.field_likes));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUsers = new StringBuilder();
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    Query query = reference
                            .child(getString(R.string.dbname_users))
                            .orderByChild(getString(R.string.field_user_id))
                            .equalTo(singleSnapshot.getValue(Like.class).getUser_id());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                                Log.d(TAG, "onDataChange: found like: " +
                                        singleSnapshot.getValue(User.class).getUser_name());

                                mUsers.append(singleSnapshot.getValue(User.class).getUser_name());
                                mUsers.append(",");
                            }

                            String[] splitUsers = mUsers.toString().split(",");

                            if(mUsers.toString().contains(mUserAccountSettings.getUser_name() + ",")){
                                mLikedByCurrentUser = true;
                            }else{
                                mLikedByCurrentUser = false;
                            }

                            int length = splitUsers.length;
                            if(length == 1){
                                mLikesString = "Liked by " + splitUsers[0];
                            }
                            else if(length == 2){
                                mLikesString = "Liked by " + splitUsers[0]
                                        + " and " + splitUsers[1];
                            }
                            else if(length == 3){
                                mLikesString = "Liked by " + splitUsers[0]
                                        + ", " + splitUsers[1]
                                        + " and " + splitUsers[2];

                            }
                            else if(length == 4){
                                mLikesString = "Liked by " + splitUsers[0]
                                        + ", " + splitUsers[1]
                                        + ", " + splitUsers[2]
                                        + " and " + splitUsers[3];
                            }
                            else if(length > 4){
                                mLikesString = "Liked by " + splitUsers[0]
                                        + ", " + splitUsers[1]
                                        + ", " + splitUsers[2]
                                        + " and " + (splitUsers.length - 3) + " others";
                            }
                            Log.d(TAG, "onDataChange: likes string: " + mLikesString);
                            setupWidgets();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                if(!dataSnapshot.exists()){
                    mLikesString = "";
                    mLikedByCurrentUser = false;
                    setupWidgets();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    //endregion

    //region class GestureListener
    public class GestureListener extends android.view.GestureDetector.SimpleOnGestureListener{

        @Override
        public boolean onDown(MotionEvent e) {

            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.d(TAG, "onDoubleTap: double tap detected");
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

            Query query = reference
                    .child(getString(R.string.dbname_photos))
                    .child(mPhoto.getPhoto_id())
                    .child(getString(R.string.field_likes));


            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot ) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                    String keyID = singleSnapshot.getKey();

                    //case1 : Then user already like the photo

                    if (mLikedByCurrentUser &&
                            singleSnapshot.getValue(Like.class).getUser_id()
                            .equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){

                        myRef.child(getString(R.string.dbname_photos))
                                .child(mPhoto.getPhoto_id())
                                .child(getString(R.string.field_likes))
                                .child(keyID)
                                .removeValue();

                        myRef.child(getString(R.string.dbname_user_photos))
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(mPhoto.getPhoto_id())
                                .child(getString(R.string.field_likes))
                                .child(keyID)
                                .removeValue();

                        mHeart.toggleLike();
                        getLikeString();

                        }
                        //case2 : The user has not like the photo

                        else if (!mLikedByCurrentUser){
                            //add new like
                            addNewLike();
                            break;
                        }

                    }
                    if (!dataSnapshot.exists()){
                        //add new like
                        addNewLike();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            return true;
        }

    }
    //endregion

    //region Add New Like
    private void addNewLike(){
        Log.d(TAG, "addNewLike: adding new like");

        String newLikeID = myRef.push().getKey();
        Like like = new Like();
        like.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());

        myRef.child(getString(R.string.dbname_photos))
                .child(mPhoto.getPhoto_id())
                .child(getString(R.string.field_likes))
                .child(newLikeID)
                .setValue(like);

        myRef.child(getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(mPhoto.getPhoto_id())
                .child(getString(R.string.field_likes))
                .child(newLikeID)
                .setValue(like);
        mHeart.toggleLike();
        getLikeString();

    }
    //endregion

    //region GetPhotoDetails
    private void getPhotoDetails(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_user_account_settings))
                .orderByChild(getString(R.string.field_user_id))
                .equalTo(mPhoto.getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    mUserAccountSettings = singleSnapshot.getValue(UserAccountSettings.class);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled");
            }
        });
    }
    //endregion

    //region SetupWidgets
    private void setupWidgets(){
        String timestampDiff = getTimestampDifference();

        if (!timestampDiff.equals("0")){
            mTimestamp.setText(timestampDiff + " DAYS AGO");
        }else {
            mTimestamp.setText("TODAY");
        }

        UniversalImageLoader.setImage(mUserAccountSettings.getProfile_photo(),mProfileImage,null,"");
        mUserName.setText(mUserAccountSettings.getUser_name());

        mLikes.setText(mLikesString);
        mCaption.setText(mPhoto.getCaption());

        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        mComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mOnCommentThreadSelectedListener.onCommentThreadSelectedListener(mPhoto);

            }
        });

        if (mLikedByCurrentUser){
            mHeartWhite.setVisibility(View.GONE);
            mHeartRed.setVisibility(View.VISIBLE);
            mHeartRed.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d(TAG, "onTouch: red heart touch detected");
                    return mGestureDetector.onTouchEvent(event);
                }
            });
        }else {
            mHeartWhite.setVisibility(View.VISIBLE);
            mHeartRed.setVisibility(View.GONE);
            mHeartWhite.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d(TAG, "onTouch: white heart touch detected");
                    return mGestureDetector.onTouchEvent(event);
                }
            });
        }


    }
    //endregion

    //region Returns a string representing the number of days ago the post was posted
    private String getTimestampDifference(){
        Log.d(TAG, "getTimestampDifference: getting timestamp difference.");

        String difference = "";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm:ss'Z'", Locale.ITALIAN);
        sdf.setTimeZone(TimeZone.getTimeZone("Europe/Zagreb"));

        Date today = (Date) c.getTime();
        sdf.format(today);
        Date timestamp;

        final String photoTimestamp = mPhoto.getDate_created();

        try {

            timestamp = sdf.parse(photoTimestamp);
            difference = String.valueOf(Math.round((today.getTime() - timestamp.getTime()) / 1000 / 60 / 60 / 24 ));


        }catch (ParseException e){
            Log.e(TAG, "getTimestampDifference: ParseException " + e.getMessage() );

        } catch (java.text.ParseException e) {
            e.printStackTrace();
            difference = "0";
        }
        return difference;
    }
    //endregion

    //region Retrieve the activity number from the incoming bundle from profileActivity interface
    private int getActivityNamFromBundle() {
        Log.d(TAG, "getActivityNamFromBundle: arguments: " + getArguments());

        Bundle bundle = this.getArguments();
        if (bundle != null){
            return bundle.getInt(getString(R.string.activity_number));
        }else {
            return 0;
        }
    }
    //endregion

    //region Retrieve the photo from the incoming bundle from profileActivity interface
    private Photo getPhotoFromBundle(){
        Log.d(TAG, "getPhotoFromBundle: arguments: " + getArguments());

        Bundle bundle = this.getArguments();
        if (bundle != null){
            return bundle.getParcelable(getString(R.string.photo));
        }else {
            return null;
        }
    }
    //endregion

    //region BottomNavigationSetup
    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");

        BottomNavigationViewHelper.setupBottomNavigationView(mBottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(getActivity(),getActivity(),mBottomNavigationViewEx);
        Menu menu = mBottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(mActivityNumber);
        menuItem.setChecked(true);

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
