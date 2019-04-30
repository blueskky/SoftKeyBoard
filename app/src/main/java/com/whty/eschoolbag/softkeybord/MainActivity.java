package com.whty.eschoolbag.softkeybord;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

import com.didichuxing.doraemonkit.DoraemonKit;
import com.whty.eschoolbag.softkeyboard.keyboard.CustomKeyboard;
import com.whty.eschoolbag.softkeyboard.keyboard.KeyboardConfig;
import com.whty.eschoolbag.softkeyboard.keyboard.KeyboardType;
import com.whty.eschoolbag.softkeyboard.utils.KeyBoardListener;

public class MainActivity extends AppCompatActivity {


    private EditText et1, et2, et3, et4;
    private CustomKeyboard customKeyboard;
    private WebView webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(this).inflate(R.layout.activity_main, null);
        setContentView(view);


        et1 = (EditText) findViewById(R.id.et1);
        et2 = (EditText) findViewById(R.id.et2);
        et3 = (EditText) findViewById(R.id.et3);

        et4 = (EditText) findViewById(R.id.et4);

        et1.setTag(R.id.input_type, KeyboardType.LETTER_ONLY);
        et2.setTag(R.id.input_type, KeyboardType.MULTI_ALPHA);
        et3.setTag(R.id.input_type, KeyboardType.MULTI_SYMBOL);
        et4.setTag(R.id.input_type, KeyboardType.INPUT_ZH);

        KeyboardConfig configure = new KeyboardConfig();
        configure.setDefaultKeyboardType(KeyboardType.MULTI_MAXIMUM);
        CustomKeyboard.init(this,configure);

    }

    private class JsInterface {

        @JavascriptInterface
        public void onFocus(){
            Log.d("lhq","onfocus");
            customKeyboard.hideSystemKeyboard(getWindow().getDecorView());
        }

        @JavascriptInterface
        public void onBlur(){
            Log.d("lhq","onBlur");
        }
    }
}
