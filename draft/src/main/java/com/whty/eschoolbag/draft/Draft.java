package com.whty.eschoolbag.draft;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class Draft {

    private static final String TAG = "draft";
    private AttachButton attachButton;
    private FrameLayout contentView;
    private Activity context;

    private Draft() {
    }

    public static Draft getInstance() {
        return new Draft();
    }


    public void register(final Activity context) {
        this.context = context;
        addLifeListener(context);
    }

    private void addLifeListener(Activity activity) {
        LifeListenerFragment fragment = getLifeListenerFragment(activity);
        fragment.addLifeListener(mLifeListener);
    }

    private LifeListenerFragment getLifeListenerFragment(Activity activity) {
        FragmentManager manager = activity.getFragmentManager();
        LifeListenerFragment fragment = (LifeListenerFragment) manager.findFragmentByTag(TAG);
        if (fragment == null) {
            fragment = new LifeListenerFragment();
            manager.beginTransaction().add(fragment, TAG).commitAllowingStateLoss();
        }
        return fragment;
    }


    private LifeListener mLifeListener = new LifeListener() {

        @Override
        public void onCreate() {
            Log.d("lhq", "onCreate");
            addDraftView();
        }

        @Override
        public void onStart() {
        }

        @Override
        public void onResume() {
        }

        @Override
        public void onPause() {
        }

        @Override
        public void onStop() {
        }

        @Override
        public void onDestroy() {
            removeDraftView();
        }
    };


    private void addDraftView() {
        contentView = (FrameLayout) context.findViewById(android.R.id.content);

        attachButton = new AttachButton(context);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;

        contentView.addView(attachButton, lp);


        attachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CanvasActivity.launchForResult(context, 100);
            }
        });
    }

    private void removeDraftView() {
        if (contentView != null)
            contentView.removeView(attachButton);
    }


}
