package com.example.simic.instagramclone.Profile;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.simic.instagramclone.Login.LoginActivity;
import com.example.simic.instagramclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Simic on 15.3.2018..
 */

public class SignOutFragment extends Fragment {

    private static final String TAG = "SignOutFragment";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private Context mContext;

    @BindView(R.id.btnConfirmSignOut) Button btnConfirm;
    @BindView(R.id.progressBar) ProgressBar mProgressBar;
    @BindView(R.id.tvSigningOut) TextView tvSigningOut;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: started!");
        View view = inflater.inflate(R.layout.fragment_signout,container,false);
        ButterKnife.bind(this,view);
        setupFirebaseAuth();

        mProgressBar.setVisibility(View.GONE);
        tvSigningOut.setVisibility(View.GONE);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressBar.setVisibility(View.VISIBLE);
                tvSigningOut.setVisibility(View.VISIBLE);

                mAuth.signOut();
                getActivity().finish();
            }
        });

        return view;
    }

    //regionFirebase
    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: setting up firebaseAuth");
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null){
                    Log.d(TAG, "onAuthStateChanged: signed_in:" + user.getUid());
                }else {
                    Log.d(TAG, "onAuthStateChanged: signed_out!");

                    Log.d(TAG, "onAuthStateChanged: navigation back to login activity");

                    Intent intent = new Intent(getActivity(),LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
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
