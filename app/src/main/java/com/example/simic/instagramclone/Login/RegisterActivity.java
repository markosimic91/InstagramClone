package com.example.simic.instagramclone.Login;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.simic.instagramclone.Models.User;
import com.example.simic.instagramclone.R;
import com.example.simic.instagramclone.Utils.FirebaseMethods;
import com.example.simic.instagramclone.Utils.StringManipulation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Simic on 19.3.2018..
 */

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private Context mContext;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String email,password,username;
    private FirebaseMethods firebaseMethods;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    private String append = "";

    //regioninitWidgetsWithButterKnife
    @BindView(R.id.inputEmail) EditText mEmail;
    @BindView(R.id.inputPassword) EditText mPassword;
    @BindView(R.id.inputUserName) EditText mUserName;
    @BindView(R.id.progressBar) ProgressBar mProgressBar;
    @BindView(R.id.pleaseWait) TextView mPleaseWait;
    @BindView(R.id.btn_register) Button mRegister;
    //endregion


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Log.d(TAG, "onCreate: started!");
        ButterKnife.bind(this);
        mContext = RegisterActivity.this;
        firebaseMethods = new FirebaseMethods(mContext);
        mProgressBar.setVisibility(View.GONE);
        mPleaseWait.setVisibility(View.GONE);

        //regionMethods
        setupFirebaseAuth();
        init();
        //endregion
    }


    //regionInit
    private void init(){
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = mEmail.getText().toString();
                password = mPassword.getText().toString();
                username = mUserName.getText().toString();

                if (checkInputs(email,password,username)){
                    mProgressBar.setVisibility(View.VISIBLE);
                    mPleaseWait.setVisibility(View.VISIBLE);

                    firebaseMethods.registerWithEmail(email,password,email);
                }

            }
        });

    }

    //endregion

    //regioncheckInputs

    private boolean checkInputs(String email,String password,String username){
        Log.d(TAG, "checkInputs: checking inpust for null values.");
        if (email.equals("") || password.equals("") || username.equals("")){
            Toast.makeText(mContext, "Please check your inputs!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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

                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    if (singleSnapshot.exists()){
                        Log.d(TAG, "checkIfUserNameExists: FOUND A MATCH :" + singleSnapshot.getValue(User.class).getUser_name());
                        append = myRef.push().getKey().substring(3,10);
                        Log.d(TAG, "onDataChange: username already exists. Appending random string to name" + append);

                    }
                }

                String mUsername = "";

                mUsername = username + append;
                //add new user to the database
                firebaseMethods.addNewUser(mUsername,email,"","","");

                Toast.makeText(mContext, "Signup successful. Sending verifcation email", Toast.LENGTH_SHORT).show();

                mAuth.signOut();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();

                //check if the user is logged in

                if (user != null){
                    Log.d(TAG, "onAuthStateChanged: signed_in:" + user.getUid());

                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            checkIfUserNameExists(username);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    finish();

                }else {
                    Log.d(TAG, "onAuthStateChanged: signed_out!");

                }
            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }
    //endregion

    //regionisStringNull
    private boolean isStringNull(String string){
        Log.d(TAG, "isStringNull: checking string if null");
        if (string.equals("")){
            return true;
        }else {
            return false;
        }
    }
    //endregion

}
