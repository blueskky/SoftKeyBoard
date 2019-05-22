package com.whty.eschoolbag.softkeybord;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.whty.eschoolbag.draft.Draft;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);

        Draft.getInstance().register(this);
    }





    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
