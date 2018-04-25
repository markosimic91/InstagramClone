package com.example.simic.instagramclone.Home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.simic.instagramclone.R;

/**
 * Created by Simic on 13.3.2018..
 */

public class HomeFragment extends Fragment{
    private static final String TAG = "HomeFragment";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);

        return view;
    }
}
