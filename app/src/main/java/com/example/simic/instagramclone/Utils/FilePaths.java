package com.example.simic.instagramclone.Utils;

import android.os.Environment;

/**
 * Created by Simic on 29.3.2018..
 */

public class FilePaths {

    //storage/emulated/0
    public String ROOT_DIR = Environment.getExternalStorageDirectory().getPath();
    public String PICTURES = ROOT_DIR + "/Pictures";
    public String CAMERA = ROOT_DIR + "/DCIM/camera";

    public String FIREBASE_IMAGE_STORAGE = "photos/users/";
}
