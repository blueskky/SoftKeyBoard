package com.whty.eschoolbag.draft;

import android.app.Fragment;
import android.os.Bundle;

//空白Fragment
public class LifeListenerFragment extends Fragment {

    private LifeListener mLifeListener;


    public void addLifeListener(LifeListener listener) {
        mLifeListener = listener;
    }

    public void removeLifeListener() {
        mLifeListener = null;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mLifeListener != null) {
            mLifeListener.onCreate();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        if (mLifeListener != null) {
            mLifeListener.onStart();
        }
    }


    @Override
    public void onPause() {
        super.onPause();

        if (mLifeListener != null) {
            mLifeListener.onPause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mLifeListener != null) {
            mLifeListener.onStop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mLifeListener != null) {
            mLifeListener.onResume();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mLifeListener != null) {
            mLifeListener.onDestroy();
        }
    }
}