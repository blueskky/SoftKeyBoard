package com.whty.eschoolbag.draft;

import android.os.Bundle;

//生命周期回调接口
public interface LifeListener {

    void onCreate();

    void onStart();

    void onResume();

    void onPause();

    void onStop();

    void onDestroy();
}