package com.whty.eschoolbag.softkeybord;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

import com.whty.eschoolbag.softkeyboard.keyboard.CustomKeyboard;
import com.whty.eschoolbag.softkeyboard.keyboard.KeyboardConfig;
import com.whty.eschoolbag.softkeyboard.keyboard.KeyboardType;

public class MainActivity extends AppCompatActivity implements CustomKeyboard.TxtListener {


    private EditText et1, et2, et3, et4;
    private WebView webView;
    private EditText editText;
    private CustomKeyboard customKeyboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(this).inflate(R.layout.activity_main, null);
        setContentView(view);




//        webView = (WebView) findViewById(R.id.web_view);
//
//        webView.setWebChromeClient(new WebChromeClient());
//        webView.setWebViewClient(new WebViewClient());
//        webView.getSettings().setJavaScriptEnabled(true);
//
//        initWebSetting();
//        webView.loadUrl("file:///android_asset/Keyboard-master/index.html");
//        webView.loadUrl("file:///android_asset/test.html");
//        webView.addJavascriptInterface(new JsInterface(), "js");


        editText = (EditText) findViewById(R.id.etd);
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
        customKeyboard = CustomKeyboard.init(this, this,configure);


    }

    private void initWebSetting() {
        webView.getSettings().setDefaultFontSize(21);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowContentAccess(true);
//        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        // webView.setBackgroundColor(0);
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

//        webView.getSettings().setDomStorageEnabled(true);
//        webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        webView.setVerticalScrollBarEnabled(false);
        webView.setVerticalScrollbarOverlay(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setHorizontalScrollbarOverlay(false);

        // webView.getSettings().setUseWideViewPort(true);
//        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDomStorageEnabled(true);
//        webView.getSettings().setSupportZoom(true); // 设置可以支持缩放
        webView.getSettings().setSupportZoom(false); // 设置可以支持缩放

    }

    @Override
    public void onTextChange(final String txt) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl("javascript:input('" + txt + "');");
            }
        });
    }

    private class JsInterface {

        @JavascriptInterface
        public void onFocus() {
            Log.d("lhq", "onfocus");
        }

        @JavascriptInterface
        public void onBlur() {
            Log.d("lhq", "onBlur");
        }

        @JavascriptInterface
        public void onClickEdit() {
            Log.d("lhq", "onClickEdit");
            customKeyboard.showKeyboardType();

        }
    }


    public void hideSystemKeyboard(View view) {
//        InputMethodManager manager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//        if (manager != null) {
//            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
//        }


//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);


        while (true) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            boolean isOpen = imm.isActive();//isOpen若返回true，则表示输入法打开

            if (isOpen) {
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                break;
            }

        }


    }


}
