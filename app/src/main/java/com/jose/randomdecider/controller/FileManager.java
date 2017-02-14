package com.jose.randomdecider.controller;

import android.content.Context;

import com.jose.randomdecider.MainActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.os.ParcelFileDescriptor.MODE_WORLD_READABLE;

/**
 * Created by jose on 2/7/2017.
 */

public class FileManager {

    public static void writeFile(String fileName, String contents, Context context) throws IOException {
        FileOutputStream fOut = context.openFileOutput(fileName, MODE_WORLD_READABLE);
        fOut.write(contents.getBytes());
        fOut.close();
    }

    public static String readFile(String fileName, Context context) throws IOException {
        FileInputStream fin = context.openFileInput(fileName);
        int c;
        String temp = "";
        while ((c = fin.read()) != -1) {
            temp = temp + Character.toString((char) c);
        }

        //string temp contains all the data of the file.
        fin.close();

        return temp;
    }

    public static boolean saveFilesExists(String fileName, Context context) {
        File f = context.getFileStreamPath(fileName);
        return f.exists();
    }
}
