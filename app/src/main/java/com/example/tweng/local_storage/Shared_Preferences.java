package com.example.tweng.local_storage;

import android.content.Context;
import android.content.SharedPreferences;

public class Shared_Preferences {
    private int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "profile_pic";
    private static SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;

    public Shared_Preferences(Context _context) {
        this._context = _context;
    }


    private void writePreference(){
        //_context = ge
    }

}
