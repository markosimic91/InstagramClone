package com.example.simic.instagramclone.Utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.simic.instagramclone.Home.HomeActivity;
import com.example.simic.instagramclone.Models.Photo;
import com.example.simic.instagramclone.Models.User;
import com.example.simic.instagramclone.Models.UserAccountSettings;
import com.example.simic.instagramclone.Models.UserSettings;
import com.example.simic.instagramclone.Profile.AccountSettingsActivity;
import com.example.simic.instagramclone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ForkJoinPool;

/**
 * Created by Simic on 20.3.2018..
 */

public class FirebaseMethods {

    private static final String TAG = "FirebaseMethods";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String userId;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private StorageReference mStorageReference;

    //vars
    private Context mContext;
    private double mPhotoUploadProgress = 0;

    public FirebaseMethods(Context context){
        mContext = context;
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();

        if (mAuth.getCurrentUser() != null){

            userId = mAuth.getCurrentUser().getUid();

        }
    }
    //regionUpdate 'user_account_settings' node for the current user
    public void updateUserAccountSettings(String displayName,String website,String description,long phoneNumber){
        Log.d(TAG, "updateUserAccountSettings: updating user account settings ");

        if (displayName != null){
            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userId)
                    .child(mContext.getString(R.string.display_name))
                    .setValue(displayName);
        }

        if (website != null){
            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userId)
                    .child(mContext.getString(R.string.web_site))
                    .setValue(website);
        }

        if (description != null){
            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userId)
                    .child(mContext.getString(R.string.description))
                    .setValue(description);
        }
        if (phoneNumber != 0){
            myRef.child(mContext.getString(R.string.dbname_users))
                    .child(userId)
                    .child(mContext.getString(R.string.phone_number))
                    .setValue(phoneNumber);
        }

    }
    //endregion

    //regionupdateUsername
    public void updateUsername(String username){
        Log.d(TAG, "upadteUsername: updating username to" + username);

        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userId)
                .child(mContext.getString(R.string.field_username))
                .setValue(username);

        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(userId)
                .child(mContext.getString(R.string.field_username))
                .setValue(username);
    }
    //endregion

    //regionupdateEmail
    public void updateEmail(String email){
        Log.d(TAG, "upadteUsername: updating email to" + email);

        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userId)
                .child(mContext.getString(R.string.field_email))
                .setValue(email);

    }
    //endregion

    //regionregisterWithEmail
    public void registerWithEmail(final String email,final String password, final String username){

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail: onComplete: " + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Toast.makeText(mContext, R.string.auth_failed, Toast.LENGTH_SHORT).show();

                        }else if (task.isSuccessful()){
                            //sending a verification email
                            sendVerification();

                            userId = mAuth.getCurrentUser().getUid();
                            Log.d(TAG, "onComplete: Authstate changed " + userId);
                        }

                    }
                });

    }
    //endregion

    //regionsendVerificationOnEmail
    public void sendVerification(){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null){
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                
                            }else {
                                Toast.makeText(mContext, "Couldn't send verification email.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }
    //endregion

    //regionaddNewUser
    public void addNewUser(String username,String email,String description,String website,String profile_photo){

        User user = new User(userId,1,email,StringManipulation.condenseUsername(username));

        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userId)
                .setValue(user);

        UserAccountSettings settings = new UserAccountSettings(
                description,
                username,
                0,
                0,
                0,
                profile_photo,
                StringManipulation.condenseUsername(username),
                website,
                userId
        );

        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(userId)
                .setValue(settings);
        }
    //endregion

    //regiongetUserSettings
    public UserSettings getUserSettings(DataSnapshot dataSnapshot) {
        Log.d(TAG, "getUserAccountSettings: retrieving user account settings from database");


        UserAccountSettings settings = new UserAccountSettings();
        User user = new User();

        //user_account_settings node
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            if (ds.getKey().equals(mContext.getString(R.string.dbname_user_account_settings))) {
                Log.d(TAG, "getUserAccountSettings: dataSnapshot " + ds);

                try {

                    settings.setDisplay_name(
                            ds.child(userId)
                                    .getValue(UserAccountSettings.class)
                                    .getDisplay_name()
                    );
                    settings.setUser_name(
                            ds.child(userId)
                                    .getValue(UserAccountSettings.class)
                                    .getUser_name()
                    );
                    settings.setWeb_site(
                            ds.child(userId)
                                    .getValue(UserAccountSettings.class)
                                    .getWeb_site()
                    );
                    settings.setDescription(
                            ds.child(userId)
                                    .getValue(UserAccountSettings.class)
                                    .getDescription()
                    );
                    settings.setProfile_photo(
                            ds.child(userId)
                                    .getValue(UserAccountSettings.class)
                                    .getProfile_photo()
                    );
                    settings.setPosts(
                            ds.child(userId)
                                    .getValue(UserAccountSettings.class)
                                    .getPosts()
                    );
                    settings.setFollowing(
                            ds.child(userId)
                                    .getValue(UserAccountSettings.class)
                                    .getFollowing()
                    );
                    settings.setFollowers(
                            ds.child(userId)
                                    .getValue(UserAccountSettings.class)
                                    .getFollowers()
                    );

                    Log.d(TAG, "getUserAccountSettings: retrieved user_account_settings information: " + settings.toString());
                } catch (NullPointerException e) {
                    Log.e(TAG, "getUserAccountSettings: NullPointerException" + e.getMessage());
                }

            }

            if (ds.getKey().equals(mContext.getString(R.string.dbname_users))) {
                Log.d(TAG, "getUsers: dataSnapshot " + ds);

                user.setUser_name(
                        ds.child(userId)
                                .getValue(User.class)
                                .getUser_name()
                );
                user.setEmail(
                        ds.child(userId)
                                .getValue(User.class)
                                .getEmail()
                );
                user.setPhone_number(
                        ds.child(userId)
                                .getValue(User.class)
                                .getPhone_number()
                );
                user.setUser_id(
                        ds.child(userId)
                                .getValue(User.class)
                                .getUser_id()
                );
                Log.d(TAG, "getUserAccountSettings: retrieved user information: " + user.toString());

            }

        }

        return new UserSettings(user,settings);

    }
        //endregion

    //region Image Count
    public int getImageCount(DataSnapshot dataSnapshot){
        int count = 0;
        for(DataSnapshot ds : dataSnapshot
                .child(mContext.getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .getChildren()){
            count++;

        }
        return count;

    }


    //endregion

    //region uploadNewPhoto
    public void uploadNewPhoto(String photoType, final String caption, int count, final String imgUrl,
                               Bitmap bm){
        Log.d(TAG, "uploadNewPhoto: attempting to upload new photo");

        FilePaths filePaths = new FilePaths();

        //case 1) new photo
        if (photoType.equals(mContext.getString(R.string.new_photo))){
            Log.d(TAG, "uploadNewPhoto: uploading new Photo");

            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            StorageReference storageReference = mStorageReference
                    .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/photo" + (count + 1));


            // convert image uri to bitmap

            if (bm == null){
                bm = ImageManager.getBitmap(imgUrl);
            }
            bm = ImageManager.getBitmap(imgUrl);
            byte[] bytes = ImageManager.getBytesFromBitmap(bm , 100);
            UploadTask uploadTask = null;
            uploadTask = storageReference.putBytes(bytes);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri firebaseUrl = taskSnapshot.getDownloadUrl();
                    Toast.makeText(mContext, "photo upload success", Toast.LENGTH_SHORT).show();

                    //add the new photo to 'photos' node and 'user_photos' node
                    addPhotoToDatabase(caption,firebaseUrl.toString());

                    //navigate to the main feed so the user can see their photo
                    Intent intent = new Intent(mContext, HomeActivity.class);
                    mContext.startActivity(intent);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Log.d(TAG, "onFailure: photo upload failed");
                    Toast.makeText(mContext, "Photo upload failed!", Toast.LENGTH_SHORT).show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    if (progress - 15 > mPhotoUploadProgress){
                        Toast.makeText(mContext, "photo upload progress " + String.format("%.0f" , progress) + "%", Toast.LENGTH_SHORT).show();
                        mPhotoUploadProgress = progress;
                    }
                    Log.d(TAG, "onProgress: upload progress " + progress + "% done");
                }
            });


        }
        //case 2) new profile photo
        else if (photoType.equals(mContext.getString(R.string.profile_photo))){
            Log.d(TAG, "uploadNewPhoto: uploading new Profile Photo");


            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            StorageReference storageReference = mStorageReference
                    .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/profile_photo");


            // convert image uri to bitmap
            if (bm == null){
                bm = ImageManager.getBitmap(imgUrl);
            }
            byte[] bytes = ImageManager.getBytesFromBitmap(bm , 100);
            UploadTask uploadTask = null;
            uploadTask = storageReference.putBytes(bytes);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri firebaseUrl = taskSnapshot.getDownloadUrl();
                    Toast.makeText(mContext, "photo upload success", Toast.LENGTH_SHORT).show();

                   //insert into 'user_account_settings' node
                    setProfilePhoto(firebaseUrl.toString());

                    ((AccountSettingsActivity)mContext).setViewPager(
                            ((AccountSettingsActivity)mContext).pagerAdapter
                                    .getFragmentNumbers(mContext.getString(R.string.editProfile)));



                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Log.d(TAG, "onFailure: photo upload failed");
                    Toast.makeText(mContext, "Photo upload failed!", Toast.LENGTH_SHORT).show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    if (progress - 15 > mPhotoUploadProgress){
                        Toast.makeText(mContext, "photo upload progress " + String.format("%.0f" , progress) + "%", Toast.LENGTH_SHORT).show();
                        mPhotoUploadProgress = progress;
                    }
                    Log.d(TAG, "onProgress: upload progress " + progress + "% done");
                }
            });

        }

    }

    private void setProfilePhoto(String url) {
        Log.d(TAG, "setProfilePhoto: setting new profile image " + url);

        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(mContext.getString(R.string.profile_photo))
                .setValue(url);
    }

    private String getTimeStamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy'T'HH-mm-ss'Z'", Locale.ITALY);
        sdf.setTimeZone(TimeZone.getTimeZone("Europe/Zagreb"));
        return sdf.format(new Date());
    }

    private void addPhotoToDatabase(String caption,String url){

        Log.d(TAG, "addPhotoToDatabase: adding  photo to database");

        String tags = StringManipulation.getTags(caption);
        String newPhotoKey = myRef.child(mContext.getString(R.string.dbname_photos)).push().getKey();
        Photo photo = new Photo();
        photo.setCaption(caption);
        photo.setDate_created(getTimeStamp());
        photo.setImage_path(url);
        photo.setTags(tags);
        photo.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
        photo.setPhoto_id(newPhotoKey);

        //insert into database
        myRef.child(mContext.getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(newPhotoKey).setValue(photo);
        myRef.child(mContext.getString(R.string.dbname_photos)).child(newPhotoKey).setValue(photo);

    }

    //endregion
}





