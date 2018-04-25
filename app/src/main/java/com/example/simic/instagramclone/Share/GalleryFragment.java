package com.example.simic.instagramclone.Share;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.simic.instagramclone.Profile.AccountSettingsActivity;
import com.example.simic.instagramclone.R;
import com.example.simic.instagramclone.Utils.FilePaths;
import com.example.simic.instagramclone.Utils.FileSearch;
import com.example.simic.instagramclone.Utils.GridImageAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Simic on 13.3.2018..
 */

public class GalleryFragment extends Fragment{
    private static final String TAG = "GalleryFragment";

    //constants
    private static final int NUM_GRID_COLUMNS = 3;


    @BindView(R.id.gridView) GridView gridView;
    @BindView(R.id.galleryImageView) ImageView galleryImage;
    @BindView(R.id.ivCloseShare) ImageView ivCloseShare;
    @BindView(R.id.progressBar) ProgressBar mProgressBar;
    @BindView(R.id.spinnerDirectory) Spinner directorySpinner;
    @BindView(R.id.tvNext) TextView nextScreen;

    //vars
    private ArrayList<String> directories;
    private String mAppend = "file:/";
    private String mSelectedImage;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery,container,false);
        Log.d(TAG, "onCreateView: started");
        ButterKnife.bind(this,view);
        mProgressBar.setVisibility(View.GONE);
        directories = new ArrayList<>();


        ivCloseShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing the gallery fragment");
                getActivity().finish();
            }
        });

        nextScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigation to the final share screen");

                if (isRootTask()){
                    Intent intent = new Intent(getActivity(),NextActivity.class);
                    intent.putExtra(getString(R.string.selected_image),mSelectedImage);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(getActivity(),AccountSettingsActivity.class);
                    intent.putExtra(getString(R.string.selected_image),mSelectedImage);
                    intent.putExtra(getString(R.string.return_to_fragment),getString(R.string.editProfile));
                    startActivity(intent);
                    getActivity().finish();
                }

            }
        });
        init();

        return view;
    }

    private boolean isRootTask(){
        if (((ShareActivity)getActivity()).getTask() == 0){
            return true;
        }else {
            return false;
        }
    }

    //regionInit
    private void init(){
        FilePaths filePaths = new FilePaths();

        //check for other folders inside "/storage/emulated/0/pictures"

        if (FileSearch.getDirectoryPath(filePaths.PICTURES) != null){
            directories = FileSearch.getDirectoryPath(filePaths.PICTURES);
        }

        ArrayList<String> directoryNames = new ArrayList<>();
        for(int i = 0; i<directories.size();i++){

            int index = directories.get(i).lastIndexOf("/");
            String string = directories.get(i).substring(index);
            directoryNames.add(string);
        }

        directories.add(filePaths.CAMERA);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item,directories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        directorySpinner.setAdapter(adapter);

        directorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: selected " + directories.get(position));

                //setup our image grid for the directory chosen
                setupGridView(directories.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    //endregion

    //region SetupGridView
    private void setupGridView(String selectedDirectory){
        Log.d(TAG, "setupGridView: directory chosen" + selectedDirectory);
        final ArrayList<String> imgURls = FileSearch.getFilePaths(selectedDirectory);

        //set the grid column width
        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth/NUM_GRID_COLUMNS;

        gridView.setColumnWidth(imageWidth);

        //use the grid adapter to adapter the images to gridview
        GridImageAdapter adapter = new GridImageAdapter(getActivity(),R.layout.layout_grid_imageview,mAppend,imgURls);

        gridView.setAdapter(adapter);

        //set the first image to be displayed when is fragment view is inflated

        try{

            setImage(imgURls.get(0) , galleryImage,mAppend);
            mSelectedImage = imgURls.get(0);

        }catch (ArrayIndexOutOfBoundsException e){
            Log.d(TAG, "setupGridView: ArrayIndexOutOfBoundsException " + e.getMessage());
        }




        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: selected an image " + imgURls.get(position));

                setImage(imgURls.get(position),galleryImage,mAppend);
                mSelectedImage = imgURls.get(position);
            }
        });


    }
    //endregion

    private void setImage(String imgURL, ImageView image , String append){
        Log.d(TAG, "setImage: setting image");

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(append + imgURL, image, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                mProgressBar.setVisibility(View.VISIBLE);

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                mProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                mProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
}
