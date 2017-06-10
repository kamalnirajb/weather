package com.weatherapp.http;

import android.app.Activity;
import android.app.ProgressDialog;

/**
 * Created by NirajKumar on 5/1/17.
 */

public class AppLoader {

    private static final String TAG = "AppLoader";
    private ProgressDialog progress;
    private Activity activity;

    public AppLoader(Activity activity) {
        this.activity = activity;
    }
    
    public  void showLoader(final String message) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progress = new ProgressDialog(activity);
                progress.setMessage(message);
                progress.setCancelable(false);
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setIndeterminate(true);
                progress.show();
            }
        });
    }

    public  void hideLoader() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(progress != null) {
                    progress.dismiss();
                }
            }
        });
    }
}
