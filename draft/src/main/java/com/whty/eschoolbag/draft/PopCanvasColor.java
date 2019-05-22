package com.whty.eschoolbag.draft;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;

import static com.whty.eschoolbag.draft.CanvasConstant.COLORS;


public class PopCanvasColor {

    ImageView ivColor0;
    ImageView ivColor1;
    ImageView ivColor2;
    ImageView ivColor3;
    ImageView ivColor4;
    ImageView ivColor5;
    ImageView ivColor6;
    ImageView ivColor7;
    ImageView ivColor8;
    ImageView ivColor9;
    ImageView ivColor10;
    ImageView ivColor11;

    private PopupWindow popupWindow;
    private View popView;

    private Context mContext;

    private View viewRoot;


    public PopCanvasColor(Context context) {
        mContext = context;
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popView = layoutInflater.inflate(R.layout.canvas_pop_stroke_color, null);
        popupWindow = new PopupWindow(popView, ViewUtil.y(mContext, (int) (186 * 1.2)), ViewUtil.y(mContext, (int) (234 * 1.2)));
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        viewRoot = popView.findViewById(R.id.layout_root);

        ivColor0 = (ImageView) popView.findViewById(R.id.iv_color0);
        ivColor1 = (ImageView) popView.findViewById(R.id.iv_color1);
        ivColor2 = (ImageView) popView.findViewById(R.id.iv_color2);
        ivColor3 = (ImageView) popView.findViewById(R.id.iv_color3);
        ivColor4 = (ImageView) popView.findViewById(R.id.iv_color4);
        ivColor5 = (ImageView) popView.findViewById(R.id.iv_color5);
        ivColor6 = (ImageView) popView.findViewById(R.id.iv_color6);
        ivColor7 = (ImageView) popView.findViewById(R.id.iv_color7);
        ivColor8 = (ImageView) popView.findViewById(R.id.iv_color8);
        ivColor9 = (ImageView) popView.findViewById(R.id.iv_color9);
        ivColor10 = (ImageView) popView.findViewById(R.id.iv_color10);
        ivColor11 = (ImageView) popView.findViewById(R.id.iv_color11);


//        TQSViewUtil.size_y(mContext, 186, 234, viewRoot);
        ViewUtil.padding_y(mContext, (int) (20 * 1.2), (int) (12 * 1.2), (int) (20 * 1.2), (int) (24 * 1.2), viewRoot);

        ImageView[] ivColors = {ivColor0, ivColor1, ivColor2, ivColor3, ivColor4, ivColor5, ivColor6, ivColor7, ivColor8, ivColor9, ivColor10, ivColor11};

        for (int i = 0, len = ivColors.length; i < len; i++) {
            final String color = COLORS[i];
            final ImageView tempIvColor = ivColors[i];
            tempIvColor.setImageDrawable(CColorDrawable.create(Color.parseColor(color)));
            ViewUtil.padding_y(mContext, 7, 7, 7, 7, tempIvColor);
//            tempIvColor.setBackgroundColor(Color.parseColor(color));
            final int index = i;
            tempIvColor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setColorSelect(index);

                    if (canvasCallback != null) {
                        canvasCallback.onColor(color);
                    }
                }
            });

        }
    }

    public void setColorSelect(int index) {
        View[] ivColors = {ivColor0, ivColor1, ivColor2, ivColor3, ivColor4, ivColor5, ivColor6, ivColor7, ivColor8, ivColor9, ivColor10, ivColor11};
        for (int i = 0; i < ivColors.length; i++) {
            if (index == i) {
                ivColors[i].setSelected(true);
            } else {
                ivColors[i].setSelected(false);
            }
        }
    }

    public interface OnCanvasCallback {
        void onColor(String color);
    }

    private OnCanvasCallback canvasCallback;

    public void setOnCanvasCallback(OnCanvasCallback callback) {
        canvasCallback = callback;
    }

    public void showPopupWindow(View parent) {
        popupWindow.showAsDropDown(parent, -ViewUtil.y(mContext, (int) (224 / 2)) + parent.getWidth() / 2, 10);
    }

    public void dismiss(){
        popupWindow.dismiss();
    }
}
