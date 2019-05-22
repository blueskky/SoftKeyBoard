package com.whty.eschoolbag.draft;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;

import static com.whty.eschoolbag.draft.CanvasConstant.STROKE_WIDTHS;


public class PopCanvasStroke {

    ImageView ivWidth1;
    ImageView ivWidth2;
    ImageView ivWidth3;


    private PopupWindow popupWindow;
    private View popView;

    private Context mContext;

    private View viewRoot;


    public PopCanvasStroke(Context context) {
        mContext = context;
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popView = layoutInflater.inflate(R.layout.canvas_pop_stroke_width, null);
        popupWindow = new PopupWindow(popView, ViewUtil.y(mContext, (int) (206 * 1.2)), ViewUtil.y(mContext, (int) (86 * 1.2)));
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        viewRoot = popView.findViewById(R.id.layout_root);

        ivWidth1 = (ImageView) popView.findViewById(R.id.iv_width1);
        ivWidth2 = (ImageView) popView.findViewById(R.id.iv_width2);
        ivWidth3 = (ImageView) popView.findViewById(R.id.iv_width3);


//        TQSViewUtil.size_y(mContext, 186, 234, viewRoot);
        ViewUtil.padding_y(mContext, (int) (6 * 1.2), (int) (6 * 1.2), (int) (6 * 1.2), (int) (18 * 1.2), viewRoot);

        ImageView[] ivWidths = {ivWidth3, ivWidth2, ivWidth1};

        for (int i = 0, len = ivWidths.length; i < len; i++) {
            final int width = STROKE_WIDTHS[i];
            final ImageView tempIvWidth = ivWidths[i];
            final int index = i;
            tempIvWidth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setWidthSelect(index);
                    if (canvasCallback != null) {
                        canvasCallback.onWidth(width);
                    }
                }
            });

        }
    }

    public void setWidthSelect(int index) {
        ImageView[] ivWidths = {ivWidth3, ivWidth2, ivWidth1};
        for (int i = 0; i < ivWidths.length; i++) {
            if (index == i) {
                ivWidths[i].setSelected(true);
            } else {
                ivWidths[i].setSelected(false);
            }
        }
    }

    public interface OnCanvasCallback {
        void onWidth(int width);
    }

    private OnCanvasCallback canvasCallback;

    public void setOnCanvasCallback(OnCanvasCallback callback) {
        canvasCallback = callback;
    }

    public void showPopupWindow(View parent) {
        popupWindow.showAsDropDown(parent, -ViewUtil.y(mContext, (int) (247 / 2)) + parent.getWidth() / 2, 10);
    }

    public void dismiss(){
        popupWindow.dismiss();
    }
}
