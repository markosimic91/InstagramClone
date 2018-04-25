package com.example.simic.instagramclone.Utils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Simic on 29.3.2018..
 */

public class FileSearch {


    //region Search directory and return a list of all ""directories"" contained inside
    public static ArrayList<String> getDirectoryPath(String directory){
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        File[] listfiles = file.listFiles();
        
        for (int i = 0; i < listfiles.length; i++){
            if (listfiles[i].isDirectory()){
                pathArray.add(listfiles[i].getAbsolutePath());
            }
        }
        return pathArray;
    }
    //endregion

    //region Search directory and return a list of all ""files"" contained inside
    public static ArrayList<String> getFilePaths(String directory){

        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        File[] listfiles = file.listFiles();

        for (int i = 0; i < listfiles.length; i++){
            if (listfiles[i].isFile()){
                pathArray.add(listfiles[i].getAbsolutePath());
            }
        }
        return pathArray;
    }
    //endregion

}
