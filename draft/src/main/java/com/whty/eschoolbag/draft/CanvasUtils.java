package com.whty.eschoolbag.draft;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Administrator on 2017/12/13 0013.
 */

public class CanvasUtils {

    /** UI设计基准宽度 */
    private final static int designW = 1920;
    /** UI设计基准高度. */
    private final static int designH = 1080;
    private static final String TAG = "CanvasUtils";

    /**
     * 获取屏幕宽度
     *
     * @param context
     * @return
     */
    public static int screen_w(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int w_screen = dm.widthPixels;
        int h_screen = dm.heightPixels;
//           CCLog.i("", "屏幕尺寸2：宽度 = " + w_screen + "高度 = " + h_screen + "密度 = " + dm.densityDpi);
        return w_screen;
    }

    /**
     * 获取屏幕高度
     *
     * @param context
     * @return
     */
    public static int screen_h(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int h_screen = dm.heightPixels;
//        CCLog.i("", "屏幕尺寸2：宽度 = " + w_screen + "高度 = " + h_screen + "密度 = " + dm.densityDpi);
        return h_screen;
    }

    public static Point screen_no_statusbar(Activity activity) {
        Rect outRect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
        System.out.println("top:" + outRect.top + " ; left: " + outRect.left);
        Point point = new Point((int) outRect.width(), (int) outRect.height());
        return point;
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
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
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
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
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
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        if (w != 0) {
            layoutParams.width = y(context, w);
        }
        if (h != 0) {
            layoutParams.height = y(context, h);
        }
        view.setLayoutParams(layoutParams);
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

        if (w_screen < h_screen) {
            w_screen = h_screen;
        }

        int result = value * w_screen / designW;


//        if (w_screen < h_screen) {
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

        if (w_screen < h_screen) {
            h_screen = w_screen;
        }

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
    public static int font(Context context, int pxValue) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int w_screen = dm.widthPixels;
        int h_screen = dm.heightPixels;

        if (w_screen < h_screen) {
            h_screen = w_screen;
        }

        int scaleValue = h_screen * pxValue / designH;
//        if (w_screen > h_screen) {
//            scaleValue = w_screen * pxValue / designH;
//        }

        int result = px2sp(context, scaleValue);
        // JLog.d(TAG, "font: pxValue=" + pxValue + " scaleValue=" + scaleValue + " result=" + result);
        return result;
    }

    /**
     * 等x比例换算字体大小，返回单位为sp
     *
     * @param context
     * @param size
     * @param textView
     */
    public static void font(Context context, int size, TextView textView) {
        textView.setTextSize(font(context, size));
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

    public static void margins_y(Context context, int left, int top, int right, int bottom, View view) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        if (left != 0) {
            layoutParams.leftMargin = y(context, left);
        }
        if (top != 0) {
            layoutParams.topMargin = y(context, top);
        }
        if (right != 0) {
            layoutParams.rightMargin = y(context, right);
        }
        if (bottom != 0) {
            layoutParams.bottomMargin = y(context, bottom);
        }
        view.setLayoutParams(layoutParams);
    }
    public static void margins(Context context, int left, int top, int right, int bottom, View view) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        if (left != 0) {
            layoutParams.leftMargin = x(context, left);
        }
        if (top != 0) {
            layoutParams.topMargin = y(context, top);
        }
        if (right != 0) {
            layoutParams.rightMargin = x(context, right);
        }
        if (bottom != 0) {
            layoutParams.bottomMargin = y(context, bottom);
        }
        view.setLayoutParams(layoutParams);
    }
    public static void padding_y(Context context, int left, int top, int right, int bottom, View view) {
        view.setPadding(y(context, left), y(context, top), y(context, right), y(context, bottom));
    }



    public static final String BASEPATH = Environment.getExternalStorageDirectory() + File.separator + "eschoolHomework";
    private static final String CANVAS_NAME = "homework";

    public static String getCanvasFileDir() {
        File file = new File(BASEPATH + File.separator + "canvas");
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getPath();
    }

    public static String getCanvasFilePath() {
        String name = CANVAS_NAME + System.currentTimeMillis() + ".jpg";
        File file = new File(getCanvasFileDir(), name);
        return name;

    }


    /**
     * show 保存图片到本地文件，耗时操作
     *
     * @return 返回保存的图片文件
     * @author TangentLu
     * create at 16/6/17 上午11:18
     */
    public static File saveInOI(Bitmap newBM) {
        String filePath = getCanvasFileDir();
        String imgName = getCanvasFilePath();

        if (!imgName.contains(".jpg")) {
            imgName += ".jpg";
        }
        Log.e(TAG, "saveInOI: " + System.currentTimeMillis());
        Log.e(TAG, "saveInOI: " + System.currentTimeMillis());

        try {
            File dir = new File(filePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File f = new File(filePath, imgName);
            if (!f.exists()) {
                f.createNewFile();
            } else {
                f.delete();
            }
            FileOutputStream out = new FileOutputStream(f);
            Log.e(TAG, "saveInOI: " + System.currentTimeMillis());

            int compress = 80;
            if (compress >= 1 && compress <= 100)
                newBM.compress(Bitmap.CompressFormat.JPEG, compress, out);
            else {
                newBM.compress(Bitmap.CompressFormat.JPEG, 80, out);
            }
            Log.e(TAG, "saveInOI: " + System.currentTimeMillis());
            out.close();
            newBM.recycle();
            newBM = null;
            return f;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
