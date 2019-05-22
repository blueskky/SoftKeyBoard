package com.whty.eschoolbag.draft;

import android.content.Context;

public class LibContext {

    public static Context mInstance;

    public static void onCreate(Context context) {
        mInstance = context;
    }

    public static Context getContext() {
        return mInstance;
    }
}