package com.android.application;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.android.aws.AWS;
import com.android.aws.common.Priority;
import com.android.aws.error.AWSError;
import com.android.aws.interfaces.AnalyticsListener;
import com.android.aws.interfaces.StringRequestListener;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_call_aws).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                call_aws();
            }
        });
        call_aws();
    }

    private void call_aws() {
        AWS.cancel(this);
        AWS.get("https://jsonplaceholder.typicode.com/posts/{id}")
                .addPathParameter("id", "1")
                .setTag(this)
                .setPriority(Priority.LOW)
                .doNotCacheResponse()
                .build()
                .setAnalyticsListener(new AnalyticsListener() {
                    @Override
                    public void onReceived(long timeTakenInMillis, long bytesSent, long bytesReceived, boolean isFromCache) {
                        Log.d(TAG, " timeTakenInMillis : " + timeTakenInMillis);
                        Log.d(TAG, " bytesSent : " + bytesSent);
                        Log.d(TAG, " bytesReceived : " + bytesReceived);
                        Log.d(TAG, " isFromCache : " + isFromCache);
                    }
                })
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: " + response);
                    }

                    @Override
                    public void onError(AWSError error) {
                        if (error.getErrorCode() != 0) {
                            // received AWSError from server

                            // error.getErrorCode() - the AWSError code from server
                            Log.d(TAG, "onError errorCode : " + error.getErrorCode());

                            // error.getErrorBody() - the AWSError body from server
                            Log.d(TAG, "onError errorBody : " + error.getErrorBody());

                            // error.getErrorDetail() - just a AWSError detail
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                        } else {
                            // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                        }
                    }
                });

        AWS.post("https://jsonplaceholder.typicode.com/posts")
                .addBodyParameter("userId", "11")
                .addBodyParameter("id", "101")
                .addBodyParameter("title", "this is test title")
                .addBodyParameter("body", "this is test body")
                .setTag(this)
                .setPriority(Priority.HIGH)
                .build()
                .setAnalyticsListener(new AnalyticsListener() {
                    @Override
                    public void onReceived(long timeTakenInMillis, long bytesSent, long bytesReceived, boolean isFromCache) {
                        Log.d(TAG, " timeTakenInMillis : " + timeTakenInMillis);
                        Log.d(TAG, " bytesSent : " + bytesSent);
                        Log.d(TAG, " bytesReceived : " + bytesReceived);
                        Log.d(TAG, " isFromCache : " + isFromCache);
                    }
                })
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: " + response);
                    }

                    @Override
                    public void onError(AWSError error) {
                        if (error.getErrorCode() != 0) {
                            // received AWSError from server

                            // error.getErrorCode() - the AWSError code from server
                            Log.d(TAG, "onError errorCode : " + error.getErrorCode());

                            // error.getErrorBody() - the AWSError body from server
                            Log.d(TAG, "onError errorBody : " + error.getErrorBody());

                            // error.getErrorDetail() - just a AWSError detail
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                        } else {
                            // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                        }
                    }
                });
    }
}
