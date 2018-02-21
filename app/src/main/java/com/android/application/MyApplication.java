package com.android.application;

import android.app.Application;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.android.aws.AWS;
import com.android.aws.common.ConnectionQuality;
import com.android.aws.interfaces.ConnectionQualityChangeListener;

public class MyApplication extends Application {

    private static final String TAG = MyApplication.class.getSimpleName();
    private static MyApplication appInstance = null;

    public static MyApplication getInstance() {
        return appInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        appInstance = this;

        AWS.initialize(getApplicationContext());
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        AWS.setBitmapDecodeOptions(options);
        AWS.enableLogging();
        AWS.removeConnectionQualityChangeListener();
        AWS.setConnectionQualityChangeListener(new ConnectionQualityChangeListener() {
            @Override
            public void onChange(ConnectionQuality currentConnectionQuality, int currentBandwidth) {
                Log.d(TAG, "onChange: currentConnectionQuality : " + currentConnectionQuality + " currentBandwidth : " + currentBandwidth);
            }
        });
    }
}