package com.example.simic.instagramclone.Dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.simic.instagramclone.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Simic on 26.3.2018..
 */

public class ConfirmPasswordDialog extends DialogFragment{
    private static final String TAG = "ConfirmPasswordDialog";

    public interface OnConfirmPasswordListener{
        public void onConfirmPassword(String password);
    }

    OnConfirmPasswordListener mOnConfirmPasswordListener;

    @BindView(R.id.dialogCancel) TextView cancelDialog;
    @BindView(R.id.dialogConfirm) TextView confirmDialog;
    @BindView(R.id.confirmPassword) EditText confirmPassword;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_confirm_password,container,false);
        Log.d(TAG, "onCreateView: stared");
        ButterKnife.bind(this,view);

        cancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing the dialog!");

                getDialog().dismiss();
            }
        });

        confirmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: captured password and confirming!");

                String password = confirmPassword.getText().toString();
                if (!password.equals("")) {
                    mOnConfirmPasswordListener.onConfirmPassword(password);
                    getDialog().dismiss();
                }else {
                    Toast.makeText(getActivity(), "you must enter a password", Toast.LENGTH_SHORT).show();
                }
            }
        });
        

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try{
            mOnConfirmPasswordListener = (OnConfirmPasswordListener) getTargetFragment();
        }catch (ClassCastException e){
            Log.d(TAG, "onAttach: ClassCastException" + e.getMessage());
        }

    }
}
