package com.whty.eschoolbag.draft;

import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/6/26 0026.
 */

public class CanvasRecord {

    private static CanvasRecord record;

    private Map<String, NoteBean> recordMap = new HashMap<>();

    public static synchronized CanvasRecord getRecord() {
        if (record == null) {
            record = new CanvasRecord();
        }
        return record;
    }

    public void setRecord(String key, NoteBean value) {
        Log.d("CanvasRecord", "setRecord: " + key);
//        recordMap.clear();
        recordMap.put(key, value);
    }

    public NoteBean getRecord(String key) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        return recordMap.get(key);
    }

    public void remove(String key) {
        if (recordMap.get(key) != null) {
            recordMap.remove(key);
        }
    }

    public boolean isValue(String key) {
        Log.d("CanvasRecord", "isValue: " + key);
//        Log.d("CanvasRecord", "isValue: value size=" + recordMap.get(key).size());
        if (TextUtils.isEmpty(key)) {
            return false;
        }
        if (recordMap.get(key) == null) {
            return false;
        }
        return true;
    }

    public void release() {
        Log.d("CanvasRecord", "release: ");
        recordMap.clear();
    }

}
