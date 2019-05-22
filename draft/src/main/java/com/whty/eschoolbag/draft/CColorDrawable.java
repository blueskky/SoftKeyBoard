package com.whty.eschoolbag.draft;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;



/**
 * 画笔drawable
 * Created by chen on 2017/4/28 0028.
 */
public class CColorDrawable extends Drawable {

    private float radius;
    private int width, height;
    private int strokeSpace = 6;

    private int strokeColor = Color.parseColor("#a8a8a8");
    private int strokeWidth = 2; // 描边宽度
    private float strokeRadius;
    private Paint strokePaint;


    private int solidColor = Color.parseColor("#a8a8a8");
    private float solidRadius;
    private Paint solidPaint;

    private boolean ringFlag = false;

    public static CColorDrawable build(int color){
        CColorDrawable drawable = new CColorDrawable(color);
        return drawable;
    }

    public static Drawable create(int color){
        StateListDrawable drawable = new StateListDrawable();
        //如果要设置莫项为false，在前面加负号 ，比如android.R.attr.state_focesed标志true，-android.R.attr.state_focesed就标志false
        drawable.addState(new int[]{android.R.attr.state_selected}, CColorDrawable.build(color).setRingFlag(true));
        drawable.addState(new int[]{-android.R.attr.state_selected}, CColorDrawable.build(color).setRingFlag(false));
//        drawable.addState(new int[]{android.R.attr.state_selected}, CColorDrawable.build(color).setRingFlag(true));
        return drawable;
    }

    public CColorDrawable(int solidColor) {
        this();
        setSolidColor(solidColor);
    }


    public CColorDrawable(int strokeColor, int strokeWidth) {
        this();
        setStrokeColor(strokeColor);
        setStrokeWidth(strokeWidth);
    }


    public CColorDrawable() {
        strokeWidth = CanvasUtils.x(LibContext.getContext(), 2);
        strokeSpace = CanvasUtils.x(LibContext.getContext(), 6);

        solidPaint = new Paint();
        solidPaint.setAntiAlias(true);
        solidPaint.setColor(solidColor);
        solidPaint.setStyle(Paint.Style.FILL);

        strokePaint = new Paint();
        strokePaint.setAntiAlias(true);
        strokePaint.setColor(strokeColor);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(strokeWidth);
    }

    public CColorDrawable setSolidColor(int solidColor){
        this.solidColor = solidColor;
        solidPaint.setColor(solidColor);
        invalidateSelf();
        return this;
    }

    public CColorDrawable setStrokeColor(int c) {
        strokeColor = c;
        strokePaint.setColor(strokeColor);
        invalidateSelf();
        return this;
    }

    public CColorDrawable setStrokeWidth(int width) {
        strokeWidth = width;
        strokePaint.setStrokeWidth(strokeWidth);
        invalidateSelf();
        return this;
    }

    public CColorDrawable setRadius(float radius) {
        this.radius = radius;
        this.strokeRadius = radius;
        invalidateSelf();
        return this;
    }

    public boolean isRingFlag() {
        return ringFlag;
    }

    public CColorDrawable setRingFlag(boolean ringFlag) {
        this.ringFlag = ringFlag;
        invalidateSelf();
        return this;
    }



    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        width = height = right - left;
        radius = width / 2;
        strokeWidth = width / 20;
        if (strokeWidth == 0) {
            strokeWidth = CanvasUtils.x(LibContext.getContext(), 2);
        }
        strokeRadius = (width - strokeWidth) / 2;

        solidRadius = width * 12 / 20 / 2;
        if(solidRadius == 0){
            solidRadius = CanvasUtils.x(LibContext.getContext(), 6);
        }
        invalidateSelf();
    }

    @Override
    public void draw(Canvas canvas) {

        if(ringFlag){
            canvas.drawCircle(width / 2, height / 2, solidRadius, solidPaint);
            canvas.drawCircle(width / 2, height / 2, strokeRadius, strokePaint);
        }else {
            canvas.drawCircle(width / 2, height / 2, radius, solidPaint);
        }

    }

    @Override
    public void setAlpha(int alpha) {
        solidPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        solidPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }


}
