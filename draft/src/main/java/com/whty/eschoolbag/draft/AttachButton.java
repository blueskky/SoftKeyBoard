package com.whty.eschoolbag.draft;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;


/**
 * 自定义View实现拖动并自动吸边效果
 * <p>
 * 处理滑动和贴边 {@link #onTouchEvent(MotionEvent)}
 * 处理事件分发 {@link #dispatchTouchEvent(MotionEvent)}
 * </p>
 *
 * @attr customIsAttach  //是否需要自动吸边
 * @attr customIsDrag    //是否可拖曳
 */
public class AttachButton extends FrameLayout {
    private float mLastRawX;
    private float mLastRawY;
    private final String TAG = "AttachButton";
    private boolean isDrug = false;
    private int mRootMeasuredWidth = 0;
    private int mRootMeasuredHeight = 0;
    private int mRootTopY = 0;
    private boolean customIsAttach;
    private boolean customIsDrag;
    private View attachView;

    public AttachButton(Context context) {
        this(context, null);
    }

    public AttachButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AttachButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);


        attachView = LayoutInflater.from(context).inflate(R.layout.left_attach_button, null);
        addView(attachView);

        initAttrs(context, attrs);

    }

    /**
     * 初始化自定义属性
     */
    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray mTypedAttay = context.obtainStyledAttributes(attrs, R.styleable.AttachButton);
        customIsAttach = mTypedAttay.getBoolean(R.styleable.AttachButton_customIsAttach, true);
        customIsDrag = mTypedAttay.getBoolean(R.styleable.AttachButton_customIsDrag, true);
        mTypedAttay.recycle();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        super.dispatchTouchEvent(event);
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //判断是否需要滑动
        if (customIsDrag) {
            //当前手指的坐标
            float mRawX = ev.getRawX();
            float mRawY = ev.getRawY();
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN://手指按下
                    isDrug = false;
                    //记录按下的位置
                    mLastRawX = mRawX;
                    mLastRawY = mRawY;
                    ViewGroup mViewGroup = (ViewGroup) getParent();
                    if (mViewGroup != null) {
                        int[] location = new int[2];
                        mViewGroup.getLocationInWindow(location);
                        //获取父布局的高度
                        mRootMeasuredHeight = mViewGroup.getMeasuredHeight();
                        mRootMeasuredWidth = mViewGroup.getMeasuredWidth();
                        //获取父布局顶点的坐标
                        mRootTopY = location[1];
                    }
                    break;
                case MotionEvent.ACTION_MOVE://手指滑动
                    if (mRawX >= 0 && mRawX <= mRootMeasuredWidth && mRawY >= mRootTopY && mRawY <= (mRootMeasuredHeight + mRootTopY)) {
                        //手指X轴滑动距离
                        float differenceValueX = mRawX - mLastRawX;
                        //手指Y轴滑动距离
                        float differenceValueY = mRawY - mLastRawY;
                        //判断是否为拖动操作
                        if (!isDrug) {
                            if (Math.sqrt(differenceValueX * differenceValueX + differenceValueY * differenceValueY) < 2) {
                                isDrug = false;
                            } else {
                                isDrug = true;
                            }
                        }
                        //获取手指按下的距离与控件本身X轴的距离
                        float ownX = getX();
                        //获取手指按下的距离与控件本身Y轴的距离
                        float ownY = getY();
                        //理论中X轴拖动的距离
                        float endX = ownX + differenceValueX;
                        //理论中Y轴拖动的距离
                        float endY = ownY + differenceValueY;
                        //X轴可以拖动的最大距离
                        float maxX = mRootMeasuredWidth - getWidth();
                        //Y轴可以拖动的最大距离
                        float maxY = mRootMeasuredHeight - getHeight();
                        //X轴边界限制
                        endX = endX < 0 ? 0 : endX > maxX ? maxX : endX;
                        //Y轴边界限制
                        endY = endY < 0 ? 0 : endY > maxY ? maxY : endY;
                        //开始移动
                        setX(endX);
                        setY(endY);
                        //记录位置
                        mLastRawX = mRawX;
                        mLastRawY = mRawY;
                    }

                    break;
                case MotionEvent.ACTION_UP://手指离开
                    //根据自定义属性判断是否需要贴边
                    if (customIsAttach) {
                        //判断是否为点击事件
                        if (isDrug) {
                            float center = mRootMeasuredWidth / 2;
                            //自动贴边
                            if (mLastRawX <= center) {
                                attachView.setBackgroundResource(R.drawable.left_attach);
                                //向左贴边
                                AttachButton.this.animate()
                                        .setInterpolator(new BounceInterpolator())
                                        .setDuration(500)
                                        .x(0)
                                        .start();
                            } else {
                                //向右贴边
                                attachView.setBackgroundResource(R.drawable.right_attach);
                                AttachButton.this.animate()
                                        .setInterpolator(new BounceInterpolator())
                                        .setDuration(500)
                                        .x(mRootMeasuredWidth - getWidth())
                                        .start();
                            }
                        }
                    }
                    break;
            }
        }
        //是否拦截事件
        return isDrug ? isDrug : super.onTouchEvent(ev);
    }
}