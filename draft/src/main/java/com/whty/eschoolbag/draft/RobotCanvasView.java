package com.whty.eschoolbag.draft;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tianqs on 2018/3/1.
 */

public class RobotCanvasView extends FrameLayout {
    private static final String TAG = RobotCanvasView.class.getSimpleName();
    private Context mContext;
    // 当前画板的Paint
    private Paint mPaint;
    private Paint eraserPaint;

    private Bitmap markBitmap;
    private Canvas markCanvas;    // 画笔画布

    // 当前画板颜色
    private String mPaintColor;
    private int mStrokeWidth = 4;

    private float initScale = 1.0f, totalScale;
    private float mTransX = 0, mTransY = 0;
    private int mOriginalWidth, mOriginalHeight;

    // 是否正在画笔
    private boolean mIsPainting;
    // 所有操作队列
    private List<RobotPath> mPathStack = new ArrayList<RobotPath>();
    // 撤销笔记存放的List
    private List<RobotPath> mRecoverStack = new ArrayList<RobotPath>();

    // 可视操作区域
    private Rect screenRect = new Rect();
    private Path mCurrPath;

    //用于点击事件
    private GestureDetector mGestureDetector;
    private OnCanvasCallback mCallback;

    private Handler mHandler;

    public RobotCanvasView(Context context) {
        this(context, null);
    }

    public RobotCanvasView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RobotCanvasView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        init();

        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                Log.v(TAG, "onSingleTapConfirmed()");
                if (mCallback != null) {
                    mCallback.onSingleTap();
                }
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                Log.v(TAG, "onDoubleTap() x = " + e.getX() + ",y = " + e.getY());
                if (totalScale == mMinScale) {
                    mToucheCentreXOnGraffiti = toX(e.getX());
                    mToucheCentreYOnGraffiti = toY(e.getY());
                    totalScale = mMaxScale;
                    float transX = toTransX(e.getX(), mToucheCentreXOnGraffiti);
                    float transY = toTransY(e.getY(), mToucheCentreYOnGraffiti);

                    setTrans(transX, transY);


                } else {
                    totalScale = mMinScale;
                    mTransX = 0;
                    mTransY = 0;
                }

                invalidate();

                return super.onDoubleTap(e);
            }
        });
    }

    private void init() {
        // 初始化笔迹画笔
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaintColor = "#000000";
        mPaint.setColor(Color.parseColor(mPaintColor));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(mStrokeWidth / initScale);// penWidth/scale
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setDither(true);

        eraserPaint = new Paint();
        eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        eraserPaint.setStrokeWidth(ViewUtil.x(mContext, 48));
        eraserPaint.setAntiAlias(true);
        eraserPaint.setStyle(Paint.Style.STROKE);
        eraserPaint.setStrokeJoin(Paint.Join.ROUND);
        eraserPaint.setStrokeCap(Paint.Cap.ROUND);

    }

    public void setOnCallback(OnCanvasCallback callback) {
        mCallback = callback;
    }

    public void release() {
        if (markBitmap != null && !markBitmap.isRecycled()) {
            markBitmap.recycle();
            markBitmap = null;
        }

        mPathStack.clear();
        mRecoverStack.clear();
    }

    public void initParams(float initScale, float originalWidth, float originalHeight) {
        Log.d(TAG, "initParams: initScale=" + initScale + " originalWidth=" + originalWidth + " originalHeight=" + originalHeight);
        this.initScale = initScale;
        this.mMinScale = initScale;
        this.mMaxScale = mMinScale * 4;
        mOriginalWidth = (int) originalWidth;
        mOriginalHeight = (int) originalHeight;

        initScreenRect();
    }

    private void initScreenRect() {
        totalScale = mMinScale;

        mTransX = 0;
        mTransY = 0;

        initMarkCanvas();

        if (mPathStack.size() > 0) {
            for (RobotPath path : mPathStack) {
                mPaint.setColor(Color.parseColor(path.getColor()));
                mPaint.setStrokeWidth(path.getStrokeWidth() / initScale);
                markCanvas.drawPath(path, mPaint);
            }
        }

        mPaint.setColor(Color.parseColor(mPaintColor));
        mPaint.setStrokeWidth(mStrokeWidth / initScale);

        screenRect.left = 0;
        screenRect.top = 0;
        screenRect.right = (int) (screenRect.left + mOriginalWidth);
        screenRect.bottom = (int) (screenRect.top + mOriginalHeight);

        invalidate();
    }


    private void initMarkCanvas() {
        if (markBitmap != null) {
            if (!markBitmap.isRecycled()) {
                markBitmap.recycle();
            }
            markBitmap = null;
        }
        markBitmap = Bitmap.createBitmap((int) mOriginalWidth, (int) mOriginalHeight, Bitmap.Config.ARGB_4444);
        markCanvas = new Canvas(markBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (markBitmap == null || markBitmap.isRecycled() || markBitmap.isRecycled()) {
            Log.d(TAG, "onDraw: sourceBitmap=null");
            return;
        }
        canvas.save();
        drawBitmap(canvas);
        canvas.restore();
    }


    private void drawBitmap(Canvas canvas) {
        float left = (/*mCentreTranX + */mTransX) / (/*initScale * */totalScale);
        float top = (/*mCentreTranY + */mTransY) / (/*initScale **/ totalScale);
        // 画布和图片共用一个坐标系，只需要处理屏幕坐标系到图片（画布）坐标系的映射关系
        canvas.scale(/*initScale * */totalScale, /*initScale **/ totalScale); // 缩放画布
        canvas.translate(left, top); // 偏移画布
        canvas.save();

        // 绘制涂鸦
        canvas.drawBitmap(markBitmap, 0, 0, null);

        if (mIsPainting && mCurrPath != null) {  //画在view的画布上
            canvas.drawPath(mCurrPath, mPaint);
        }

        canvas.restore();
    }

    private int mTouchMode;
    private float mOldScale, mOldDist, mNewDist, mToucheCentreXOnGraffiti,
            mToucheCentreYOnGraffiti, mTouchCentreX, mTouchCentreY;// 双指距离
    boolean mIsBusy = false;

    private float mMaxScale = 4.0f; // 最大缩放倍数
    private float mMinScale = 1.0f; // 最小缩放倍数

    private float mTouchDownX, mTouchDownY, mLastTouchX, mLastTouchY, mTouchX, mTouchY;
    private RobotPath mTmpPath; // 当前手写的路径

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.v("fortest", "pointer count:" + event.getPointerCount() + "，action:" + (event.getAction() & MotionEvent.ACTION_MASK));

//        if (mGestureDetector.onTouchEvent(event)) {
//            return true;
//        }

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mTouchMode = 1;
                mTouchDownX = mTouchX = mLastTouchX = event.getX();
                mTouchDownY = mTouchY = mLastTouchY = event.getY();

//                mTmpPath = new Path();
                mTmpPath = new RobotPath();
                mTmpPath.setColor(mPaintColor);
                mTmpPath.setStrokeWidth(mStrokeWidth);
                mTmpPath.moveTo(toX(mTouchDownX), toY(mTouchDownY));
                mIsPainting = true;

//                setIsPainting(mIsPainting);
                setCurrPath(mTmpPath);


                invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mTouchMode = 0;
                if (mTouchMode < 2 && !mIsBusy) {
                    mLastTouchX = mTouchX;
                    mLastTouchY = mTouchY;
                    mTouchX = event.getX();
                    mTouchY = event.getY();
                    // 为了仅点击时也能出现绘图，必须移动path
                    if (mTouchDownX == mTouchX && mTouchDownY == mTouchY & mTouchDownX == mLastTouchX && mTouchDownY == mLastTouchY) {
                        break;
                    }
                    if (mIsPainting) {
                        RobotPath path = null;
                        // 把操作记录到加入的堆栈中
                        mTmpPath.quadTo(
                                toX(mLastTouchX),
                                toY(mLastTouchY),
                                toX((mTouchX + mLastTouchX) / 2),
                                toY((mTouchY + mLastTouchY) / 2));
                        addPath(mTmpPath);
                        draw(mTmpPath); // 保存到图片中
                        mIsPainting = false;
                    }

                    invalidate();
                }

                break;
            case MotionEvent.ACTION_MOVE:
                if (mTouchMode < 2) {
                    if (mIsBusy) { // 从多点触摸变为单点触摸，忽略该次事件，避免从双指缩放变为单指移动时图片瞬间移动
                        mIsBusy = false;
                        mTouchDownX = mTouchX = mLastTouchX = event.getX();
                        mTouchDownY = mTouchY = mLastTouchY = event.getY();
                        mTmpPath.reset();
                        mTmpPath.moveTo(toX(mTouchDownX), toY(mTouchDownY));
                        mIsPainting = true;
//                        setIsPainting(mIsPainting);
                        setCurrPath(mTmpPath);
                        break;
                    }

                    mLastTouchX = mTouchX;
                    mLastTouchY = mTouchY;
                    mTouchX = event.getX();
                    mTouchY = event.getY();


                    mTmpPath.quadTo(
                            toX(mLastTouchX),
                            toY(mLastTouchY),
                            toX((mTouchX + mLastTouchX) / 2),
                            toY((mTouchY + mLastTouchY) / 2));
                    invalidate();

                } else {
                    mNewDist = spacing(event);// 两点滑动时的距离
                    if (Math.abs(mNewDist - mOldDist) > 20) {
                        float scale = mNewDist / mOldDist;
                        float mScale = mOldScale * scale;

                        if (mScale > mMaxScale) {
                            mScale = mMaxScale;
                        }
                        if (mScale < mMinScale) { // 最小倍数
                            mScale = mMinScale;
                        }
                        // 围绕坐标(0,0)缩放图片
                        setScale(mScale);
                        // 缩放后，偏移图片，以产生围绕某个点缩放的效果
                        float transX = toTransX(mTouchCentreX, mToucheCentreXOnGraffiti);
                        float transY = toTransY(mTouchCentreY, mToucheCentreYOnGraffiti);
                        setTrans(transX, transY);
                    } else {
                        float touchCentreX = (event.getX(0) + event.getX(1)) / 2;
                        float touchCentreY = (event.getY(0) + event.getY(1)) / 2;

                        float tranX = touchCentreX - mTouchCentreX;
                        float tranY = touchCentreY - mTouchCentreY;
                        setTrans(getTransX() + tranX, getTransY() + tranY);

                        mTouchCentreX = touchCentreX;
                        mTouchCentreY = touchCentreY;

                        mOldDist = spacing(event);// 两点按下时的距离
                        mToucheCentreXOnGraffiti = toX(mTouchCentreX);
                        mToucheCentreYOnGraffiti = toY(mTouchCentreY);
                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mTouchMode -= 1;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mTouchMode += 1;
                mOldScale = getScale();
                mOldDist = spacing(event);// 两点按下时的距离
                mTouchCentreX = (event.getX(0) + event.getX(1)) / 2;// 不用减trans
                mTouchCentreY = (event.getY(0) + event.getY(1)) / 2;
                mToucheCentreXOnGraffiti = toX(mTouchCentreX);
                mToucheCentreYOnGraffiti = toY(mTouchCentreY);
                mIsBusy = true; // 标志位多点触摸
                mIsPainting = false;
//                setIsPainting(mIsPainting);
                break;
            default:
                break;
        }

        return true;
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 将屏幕触摸坐标x转换成在图片中的坐标
     */
    public final float toX(float touchX) {
        return (touchX /*- mCentreTranX*/ - mTransX) / (/*initScale * */totalScale);
    }

    /**
     * 将屏幕触摸坐标y转换成在图片中的坐标
     */
    public final float toY(float touchY) {
        return (touchY /*- mCentreTranY*/ - mTransY) / (/*initScale **/ totalScale);
    }

    /**
     * 坐标换算
     * （公式由toX()中的公式推算出）
     *
     * @param touchX    触摸坐标
     * @param graffitiX 在涂鸦图片中的坐标
     * @return 偏移量
     */
    public float toTransX(float touchX, float graffitiX) {
        return -graffitiX * (/*initScale * */totalScale) + touchX /*- mCentreTranX*/;
    }

    public float toTransY(float touchY, float graffitiY) {
        return -graffitiY * (/*initScale * */totalScale) + touchY /*- mCentreTranY*/;
    }

    public float getScale() {
        return totalScale;
    }

    public float getTransX() {
        return mTransX;
    }

    public float getTransY() {
        return mTransY;
    }

    /**
     * 调整图片位置
     * <p>
     * 明白下面一点很重要：
     * 假设不考虑任何缩放，图片就是肉眼看到的那么大，此时图片的大小width =  mPrivateWidth * mScale ,
     * 偏移量x = mCentreTranX + mTransX，而view的大小为width = getWidth()。height和偏移量y以此类推。
     */
    private void judgePosition() {
        if (mOriginalWidth * totalScale <= screenRect.width()) { // 限制在screenRect范围内
            mTransX = 0;
        } else { // 限制在view范围外
            if (mTransX /*+ mCentreTranX*/ > screenRect.left) {
                mTransX = 0;
            } else if (mTransX /*+ mCentreTranX*/ + mOriginalWidth * totalScale < screenRect.right) {
                mTransX = screenRect.right /*- mCentreTranX*/ - mOriginalWidth * totalScale;
            }
        }
        if (mOriginalHeight * totalScale <= screenRect.height()) { // 限制在screenRect范围内
            mTransY = 0;
        } else { // 限制在screenRect范围外
            if (mTransY /*+ mCentreTranY*/ > screenRect.top) {
                mTransY = 0;
            } else if (mTransY /*+ mCentreTranY*/ + mOriginalHeight * totalScale < screenRect.bottom) {
                mTransY = screenRect.bottom /*- mCentreTranY*/ - mOriginalHeight * totalScale;
            }
        }
    }

    public void setScale(float scale) {
        this.totalScale = scale;
        judgePosition();
        invalidate();
    }

    public void setTrans(float transX, float transY) {
        mTransX = transX;
        mTransY = transY;
        Log.v(TAG, "setTrans() mTransX = " + mTransX + ",mTransY = " + mTransY);
        judgePosition();
        invalidate();
    }

    public void setCurrPath(Path path) {
        mCurrPath = path;
    }

    public void addPath(RobotPath path) {
        mPathStack.add(path);
        if (mCallback != null) {
            mCallback.setUndoEnable(true);
        }
    }

    public void draw(RobotPath path) {
        mPaint.setStrokeWidth(path.getStrokeWidth() / initScale);
        mPaint.setColor(Color.parseColor(path.getColor()));
        markCanvas.drawPath(path, mPaint);
    }


    public void setPaintColor(String color) {
        mPaintColor = color;
        mPaint.setColor(Color.parseColor(mPaintColor));
    }

    public void setPaintWidth(int width) {
        mStrokeWidth = width;
        mPaint.setStrokeWidth(width / initScale);
    }

    public List<RobotPath> getPaths() {
        return mPathStack;
    }

    public List<RobotPath> getRecoverPaths() {
        return mRecoverStack;
    }

    public void loadPage(List<RobotPath> list, List<RobotPath> recoverList) {
        setCurrPath(null);
        mPathStack.clear();
        mRecoverStack.clear();
        if (list != null && !list.isEmpty()) {
            mPathStack.addAll(list);
        }
        if (recoverList != null && !recoverList.isEmpty()) {
            mRecoverStack.addAll(recoverList);
        }

        if (markCanvas != null) {
            markCanvas.drawPaint(eraserPaint);
        }

        mTransX = 0;
        mTransY = 0;

        if (mPathStack.size() > 0) {
            for (RobotPath path : mPathStack) {
                mPaint.setColor(Color.parseColor(path.getColor()));
                mPaint.setStrokeWidth(path.getStrokeWidth());
                markCanvas.drawPath(path, mPaint);
            }
        }

        mPaint.setColor(Color.parseColor(mPaintColor));
        mPaint.setStrokeWidth(mStrokeWidth);

        screenRect.left = 0;
        screenRect.top = 0;
        screenRect.right = screenRect.left + mOriginalWidth;
        screenRect.bottom = screenRect.top + mOriginalHeight;

        if (mPathStack.size() > 0) {
            if (mCallback != null) {
                mCallback.setUndoEnable(true);
            }
        }else {
            if (mCallback != null) {
                mCallback.setUndoEnable(false);
            }
        }

        if (mRecoverStack.size() > 0) {
            if (mCallback != null) {
                mCallback.setRecoverEnable(true);
            }
        }else {
            if (mCallback != null) {
                mCallback.setRecoverEnable(false);
            }
        }

        invalidate();
    }

    public void undo() {
        if (markCanvas != null) {
            markCanvas.drawPaint(eraserPaint);
        }

        if (mPathStack != null && mPathStack.size() > 0) {
            mRecoverStack.add(mPathStack.get(mPathStack.size() - 1));
            mPathStack.remove(mPathStack.size() - 1);
            if (mPathStack.size() > 0) {
                for (RobotPath path : mPathStack) {
                    mPaint.setColor(Color.parseColor(path.getColor()));
                    mPaint.setStrokeWidth(path.getStrokeWidth());
                    markCanvas.drawPath(path, mPaint);
                }
            }

            invalidate();

        }

        mPaint.setColor(Color.parseColor(mPaintColor));
        mPaint.setStrokeWidth(mStrokeWidth);

        if (mPathStack.size() <= 0) {
            if (mCallback != null) {
                mCallback.setUndoEnable(false);
            }
        }

        if (mRecoverStack.size() > 0) {
            if (mCallback != null) {
                mCallback.setRecoverEnable(true);
            }
        }

    }

    public void recover() {
        if (markCanvas != null) {
            markCanvas.drawPaint(eraserPaint);
        }

        if (mRecoverStack != null && mRecoverStack.size() > 0) {
            mPathStack.add(mRecoverStack.get(mRecoverStack.size() - 1));
            mRecoverStack.remove(mRecoverStack.size() - 1);
            if (mPathStack.size() > 0) {
                for (RobotPath path : mPathStack) {
                    mPaint.setColor(Color.parseColor(path.getColor()));
                    mPaint.setStrokeWidth(path.getStrokeWidth());
                    markCanvas.drawPath(path, mPaint);
                }
            }

            invalidate();

        }

        mPaint.setColor(Color.parseColor(mPaintColor));
        mPaint.setStrokeWidth(mStrokeWidth);

        if (mPathStack.size() > 0) {
            if (mCallback != null) {
                mCallback.setUndoEnable(true);
            }
        }

        if (mRecoverStack.size() <= 0) {
            if (mCallback != null) {
                mCallback.setRecoverEnable(false);
            }
        }

    }

    public void clear() {
        if (markCanvas != null) {
            markCanvas.drawPaint(eraserPaint);
        }
        setCurrPath(null);
        invalidate();
        mPathStack.clear();
        mRecoverStack.clear();
        if (mCallback != null) {
            mCallback.setUndoEnable(false);
            mCallback.setRecoverEnable(false);
        }
    }

    public void resetInitScale() {
        totalScale = mMinScale;
        mTransX = 0;
        mTransY = 0;
        invalidate();
    }


}
