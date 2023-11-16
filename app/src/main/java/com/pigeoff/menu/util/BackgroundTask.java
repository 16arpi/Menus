package com.pigeoff.menu.util;

import android.app.Activity;

public class BackgroundTask {

    public interface DoInBackground {
        void onStart();
        void onFinish();
        void onFailure(Exception e);
    }


    public BackgroundTask(Activity activity, DoInBackground callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    callback.onStart();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFinish();
                        }
                    });
                } catch (Exception e) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(e);
                        }
                    });
                }
            }
        }).start();
    }
}
