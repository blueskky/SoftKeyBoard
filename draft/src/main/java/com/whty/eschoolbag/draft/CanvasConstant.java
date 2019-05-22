package com.whty.eschoolbag.draft;

import android.text.TextUtils;

/**
 * Created by chenjun on 2018/6/21 0021.
 */

public class CanvasConstant {

    public static final int STROKE_WIDTH_THIN = 2;
    public static final int STROKE_WIDTH_MIDDLE = 4;
    public static final int STROKE_WIDTH_THICK = 8;

    public static final int[] STROKE_WIDTHS = {
            STROKE_WIDTH_THIN,
            STROKE_WIDTH_MIDDLE,
            STROKE_WIDTH_THICK
    };


    public static final String COLOR_0 = "#000000";
    public static final String COLOR_1 = "#C97036";
    public static final String COLOR_2 = "#FFFFFE";
    public static final String COLOR_3 = "#8659F7";
    public static final String COLOR_4 = "#007AFF";
    public static final String COLOR_5 = "#21C0FF";
    public static final String COLOR_6 = "#4FC25D";
    public static final String COLOR_7 = "#96DA3E";
    public static final String COLOR_8 = "#FFCB06";
    public static final String COLOR_9 = "#F5699A";
    public static final String COLOR_10 = "#FF8200";
    public static final String COLOR_11 = "#FF1100";

    public static final String[] COLORS = {
            COLOR_0,
            COLOR_1,
            COLOR_2,
            COLOR_3,
            COLOR_4,
            COLOR_5,
            COLOR_6,
            COLOR_7,
            COLOR_8,
            COLOR_9,
            COLOR_10,
            COLOR_11

    };

    public static String DEFAULT_BACKGROUND = "#74B26E";    // 默认画板背景色

    public static final int DEFAULT_STROKE_WIDTH_INDEX = 1; // 默认笔迹粗细大小的数组下标值
    public static int DEFAULT_STROKE_WIDTH = STROKE_WIDTHS[DEFAULT_STROKE_WIDTH_INDEX];// 默认笔迹粗细大小

    public static final int DEFAULT_COLOR_INDEX = 0;// 默认笔迹颜色的数组下标值
    public static String DEFAULT_COLOR = COLORS[DEFAULT_COLOR_INDEX];// 默认笔迹颜色

    public static int indexOfColor(String color) {
        if (!TextUtils.isEmpty(color)) {
            for (int i = 0; i < COLORS.length; i++) {
                if (color.equalsIgnoreCase(COLORS[i])) {
                    return i;
                }
            }
        }
        return 0;
    }

    public static int indexOfWidth(int width) {
        for (int i = 0; i < STROKE_WIDTHS.length; i++) {
            if (width == STROKE_WIDTHS[i]) {
                return i;
            }
        }
        return 0;
    }

}
