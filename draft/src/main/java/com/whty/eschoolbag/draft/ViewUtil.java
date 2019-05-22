package com.whty.eschoolbag.draft;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;

public class ViewUtil {
    private static final String TAG = "TQSViewUtil";

    /** UI设计基准宽度 */
    private final static int designW = 1920;
    /** UI设计基准高度. */
    private final static int designH = 1080;

    /**
     * 获取屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getDisplayMetricsWidth(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int w_screen = dm.widthPixels;
//        int h_screen = dm.heightPixels;
        //   CCLog.i("", "屏幕尺寸2：宽度 = " + w_screen + "高度 = " + h_screen + "密度 = " + dm.densityDpi);
        return w_screen;
    }

    /**
     * 获取屏幕高度
     *
     * @param context
     * @return
     */
    public static int getDisplayMetricsHeight(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
//        int w_screen = dm.widthPixels;
        int h_screen = dm.heightPixels;
//        CCLog.i("", "屏幕尺寸2：宽度 = " + w_screen + "高度 = " + h_screen + "密度 = " + dm.densityDpi);
        return h_screen;
    }

    public static Point getScreenWithOutStatusBar(Activity activity) {
        Rect outRect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
        System.out.println("top:" + outRect.top + " ; left: " + outRect.left);
        Point point = new Point((int) outRect.width(), (int) outRect.height());
        return point;
    }


    /**
     * 等x轴比例换算
     *
     * @param context
     * @param value
     * @return
     */
    public static int x(Context context, int value) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int w_screen = dm.widthPixels;
        int h_screen = dm.heightPixels;

        int result = value * w_screen / designW;
//        if (w_screen > h_screen) {
//            result = value * w_screen / designH;
//        }
//        JLog.d(TAG ,"x: value = " + value + " result= " + result);
        return result < 1 ? value : result;
    }

    /**
     * 等y轴比例换算
     *
     * @param context
     * @param value
     * @return
     */
    public static int y(Context context, int value) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int w_screen = dm.widthPixels;
        int h_screen = dm.heightPixels;
        int result = value * h_screen / designH;
//        if (w_screen > h_screen) {
//            result = value * h_screen / designW;
//        }
        // JLog.d(TAG ,"y: value = " + value + " result= " + result);
        return result < 1 ? value : result;
    }

    /**
     * 等x比例换算字体大小
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static float font(Context context, int pxValue) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int w_screen = dm.widthPixels;
        int h_screen = dm.heightPixels;

        int scaleValue = w_screen * pxValue / designW;
//        if (w_screen > h_screen) {
//            scaleValue = w_screen * pxValue / designH;
//        }

        float result = px2sp(context, scaleValue);
        // JLog.d(TAG, "font: pxValue=" + pxValue + " scaleValue=" + scaleValue + " result=" + result);
        return result;
    }

    /**
     * 设置文字大小适配
     * @param context
     * @param size
     * @param textView
     */
    public static void font(Context context, int size, TextView textView){
        textView.setTextSize(font(context, size));
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     *
     * @param context
     * @param dpValue
     * @return
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /***
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 sp
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        //  CCLog.i("getScaleX fontScale=" + fontScale);
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 sp 的单位 转成为 px
     *
     * @param context
     * @param spValue
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }


    /**
     * 根据手机高度，去8%的高度作为标题栏高度
     */
    public static int titleHeight(Context context) {
        return y(context, 110);
    }

    /**
     * 根据手机高度，去8%的高度作为标题栏高度
     */
    public static void titleHeight(Context context, View view) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        layoutParams.height = y(context, 110);
        view.setLayoutParams(layoutParams);
    }

    /**
     * 按屏幕等比例适配控件
     *
     * @param context
     * @param w
     * @param h
     * @param view
     */
    public static void size(Context context, int w, int h, View view) {
        ViewGroup.LayoutParams layoutParams = (ViewGroup.LayoutParams) view.getLayoutParams();
        if (w != 0) {
            layoutParams.width = x(context, w);
        }
        if (h != 0) {
            layoutParams.height = y(context, h);
        }
        view.setLayoutParams(layoutParams);
    }

    /**
     * 按屏幕宽度等比例适配控件
     *
     * @param context
     * @param w
     * @param h
     * @param view
     */
    public static void size_x(Context context, int w, int h, View view) {
        ViewGroup.LayoutParams layoutParams = (ViewGroup.LayoutParams) view.getLayoutParams();
        if (w != 0) {
            layoutParams.width = x(context, w);
        }
        if (h != 0) {
            layoutParams.height = x(context, h);
        }
        view.setLayoutParams(layoutParams);
    }

    /**
     * 按屏幕高度等比例适配控件
     *
     * @param context
     * @param w
     * @param h
     * @param view
     */
    public static void size_y(Context context, int w, int h, View view) {
        ViewGroup.LayoutParams layoutParams = (ViewGroup.LayoutParams) view.getLayoutParams();
        if (w != 0) {
            layoutParams.width = y(context, w);
        }
        if (h != 0) {
            layoutParams.height = y(context, h);
        }
        view.setLayoutParams(layoutParams);
    }

    public static void margins(Context context, int left, int top, int right, int bottom, View view) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        if(left != 0){
            layoutParams.leftMargin = x(context, left);
        }
        if(top != 0){
            layoutParams.topMargin = y(context, top);
        }
        if(right != 0){
            layoutParams.rightMargin = x(context, right);
        }
        if(bottom != 0){
            layoutParams.bottomMargin = y(context, bottom);
        }
        view.setLayoutParams(layoutParams);
    }

    public static void margins_x(Context context, int left, int top, int right, int bottom, View view) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        if(left != 0){
            layoutParams.leftMargin = x(context, left);
        }
        if(top != 0){
            layoutParams.topMargin = x(context, top);
        }
        if(right != 0){
            layoutParams.rightMargin = x(context, right);
        }
        if(bottom != 0){
            layoutParams.bottomMargin = x(context, bottom);
        }
        view.setLayoutParams(layoutParams);
    }

    public static void margins_y(Context context, int left, int top, int right, int bottom, View view) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        if(left != 0){
            layoutParams.leftMargin = y(context, left);
        }
        if(top != 0){
            layoutParams.topMargin = y(context, top);
        }
        if(right != 0){
            layoutParams.rightMargin = y(context, right);
        }
        if(bottom != 0){
            layoutParams.bottomMargin = y(context, bottom);
        }
        view.setLayoutParams(layoutParams);
    }

    public static void padding(Context context, int left, int top, int right, int bottom, View view) {
        view.setPadding(x(context, left), y(context, top), x(context, right), y(context, bottom));
    }

    public static void padding_x(Context context, int left, int top, int right, int bottom, View view) {
        view.setPadding(x(context, left), x(context, top), x(context, right), x(context, bottom));
    }

    public static void padding_y(Context context, int left, int top, int right, int bottom, View view) {
        view.setPadding(y(context, left), y(context, top), y(context, right), y(context, bottom));
    }


    /**
     * 根据密度判断是否是平板，不太靠谱
     * @param context
     * @return
     */
    public static boolean isScaledDensitySmall(Context context){
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        if(fontScale > 1.5){
            return false;
        }else {
            return true;
        }
    }

    /**
     * 列表适配专用
     * @param context
     * @param w
     * @param h
     * @param view
     */
    public static void list_size(Context context, int w, int h, View view){
        AbsListView.LayoutParams layoutParams = (AbsListView.LayoutParams) view.getLayoutParams();
        if (w != 0) {
            layoutParams.width = x(context, w);
        }
        if (h != 0) {
            layoutParams.height = y(context, h);
        }
        view.setLayoutParams(layoutParams);
    }

    /**
     * 列表适配专用
     * @param context
     * @param w
     * @param h
     * @param view
     */
    public static void list_size_x(Context context, int w, int h, View view){
        AbsListView.LayoutParams layoutParams = (AbsListView.LayoutParams) view.getLayoutParams();
        if (w != 0) {
            layoutParams.width = x(context, w);
        }
        if (h != 0) {
            layoutParams.height = x(context, h);
        }
        view.setLayoutParams(layoutParams);
    }

    /**
     * 列表适配专用
     * @param context
     * @param w
     * @param h
     * @param view
     */
    public static void list_size_y(Context context, int w, int h, View view){
        AbsListView.LayoutParams layoutParams = (AbsListView.LayoutParams) view.getLayoutParams();
        if (w != 0) {
            layoutParams.width = y(context, w);
        }
        if (h != 0) {
            layoutParams.height = y(context, h);
        }
        view.setLayoutParams(layoutParams);
    }
}