package com.example.simic.instagramclone.Login;

import android.content.Context;
import android.content.Intent;
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

import com.example.simic.instagramclone.Home.HomeActivity;
import com.example.simic.instagramclone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Simic on 19.3.2018..
 */

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Context mContext;

    @BindView(R.id.progressBar) ProgressBar mProgressBar;
    @BindView(R.id.inputEmail) EditText mEmail;
    @BindView(R.id.inputPassword) EditText mPassword;
    @BindView(R.id.pleaseWait) TextView mPleaseWait;
    @BindView(R.id.btn_login) Button bLogin;
    @BindView(R.id.link_signup) TextView linkSignUp;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(TAG, "onCreate: started");
        ButterKnife.bind(this);

        //regionMethods
        init();
        setupFirebaseAuth();

        //endregion

        mContext = LoginActivity.this;

        mProgressBar.setVisibility(View.GONE);
        mPleaseWait.setVisibility(View.GONE);

        linkSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigation to register screen");
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean isStringNull(String string){
        Log.d(TAG, "isStringNull: checking string if null");
        if (string.equals("")){
            return true;
        }else {
            return false;
        }
    }

    //regionFirebase

    private void init(){

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to log in");

                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                if (isStringNull(email)&& isStringNull(password)){
                    Toast.makeText(mContext, "You must fill out all the fields", Toast.LENGTH_SHORT).show();
                    
                }else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mPleaseWait.setVisibility(View.VISIBLE);

                    mAuth.signInWithEmailAndPassword(email,password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.d(TAG, "signInWithEmail:onComplete: " + task.isSuccessful());


                                    FirebaseUser user = mAuth.getCurrentUser();

                                    
                                    if (!task.isSuccessful()){
                                        Log.w(TAG, "signInWithEmail:onComplete: failed ",task.getException());
                                        Toast.makeText(mContext, getString(R.string.auth_failed), Toast.LENGTH_SHORT).show();
                                        mProgressBar.setVisibility(View.GONE);
                                        mPleaseWait.setVisibility(View.GONE);

                                    }else {
                                        try {

                                            if (user.isEmailVerified()){
                                                Log.e(TAG, "onComplete: success , email is verified" );

                                                Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                                                startActivity(intent);

                                            }else {
                                                Toast.makeText(mContext, "Email is not verified yet, \n check your inbox!", Toast.LENGTH_SHORT).show();
                                                mProgressBar.setVisibility(View.GONE);
                                                mPleaseWait.setVisibility(View.GONE);
                                                mAuth.signOut();
                                            }

                                        }catch (NullPointerException e){
                                            Log.d(TAG, "onComplete: NullPointerException " + e.getMessage());
                                        }
                                    }
                                }
                            });

                    if (mAuth.getCurrentUser() != null){
                        Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });

    }

    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: setting up firebaseAuth");
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                //check if the usr is logged in

                if (user != null){
                    Log.d(TAG, "onAuthStateChanged: signed_in:" + user.getUid());
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
}
