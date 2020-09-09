package com.example.tweng.local_storage;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SaveProfilePic {

    private Context _context;
    private Uri imageUri;
    private Bitmap bitmap;

    public SaveProfilePic(Context _context, Bitmap bitmap) {
        this._context = _context;
        this.bitmap = bitmap;
        addBitMap();
    }

    public SaveProfilePic(Context _context, Uri imageUri) throws IOException {
        this._context = _context;
        this.imageUri = imageUri;
        addFromUri();
    }




    private void addBitMap() {
        //ContextWrapper cw = new ContextWrapper(_context.getApplicationContext());
        String filename = "profile__pic.jpg";
        File directory = new File(_context.getFilesDir(), filename);

        File file = new File(directory, "profile_pic" + ".jpg");
        if (directory.exists()) {
            directory.delete();
        }
        Log.d("path", file.toString());
        FileOutputStream fos;
        try {
            directory.delete();
            fos = new FileOutputStream(directory);
            //bitmap = imageUri.getData();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

    }

    private void addFromUri() throws IOException {
        ContextWrapper cw = new ContextWrapper(_context.getApplicationContext());
        String filename = "profile_pic.jpg";
        File directory = new File(_context.getFilesDir(), filename);
        directory.delete();
        File file = new File(directory + File.separator + "profile_pic.jpg");
        //file.createNewFile();
        /**if (file.exists()) {
            file.delete();
        }**/
        Log.d("path", file.toString());
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(directory);
            //bitmap = imageUri.getData();
            bitmap = BitmapFactory.decodeFile(String.valueOf(imageUri));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            //addFromUri(this.imageUri);
            fos.flush();
            fos.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

}

