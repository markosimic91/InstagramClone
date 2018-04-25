package com.example.simic.instagramclone.Profile;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.simic.instagramclone.Dialogs.ConfirmPasswordDialog;
import com.example.simic.instagramclone.Models.User;
import com.example.simic.instagramclone.Models.UserAccountSettings;
import com.example.simic.instagramclone.Models.UserSettings;
import com.example.simic.instagramclone.R;
import com.example.simic.instagramclone.Share.ShareActivity;
import com.example.simic.instagramclone.Utils.FirebaseMethods;
import com.example.simic.instagramclone.Utils.UniversalImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Simic on 15.3.2018..
 */

public class EditProfileFragment extends Fragment implements ConfirmPasswordDialog.OnConfirmPasswordListener{

    //regionConfirm password for email change
    @Override
    public void onConfirmPassword(String password) {
        Log.d(TAG, "onConfirmPassword: got the password " + password);

        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.
        AuthCredential credential = EmailAuthProvider.getCredential(mAuth.getCurrentUser().getEmail(), password);

        // Prompt the user to re-provide their sign-in credentials
        mAuth.getCurrentUser().reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.d(TAG, "User re-authenticated.");

                            //check to see if the email is not already present in the database
                            mAuth.fetchProvidersForEmail(mEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                                    if (task.isSuccessful()) {

                                            try {

                                                if (task.getResult().getProviders().size() == 1){
                                                    Log.d(TAG, "onComplete: that email is already in use");
                                                    Toast.makeText(getActivity(), "That email is already in use", Toast.LENGTH_SHORT).show();
                                                }else {

                                                    Log.d(TAG, "onComplete:  That mail is available");

                                                    //the email is available so update it

                                                    mAuth.getCurrentUser().updateEmail(mEmail.getText().toString())
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                    if (task.isSuccessful()){
                                                                        Log.d(TAG, "onComplete: email is updated!");
                                                                        Toast.makeText(getActivity(), "Email is updated!", Toast.LENGTH_SHORT).show();
                                                                        mFirebaseMethods.updateEmail(mEmail.getText().toString());
                                                                    }
                                                                }
                                                            });

                                                }


                                            } catch (NullPointerException e) {
                                                Log.d(TAG, "onComplete: NullPointerException " + e.getMessage());
                                            }

                                    }
                                }

                            });

                        }else {
                            Log.d(TAG, "onComplete: re-authenticated failed");
                        }
                    }
                });
    }
    //endregion

    private static final String TAG = "EditProfileFragment";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;
    private String userId;

    //vars
    private UserSettings mUserSettings;

    //region WidgetsSetup
    @BindView(R.id.profile_photo) CircleImageView mProfilePhoto;
    @BindView(R.id.backArrow) ImageView backArrow;
    @BindView(R.id.saveChanges) ImageView checkMark;
    @BindView(R.id.display_name) EditText mDisplayName;
    @BindView(R.id.user_name) EditText mUserName;
    @BindView(R.id.web_site) EditText mWebsite;
    @BindView(R.id.description) EditText mDescription;
    @BindView(R.id.email) EditText mEmail;
    @BindView(R.id.phone) EditText mPhone;
    @BindView(R.id.changeProfilePhoto) TextView mChangeProfilePhoto;
    //endregion


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: started!");
        View view = inflater.inflate(R.layout.fragment_editprofile,container,false);
        ButterKnife.bind(this,view);
        mFirebaseMethods = new FirebaseMethods(getActivity());
        setupFirebaseAuth();
        //back arrow for navigation back to "Profile Activity"
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigation back to profileActivity");
                getActivity().finish();
            }
        });

        checkMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to save changes!");
                saveProfileSettings();

            }
        });


        return view;
    }


    //region SaveProfileSettings

    private void saveProfileSettings(){
        final String displayName = mDisplayName.getText().toString();
        final String username = mUserName.getText().toString();
        final String website = mWebsite.getText().toString();
        final String description = mDescription.getText().toString();
        final String email = mEmail.getText().toString();
        final long phoneNumber = Long.parseLong(mPhone.getText().toString());

        //case 1:if the user made a change to their username
        if (!mUserSettings.getUser().getUser_name().equals(username)){
            checkIfUserNameExists(username);
        }
        //case 2: if the user made a change to their email
        if (!mUserSettings.getUser().getEmail().equals(email)) {

            //step1) Reauthenticate
            //      -Confirm the password and email
            ConfirmPasswordDialog dialog = new ConfirmPasswordDialog();
            dialog.show(getFragmentManager(), getString(R.string.confirm_password_dialog));
            dialog.setTargetFragment(EditProfileFragment.this,1);

            //step2) check if the email already is registered
            //      -'fetchProvidersForEmail(String email)'
            //step3) change the email
            //      -submit the new email to the database and authentication

        }

        if (!mUserSettings.getSettings().getDisplay_name().equals(displayName)){

            //update display name
            mFirebaseMethods.updateUserAccountSettings(displayName,null,null,0);
        }

        if (!mUserSettings.getSettings().getWeb_site().equals(website)){

            //update website
            mFirebaseMethods.updateUserAccountSettings(null,website,null,0);
        }

        if (!mUserSettings.getSettings().getDescription().equals(description)){

            //update description
            mFirebaseMethods.updateUserAccountSettings(null,null,description,0);
        }

        if (!mUserSettings.getSettings().getProfile_photo().equals(phoneNumber)){

            //update phoneNumber
            mFirebaseMethods.updateUserAccountSettings(null,null,null,phoneNumber);
        }

    }
    //endregion

    //regionCheck If user name already exists in database
    private void checkIfUserNameExists(final String username) {
        Log.d(TAG, "checkIfUserNameExists: Checking if " + username + "already exists");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_users))
                .orderByChild(getString(R.string.field_username))
                .equalTo(username);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    //add the username
                    mFirebaseMethods.updateUsername(username);
                    Toast.makeText(getActivity(), "Saved username", Toast.LENGTH_SHORT).show();


                }
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    if (singleSnapshot.exists()){
                        Log.d(TAG, "checkIfUserNameExists: FOUND A MATCH :" + singleSnapshot.getValue(User.class).getUser_name());
                        Toast.makeText(getActivity(), "The user name already exists", Toast.LENGTH_SHORT).show();

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
    //endregion


    //region SetProfileWidgets
    private void setProfileWidgets(UserSettings userSettings){

        Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase" + userSettings.toString());

        mUserSettings = userSettings;
        //User user = userSettings.getUser();
        UserAccountSettings settings = userSettings.getSettings();

        UniversalImageLoader.setImage(settings.getProfile_photo(),mProfilePhoto,null,"");

        mDisplayName.setText(settings.getDisplay_name());
        mUserName.setText(settings.getUser_name());
        mWebsite.setText(settings.getWeb_site());
        mDescription.setText(settings.getDescription());
        mEmail.setText(userSettings.getUser().getEmail());
        mPhone.setText(String.valueOf(userSettings.getUser().getPhone_number()));

        mChangeProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: changing profile photo");

                Intent intent = new Intent(getActivity(), ShareActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        });


    }
    //endregion

    //regionFirebase

    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: setting up firebaseAuth");
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        userId = mAuth.getCurrentUser().getUid();

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

                //retrieve user information form database
                setProfileWidgets(mFirebaseMethods.getUserSettings(dataSnapshot));
                //retrieve image from the user in question

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
