package com.whty.eschoolbag.softkeyboard.keyboard;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;


import com.whty.eschoolbag.softkeyboard.R;
import com.whty.eschoolbag.softkeyboard.utils.DisplayUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

/**
 * 键盘主类
 */
public class CustomKeyboard extends PopupWindow {

    private KeyboardView keyboardView;

    /**
     * 是否为数字键盘
     */
    private boolean isNumber = false;

    /**
     * 是否大写
     */

    private View mKeyboardViewContainer;
    private EditText curEditText;
    private Context mContext;
    private RadioGroup llCheckMenu;
    Keyboard keyboard;
    private KeyboardType currKeyboardType;
    private ViewGroup mRootView;
    private RadioButton rb_zh, rb_used, rb_adc, rb_alpha, rb_symbol, rb_expression;
    private final View mChildOfContent;
    private int usableHeightPrevious;



    public static void init(Activity mContext, KeyboardConfig configure) {
        new CustomKeyboard(mContext,configure);
    }


    @SuppressLint("ClickableViewAccessibility")
    public CustomKeyboard(Activity mContext, KeyboardConfig keyboardConfig) {
        super(mContext);
        this.mContext = mContext;
        mRootView = (mContext.getWindow().getDecorView().findViewById(android.R.id.content));

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mKeyboardViewContainer = inflater.inflate(R.layout.gs_keyboard, null);

        mKeyboardViewContainer.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        this.setContentView(mKeyboardViewContainer);


        this.setWidth(DisplayUtils.getScreenWidth(mContext) - 200);
//        this.setWidth(LayoutParams.WRAP_CONTENT);
        this.setHeight(LayoutParams.WRAP_CONTENT);


        ColorDrawable dw = new ColorDrawable(Color.parseColor("#00000000"));
        // 设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);

        this.setFocusable(true);
        this.setOutsideTouchable(false);
        this.setPopupWindowTouchModal(this, false);

        this.setAnimationStyle(com.whty.eschoolbag.softkeyboard.R.style.PopupKeybroad);

        //init View
        llCheckMenu = mKeyboardViewContainer.findViewById(R.id.ll_menu);
        keyboardView = mKeyboardViewContainer.findViewById(R.id.keyboard_view);

        rb_zh = mKeyboardViewContainer.findViewById(R.id.tv_zh);
        rb_used = mKeyboardViewContainer.findViewById(R.id.tv_used);
        rb_adc = mKeyboardViewContainer.findViewById(R.id.tv_abc);
        rb_alpha = mKeyboardViewContainer.findViewById(R.id.tv_greece);
        rb_expression = mKeyboardViewContainer.findViewById(R.id.tv_expression);
        rb_symbol = mKeyboardViewContainer.findViewById(R.id.tv_symbol);

        //init keyboard
        switchKeyboardType(keyboardConfig.getDefaultKeyboardType());

        llCheckMenu.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.tv_zh) {
                    dismiss();
                    showSystemKeyBoard();
                } else if (checkedId == R.id.tv_used) {
                    switchKeyboardType(KeyboardType.MULTI_MAXIMUM);
                } else if (checkedId == R.id.tv_abc) {
                    switchKeyboardType(KeyboardType.MULTI_LETTR);
                } else if (checkedId == R.id.tv_greece) {
                    switchKeyboardType(KeyboardType.MULTI_ALPHA);
                } else if (checkedId == R.id.tv_expression) {
                    switchKeyboardType(KeyboardType.MULTI_FORMULA);
                } else if (checkedId == R.id.tv_symbol) {
                    switchKeyboardType(KeyboardType.MULTI_SYMBOL);
                }
            }
        });


        keyboardView.setEnabled(true);
        keyboardView.setPreviewEnabled(false);
        keyboardView.setOnKeyboardActionListener(listener);

        List<View> children = getAllChildren(mRootView);
        for (int i = 0; i < children.size(); i++) {
            View view = children.get(i);
            if (view instanceof EditText) {
                EditText securityEditText = (EditText) view;
                securityEditText.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            curEditText = (EditText) v;
                            curEditText.requestFocus();
                            //curEditText.setInputType(InputType.TYPE_NULL);
                            //将光标移到文本最后
                            Editable editable = curEditText.getText();
                            Selection.setSelection(editable, editable.length());
                            //显示键盘
                            showKeyboardType();

                        }
                        return true;
                    }
                });
            }
        }


        FrameLayout content = (FrameLayout) mContext.findViewById(android.R.id.content);
        mChildOfContent = content.getChildAt(0);
        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {


                        int usableHeightNow = computeUsableHeight();
                        if (usableHeightNow != usableHeightPrevious) {
                            int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();
                            int heightDifference = usableHeightSansKeyboard - usableHeightNow;
                            if (heightDifference > (usableHeightSansKeyboard / 4)) {
                                // keyboard probably just became visible
                                Log.d("lhq","onGlobalLayout visible");
                            } else {
                                // keyboard probably just became hidden

                                Log.d("lhq","onGlobalLayout hidden");
                            }
                            mChildOfContent.requestLayout();
                            usableHeightPrevious = usableHeightNow;
                        }

                    }
                });
    }


    private int computeUsableHeight() {
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        return (r.bottom - r.top);
    }

    public void showKeyboardType() {
        int inputType = curEditText.getInputType();

        //通过input控制类型
        switch (inputType) {
            case InputType.TYPE_CLASS_NUMBER:
            case InputType.TYPE_CLASS_PHONE:
                switchKeyboardType(KeyboardType.NUMBER_ONLY);
                break;

        }

        //通过setTag控制类型
        Object tag = curEditText.getTag(R.id.input_type);
        if (tag != null) {
            KeyboardType type = (KeyboardType) tag;
            if (type.getCode() == KeyboardType.INPUT_ZH.getCode()) {
                //中文
                hideCustomKeyboard();
                showSystemKeyBoard();
                return;
            }
            hideSystemKeyboard(curEditText);
            switchKeyboardType(type);
        }

        if (!this.isShowing()) {
            showCustomKeyBoard(curEditText);
        }


    }

    private void switchKeyboardType(KeyboardType type) {
        currKeyboardType = type;
        switch (type) {
            case NUMBER_ONLY:
                keyboard = new Keyboard(mContext, R.xml.gs_keyboard_number_only);
                llCheckMenu.setVisibility(View.GONE);
                break;

            case LETTER_ONLY:
                keyboard = new Keyboard(mContext, R.xml.gs_keyboard_letter);
                llCheckMenu.setVisibility(View.GONE);
                break;

            case MULTI_MAXIMUM: //综合常用

                keyboard = new Keyboard(mContext, R.xml.gs_keyboard_used);
                llCheckMenu.setVisibility(View.VISIBLE);
                rb_used.setChecked(true);
                break;

            case MULTI_LETTR: //综合字母

                keyboard = new Keyboard(mContext, R.xml.gs_keyboard_letter);
                llCheckMenu.setVisibility(View.VISIBLE);
                rb_adc.setChecked(true);
                break;

            case MULTI_ALPHA: //综合希腊

                keyboard = new Keyboard(mContext, R.xml.gs_keyboard_alpha);
                llCheckMenu.setVisibility(View.VISIBLE);
                rb_alpha.setChecked(true);
                break;

            case MULTI_FORMULA: //综合公式

                keyboard = new Keyboard(mContext, R.xml.gs_keyboard_number_only);
                llCheckMenu.setVisibility(View.VISIBLE);
                rb_expression.setChecked(true);
                break;

            case MULTI_SYMBOL: //综合符号

                keyboard = new Keyboard(mContext, R.xml.gs_keyboard_symbols);
                llCheckMenu.setVisibility(View.VISIBLE);
                rb_symbol.setChecked(true);
                break;

            default:
                keyboard = new Keyboard(mContext, R.xml.gs_keyboard_letter);
                llCheckMenu.setVisibility(View.GONE);
                break;

        }
        keyboardView.setKeyboard(keyboard);
    }


    /**
     * @param popupWindow popupWindow 的touch事件传递
     * @param touchModal  true代表拦截，事件不向下一层传递，false表示不拦截，事件向下一层传递
     */
    @SuppressLint("PrivateApi")
    private void setPopupWindowTouchModal(PopupWindow popupWindow, boolean touchModal) {
        Method method;
        try {
            method = PopupWindow.class.getDeclaredMethod("setTouchModal", boolean.class);
            method.setAccessible(true);
            method.invoke(popupWindow, touchModal);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void showSystemKeyBoard() {
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            public void run() {
                InputMethodManager inputManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(curEditText, 0);
//            }
//        }, 100);

    }

    public void hideSystemKeyboard(View view) {
        InputMethodManager manager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager != null) {
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }


    private OnKeyboardActionListener listener = new OnKeyboardActionListener() {
        @Override
        public void swipeUp() {
        }

        @Override
        public void swipeRight() {
        }

        @Override
        public void swipeLeft() {
        }

        @Override
        public void swipeDown() {
        }

        @Override
        public void onText(CharSequence text) {
            if (curEditText == null)
                return;
            Editable editable = curEditText.getText();
            int start = curEditText.getSelectionStart();
            int end = curEditText.getSelectionEnd();
            String temp = editable.subSequence(0, start) + text.toString() + editable.subSequence(start, editable.length());
            curEditText.setText(temp);
            Editable etext = curEditText.getText();
            Selection.setSelection(etext, start + 1);

        }

        @Override
        public void onRelease(int primaryCode) {
        }

        @Override
        public void onPress(int primaryCode) {
        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            Editable editable = curEditText.getText();
            int start = curEditText.getSelectionStart();
            if (primaryCode == Keyboard.KEYCODE_CANCEL) {
                // 完成按钮所做的动作
                hideCustomKeyboard();
            } else if (primaryCode == Keyboard.KEYCODE_DELETE) {
                // 删除按钮所做的动作
                if (editable != null && editable.length() > 0) {
                    if (start > 0) {
                        editable.delete(start - 1, start);
                    }
                }
            } else if (primaryCode == Keyboard.KEYCODE_SHIFT) {
                // 大小写切换
                changeKey();
                keyboardView.setKeyboard(keyboard);

            } else if (primaryCode == Keyboard.KEYCODE_MODE_CHANGE) {
                // 数字键盘切换,默认是英文键盘
                if (isNumber) {
                    isNumber = false;
                    keyboardView.setKeyboard(keyboard);
                } else {
                    isNumber = true;
                    keyboardView.setKeyboard(keyboard);
                }
            } else if (primaryCode == 57419) {
                //左移
                if (start > 0) {
                    curEditText.setSelection(start - 1);
                }
            } else if (primaryCode == 57421) {
                //右移
                if (start < curEditText.length()) {
                    curEditText.setSelection(start + 1);
                }
            } else if (primaryCode == -7) {


            } else if (primaryCode == -7) {

            } else {
                editable.insert(start, Character.toString((char) primaryCode));
            }
        }
    };

    /**
     * 键盘大小写切换
     */
    private void changeKey() {
        if (currKeyboardType == KeyboardType.MULTI_LETTR || currKeyboardType == KeyboardType.LETTER_ONLY) {
            List<Key> keylist = keyboard.getKeys();
//            if (isUpperAlpha) {  //大写切换小写
//                isUpperAlpha = false;
//                for (Key key : keylist) {
//                    if (key.label != null && isLetter(key.label.toString())) {
//                        key.label = key.label.toString().toLowerCase();
//                        key.codes[0] = key.codes[0] + 32;
//                    }
//                    if (key.codes[0] == -1) {
//                        key.icon = mContext.getResources().getDrawable(R.drawable.keyboard_shift);
//                    }
//                }
//            } else {  //小写切换大写
//                isUpperAlpha = true;
//                for (Key key : keylist) {
//                    if (key.label != null && isLetter(key.label.toString())) {
//                        key.label = key.label.toString().toUpperCase();
//                        key.codes[0] = key.codes[0] - 32;
//                    }
//                    if (key.codes[0] == -1) {
//                        key.icon = mContext.getResources().getDrawable(R.drawable.keyboard_shift_c);
//                    }
//                }
//            }

            if (isUpperLetter(keylist)) {
                keyboard = new Keyboard(mContext, R.xml.gs_keyboard_letter);
            } else {
                keyboard = new Keyboard(mContext, R.xml.gs_keyboard_letter_uppercase);
            }

        } else if (currKeyboardType == KeyboardType.MULTI_ALPHA) {
            List<Key> keylist = keyboard.getKeys();
            if (isUpperAlpha(keylist)) {//大写转小写
                keyboard = new Keyboard(mContext, R.xml.gs_keyboard_alpha);
            } else { //小写转大写
                keyboard = new Keyboard(mContext, R.xml.gs_keyboard_alpha_uppercase);
            }

        }

    }

    private boolean isUpperLetter(List<Key> keylist) {
        for (Key key : keylist) {
            if (key.label != null) {
                if (isLetter(key.label.toString())) {
                    String letterStr = mContext.getString(R.string.aToz);
                    return !letterStr.contains(key.label.toString());
                }
            }

        }
        return false;
    }

    private boolean isUpperAlpha(List<Key> keylist) {
        for (Key key : keylist) {
            if (key.label != null) {
                if (isAlphaLetter(key.label.toString())) {
                    String letterStr = mContext.getString(R.string.aToz_for_alpha);
                    return !letterStr.contains(key.label.toString());
                }
            }
        }
        return false;
    }


    /**
     * 弹出键盘
     *
     * @param view
     */
    private void showCustomKeyBoard(final View view) {

        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.push_bottom_in);
        showAtLocation(view, Gravity.BOTTOM | Gravity.CENTER, 0, 0);
        getContentView().setAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scrollCurrentView(view);
                    }
                },200 );

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }

    private void scrollCurrentView(View view) {
        int[] vLocation = new int[2];
        view.getLocationOnScreen(vLocation); //计算输入框在屏幕中的位置
        int viewtop = vLocation[1] + view.getHeight() + view.getPaddingTop();

        int measuredHeight = getContentView().getMeasuredHeight();

        int screenHeight = DisplayUtils.getScreenHeight(mContext);

        int moveHeight = measuredHeight - (screenHeight - viewtop);
        if (moveHeight > 0) {
            mRootView.getChildAt(0).scrollBy(0, moveHeight); //移动屏幕
        } else {
            moveHeight = 0;
        }

        Log.d("lhq", "onAnimationEnd  moveHeight==" + moveHeight);

        mRootView.setTag(R.id.keyboard_view_move_height, moveHeight);
    }

    /**
     * 隐藏键盘
     */
    private void hideCustomKeyboard() {
        dismiss();
    }


    private boolean isAlphaLetter(String str) {
        String letterStr = mContext.getString(R.string.aToz_for_alpha);
        return letterStr.contains(str.toLowerCase());
    }

    private boolean isLetter(String str) {
        String letterStr = mContext.getString(R.string.aToz);
        return letterStr.contains(str.toLowerCase());
    }

    private boolean isNumber(String str) {
        String numStr = mContext.getString(R.string.zeroTonine);
        return numStr.contains(str.toLowerCase());
    }

    @Override
    public void dismiss() {
        super.dismiss();
        int moveHeight = 0;
        Object tag = mRootView.getTag(R.id.keyboard_view_move_height);
        if (null != tag) moveHeight = (int) tag;
        if (moveHeight > 0) { //复原屏幕
            mRootView.getChildAt(0).scrollBy(0, -1 * moveHeight);
            mRootView.setTag(R.id.keyboard_view_move_height, 0);
        }

        Log.d("lhq", "dismiss  moveHeight==" + moveHeight);
    }


    /**
     * 获取所有子元素
     *
     * @param parent
     * @return
     */
    private List<View> getAllChildren(View parent) {
        List<View> allChildren = new ArrayList<>();
        if (parent instanceof ViewGroup) {
            ViewGroup vp = (ViewGroup) parent;
            for (int i = 0; i < vp.getChildCount(); i++) {
                View child = vp.getChildAt(i);
                allChildren.add(child);
                //再次 调用本身（递归）
                allChildren.addAll(getAllChildren(child));
            }
        }
        return allChildren;
    }


}
