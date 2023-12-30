package com.pigeoff.menu.util;

import android.app.Activity;

public class BackgroundTask<T> {
    Activity activity;
    OnPreProcess preProcessListener;
    OnProcess<T> processListener;
    OnSuccess<T> successListener;
    OnFailure failureListener;

    public BackgroundTask(Activity activity, OnProcess<T> listener) {
        this.activity = activity;
        this.processListener = listener;
    }

    public BackgroundTask<T> addSuccessListener(OnSuccess<T> listener) {
        this.successListener = listener;
        return this;
    }

    public BackgroundTask<T> addFailureListener(OnFailure listener) {
        this.failureListener = listener;
        return this;
    }

    public BackgroundTask<T> addPreProcessListener(OnPreProcess listener) {
        this.preProcessListener = listener;
        return this;
    }

    public void start() {
        if (this.preProcessListener != null) this.preProcessListener.onPreProcess();
        new Thread(() -> {
            try {
                T result = this.processListener.onProcessListener();
                if (this.successListener != null) {
                    this.activity.runOnUiThread(() -> this.successListener.onSuccess(result));
                }
            } catch (Exception e) {
                if (this.failureListener != null)
                    this.activity.runOnUiThread(() -> this.failureListener.onFailure(e));
            }
        }).start();
    }

    // Interfaces
    public interface OnPreProcess {
        void onPreProcess();
    }
    public interface OnProcess<T> {
        T onProcessListener() throws Exception;
    }

    public interface OnSuccess<T> {
        void onSuccess(T result);
    }

    public interface OnFailure {
        void onFailure(Exception exception);
    }

}
