package com.whty.eschoolbag.draft;

/**
 * Created by tianqs on 2018/1/15.
 */

public interface OnCanvasCallback {
    void onSingleTap();

    void onDownDispear();

    void setUndoEnable(boolean enable);
    void setRecoverEnable(boolean enable);
}
