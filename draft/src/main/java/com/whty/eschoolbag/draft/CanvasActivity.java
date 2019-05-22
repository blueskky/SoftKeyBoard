package com.whty.eschoolbag.draft;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import static com.whty.eschoolbag.draft.CanvasConstant.DEFAULT_COLOR;
import static com.whty.eschoolbag.draft.CanvasConstant.DEFAULT_COLOR_INDEX;
import static com.whty.eschoolbag.draft.CanvasConstant.DEFAULT_STROKE_WIDTH;
import static com.whty.eschoolbag.draft.CanvasConstant.DEFAULT_STROKE_WIDTH_INDEX;
import static com.whty.eschoolbag.draft.CanvasConstant.STROKE_WIDTH_MIDDLE;
import static com.whty.eschoolbag.draft.CanvasConstant.STROKE_WIDTH_THICK;
import static com.whty.eschoolbag.draft.CanvasConstant.STROKE_WIDTH_THIN;
import static com.whty.eschoolbag.draft.CanvasUtils.saveInOI;


/**
 * 作业展示主界面
 *
 * @author chen
 */
public class CanvasActivity extends Activity {
    private static final String FRAG_INDEX = "fragment_index";
    private static final String IS_RECORD = "is_record";
    private String TAG = "CanvasActivity";

    public static final String PICK_RESOURCE = "PICK_RESOURCE";
    public static final String LAST_RESOURCE = "LAST_RESOURCE";

    RelativeLayout layoutBottom;

    ImageView viewLine;

    ImageView ivColor;

    View viewDivider;
    ImageView ivUndo;
    ImageView ivRedo;
    ImageView ivClean;

    LinearLayout layoutSave;
    ImageView ivSave;
    TextView btnSave;

    RelativeLayout backLayout;

    RelativeLayout penStatusLayout;

    RobotCanvasView canvasView;

    private int noteW, noteH;

    private String lastPath;

    private Activity mInstance;

    //罗博笔后台监听注册key
    private String key;
    private ImageView ivClose;
    private int index;
    private boolean isRecord;


    public static void launch(Activity context, int currentItem, boolean isReodrd) {
        int writePermission = PermissionChecker.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (writePermission != PermissionChecker.PERMISSION_GRANTED) {
            Toast.makeText(context, "该功能需要存储权限,请打开存储权限", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(context, CanvasActivity.class);
        intent.putExtra(FRAG_INDEX, currentItem);
        intent.putExtra(IS_RECORD, isReodrd);
        context.startActivity(intent);
    }

    public static void launchForResult(Activity context, int requestCode) {
        int writePermission = PermissionChecker.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (writePermission != PermissionChecker.PERMISSION_GRANTED) {
            Toast.makeText(context, "该功能需要存储权限,请打开存储权限", Toast.LENGTH_SHORT).show();
            return;
        }
        context.startActivityForResult(new Intent(context, CanvasActivity.class), requestCode);
    }


    protected void initView() {

        ivClose = findViewById(R.id.iv_close);
        canvasView = findViewById(R.id.canvas_view);
        layoutBottom = findViewById(R.id.layout_bottom);
        viewLine = findViewById(R.id.view_line);
        ivColor = findViewById(R.id.iv_color);
        viewDivider = findViewById(R.id.view_divider);
        ivUndo = findViewById(R.id.iv_undo);

        ivRedo = findViewById(R.id.iv_redo);
        ivClean = findViewById(R.id.iv_clean);

        layoutSave = findViewById(R.id.layout_save);
        ivSave = findViewById(R.id.view_save);

        btnSave = findViewById(R.id.btn_save);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setLayout(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.canvas_draft_activity);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);


        initView();
        mInstance = this;
        LibContext.onCreate(mInstance);

        index = getIntent().getIntExtra(FRAG_INDEX, 0);
        isRecord = getIntent().getBooleanExtra(IS_RECORD, false);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float noteBookW = displayMetrics.widthPixels;
        float noteBookH = displayMetrics.heightPixels;

        initCanvasView(noteBookW, noteBookH);


        canvasView.setOnCallback(new OnCanvasCallback() {
            @Override
            public void onSingleTap() {

            }

            @Override
            public void onDownDispear() {

            }

            @Override
            public void setUndoEnable(boolean enable) {
                ivUndo.setEnabled(enable);
                layoutSave.setEnabled(enable);
                ivSave.setEnabled(enable);
                btnSave.setEnabled(enable);
                ivClean.setEnabled(enable);
            }

            @Override
            public void setRecoverEnable(boolean enable) {
                ivRedo.setEnabled(enable);
            }
        });

        canvasView.setPaintWidth(DEFAULT_STROKE_WIDTH);
        viewLine.setBackgroundResource(R.drawable.canvas_stroke_width2_press);
        ivColor.setImageDrawable(CColorDrawable.build(Color.parseColor(DEFAULT_COLOR)).setRingFlag(true));

        viewLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopCanvasStroke(v);
            }
        });

        ivColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopCanvasColor(v);
            }
        });

        ivUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canvasView.undo();
            }
        });
        ivUndo.setEnabled(false);

        ivRedo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canvasView.recover();
            }
        });
        ivRedo.setEnabled(false);

        ivClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canvasView.clear();
            }
        });

        layoutSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(canvasView.isEmpty()){   // 画布是空的
//                    Toast.makeText(mInstance, "画布是空的哦", Toast.LENGTH_SHORT);
//                }
                Bitmap bitmap = getNoteThumb();
                save2File(CanvasActivity.this, bitmap);
            }
        });

        layoutSave.setEnabled(false);
        ivSave.setEnabled(false);
        btnSave.setEnabled(false);

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        CanvasUtils.size_y(mInstance, 0, 72, layoutBottom);
        CanvasUtils.size_y(mInstance, 60, 60, viewLine);
        CanvasUtils.margins_y(mInstance, 48, 0, 0, 0, viewLine);
        CanvasUtils.size_y(mInstance, 60, 60, ivColor);
        CanvasUtils.margins_y(mInstance, 36, 0, 0, 0, ivColor);
        CanvasUtils.padding_y(mInstance, 12, 12, 12, 12, ivColor);
        CanvasUtils.size_y(mInstance, 60, 60, ivUndo);
        CanvasUtils.margins_y(mInstance, 36, 0, 0, 0, ivUndo);
        CanvasUtils.size_y(mInstance, 60, 60, ivRedo);
        CanvasUtils.margins_y(mInstance, 36, 0, 0, 0, ivRedo);
        CanvasUtils.size_y(mInstance, 60, 60, ivClean);
        CanvasUtils.margins_y(mInstance, 36, 0, 0, 0, ivClean);

        CanvasUtils.size_y(mInstance, 2, 44, viewDivider);
        CanvasUtils.margins_y(mInstance, 24, 0, 24, 0, viewDivider);


        CanvasUtils.size_y(mInstance, 60, 60, ivSave);

        CanvasUtils.font(mInstance, 24, btnSave);
        CanvasUtils.margins_y(mInstance, 6, 0, 36, 0, btnSave);

//        CanvasUtils.size(this, 96, 96, findViewById(R.id.pen_status_layout));
//        CanvasUtils.size(this, 96, 96, findViewById(R.id.robot_icon_iv));
//        CanvasUtils.size(this, 12, 12, findViewById(R.id.robot_state_v));
//        CanvasUtils.margins(this, 0, 32, 32, 0, findViewById(R.id.pen_status_layout));
//        CanvasUtils.margins(this, 0, 12, 0, 0, findViewById(R.id.robot_state_v));


        key = System.currentTimeMillis() + "";

        if (isRecord) {
            NoteBean noteBean = CanvasRecord.getRecord().getRecord(String.valueOf(index));
            if (noteBean != null) {
                canvasView.loadPage(noteBean.getPaths(), noteBean.getRecoverPaths());
            }
        }


    }

    private PopCanvasColor popCanvasColor;

    private void showPopCanvasColor(View v) {
        if (popCanvasColor == null) {
            popCanvasColor = new PopCanvasColor(this);
            popCanvasColor.setColorSelect(DEFAULT_COLOR_INDEX);
            popCanvasColor.setOnCanvasCallback(new PopCanvasColor.OnCanvasCallback() {
                @Override
                public void onColor(String color) {
                    popCanvasColor.dismiss();
                    canvasView.setPaintColor(color);
                    ivColor.setImageDrawable(CColorDrawable.build(Color.parseColor(color)).setRingFlag(true));
                }
            });
        }
        popCanvasColor.showPopupWindow(v);
    }

    private PopCanvasStroke popCanvasStroke;

    private void showPopCanvasStroke(View v) {
        if (popCanvasStroke == null) {
            popCanvasStroke = new PopCanvasStroke(this);
            popCanvasStroke.setWidthSelect(DEFAULT_STROKE_WIDTH_INDEX);
            popCanvasStroke.setOnCanvasCallback(new PopCanvasStroke.OnCanvasCallback() {
                @Override
                public void onWidth(int width) {
                    popCanvasStroke.dismiss();
                    canvasView.setPaintWidth(width);
                    setViewLine(width);
                }
            });
        }
        popCanvasStroke.showPopupWindow(v);
    }

    private void setViewLine(int width) {
        switch (width) {
            case STROKE_WIDTH_THIN:
                viewLine.setBackgroundResource(R.drawable.canvas_stroke_width3_press);
                break;
            case STROKE_WIDTH_MIDDLE:
                viewLine.setBackgroundResource(R.drawable.canvas_stroke_width2_press);
                break;
            case STROKE_WIDTH_THICK:
                viewLine.setBackgroundResource(R.drawable.canvas_stroke_width1_press);
                break;
        }
    }

    private void initCanvasView(float noteBookW, float noteBookH) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int w = displayMetrics.widthPixels;
        int h = displayMetrics.heightPixels;
        float rateWH = (float) w / h;
        float rateWHBmp = noteBookW / noteBookH;

        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) canvasView.getLayoutParams();

        if (rateWHBmp >= rateWH) {
            layoutParams.width = w;
            layoutParams.height = (int) (w / rateWHBmp);
        } else {
            layoutParams.height = h;
            layoutParams.width = (int) (h * rateWHBmp);
        }

        noteW = layoutParams.width;
        noteH = layoutParams.height;

        canvasView.setLayoutParams(layoutParams);

        canvasView.initParams((float) noteW / layoutParams.width, noteW, noteH);
    }

    public Bitmap getNoteThumb() {
        canvasView.setScale(1f);
        findViewById(R.id.robot_container).setDrawingCacheEnabled(true);
        Bitmap bitmap = findViewById(R.id.robot_container).getDrawingCache();
        if (bitmap != null) {
            int dstW = 1280;
            int dstH = dstW * bitmap.getHeight() / bitmap.getWidth();
            bitmap = Bitmap.createScaledBitmap(bitmap, dstW, dstH, false);
        }
        findViewById(R.id.robot_container).destroyDrawingCache();
        return bitmap;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (isRecord) {
            NoteBean noteBean = new NoteBean(index);
            noteBean.setPaths(canvasView.getPaths());
            noteBean.setRecoverPaths(canvasView.getRecoverPaths());
            CanvasRecord.getRecord().setRecord(String.valueOf(index), noteBean);
        }

        if (canvasView != null) {
            canvasView.release();
        }
    }


    public void save2File(Context context, final Bitmap bitmap) {
        new saveToFileTask(context).execute(bitmap);
    }


    class saveToFileTask extends AsyncTask<Bitmap, Void, File> {

        Context mContext;
        AlertDialog dialog;


        public saveToFileTask(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new AlertDialog.Builder(mContext)
                    .setTitle("保存画板")
                    .setMessage("保存中...")
                    .show();
        }

        @Override
        protected File doInBackground(Bitmap... bitmaps) {
            return saveInOI(bitmaps[0]);
        }

        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            if (file != null && file.exists()) {
//                Toast.makeText(mContext, file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                String filePath = file.getPath();
                Log.v(TAG, "filePath = " + filePath);

                insertImage(CanvasActivity.this, filePath);

//                NoteBean noteBean = new NoteBean(filePath);
//                noteBean.setPaths(canvasView.getPaths());
//                noteBean.setRecoverPaths(canvasView.getRecoverPaths());
//                CanvasRecord.getRecord().setRecord(filePath, noteBean);

                Intent intent = new Intent();
                intent.putExtra(LAST_RESOURCE, lastPath);
                intent.putExtra(PICK_RESOURCE, filePath);
                setResult(RESULT_OK, intent);
                finish();
            } else {
                Toast.makeText(mContext, "保存失败！", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        }
    }


    public static void insertImage(Context context, String path) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
//        其次把文件插入到系统图库
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(new File(path));
        intent.setData(uri);
        context.sendBroadcast(intent);

        Log.d("FileUtils", "insertImage: over  " + path);
    }


}
  