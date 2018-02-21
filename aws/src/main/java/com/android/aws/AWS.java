/*
 *    Copyright (C) 2018 Renish Patel
 *    Copyright (C) 2011 Android Open Source Project
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.android.aws;

import android.content.Context;
import android.graphics.BitmapFactory;

import com.android.aws.common.AWSConstants;
import com.android.aws.common.AWSRequest;
import com.android.aws.common.ConnectionClassManager;
import com.android.aws.common.ConnectionQuality;
import com.android.aws.core.Core;
import com.android.aws.interceptors.HttpLoggingInterceptor.Level;
import com.android.aws.interfaces.ConnectionQualityChangeListener;
import com.android.aws.interfaces.Parser;
import com.android.aws.internal.AWSImageLoader;
import com.android.aws.internal.AWSRequestQueue;
import com.android.aws.internal.InternalNetworking;
import com.android.aws.utils.ParseUtil;
import com.android.aws.utils.Utils;

import okhttp3.OkHttpClient;

/**
 * AWS entry point.
 * You must initialize this class before use. The simplest way is to just do
 * {#code AWS.initialize(context)}.
 */
@SuppressWarnings("unused")
public class AWS {

    /**
     * private constructor to prevent instantiation of this class
     */
    private AWS() {
    }

    /**
     * Initializes AWS with the default config.
     *
     * @param context The context
     */
    public static void initialize(Context context) {
        InternalNetworking.setClientWithCache(context.getApplicationContext());
        AWSRequestQueue.initialize();
        AWSImageLoader.initialize();
    }

    /**
     * Initializes AWS with the specified config.
     *
     * @param context      The context
     * @param okHttpClient The okHttpClient
     */
    public static void initialize(Context context, OkHttpClient okHttpClient) {
        if (okHttpClient != null && okHttpClient.cache() == null) {
            okHttpClient = okHttpClient
                    .newBuilder()
                    .cache(Utils.getCache(context.getApplicationContext(),
                            AWSConstants.MAX_CACHE_SIZE, AWSConstants.CACHE_DIR_NAME))
                    .build();
        }
        InternalNetworking.setClient(okHttpClient);
        AWSRequestQueue.initialize();
        AWSImageLoader.initialize();
    }

    /**
     * Method to set decodeOptions
     *
     * @param decodeOptions The decode config for Bitmaps
     */
    public static void setBitmapDecodeOptions(BitmapFactory.Options decodeOptions) {
        if (decodeOptions != null) {
            AWSImageLoader.getInstance().setBitmapDecodeOptions(decodeOptions);
        }
    }

    /**
     * Method to set connectionQualityChangeListener
     *
     * @param connectionChangeListener The connectionQualityChangeListener
     */
    public static void setConnectionQualityChangeListener(ConnectionQualityChangeListener connectionChangeListener) {
        ConnectionClassManager.getInstance().setListener(connectionChangeListener);
    }

    /**
     * Method to set connectionQualityChangeListener
     */
    public static void removeConnectionQualityChangeListener() {
        ConnectionClassManager.getInstance().removeListener();
    }

    /**
     * Method to make GET request
     *
     * @param url The url on which request is to be made
     * @return The GetRequestBuilder
     */
    public static AWSRequest.GetRequestBuilder get(String url) {
        return new AWSRequest.GetRequestBuilder(url);
    }

    /**
     * Method to make HEAD request
     *
     * @param url The url on which request is to be made
     * @return The HeadRequestBuilder
     */
    public static AWSRequest.HeadRequestBuilder head(String url) {
        return new AWSRequest.HeadRequestBuilder(url);
    }

    /**
     * Method to make OPTIONS request
     *
     * @param url The url on which request is to be made
     * @return The OptionsRequestBuilder
     */
    public static AWSRequest.OptionsRequestBuilder options(String url) {
        return new AWSRequest.OptionsRequestBuilder(url);
    }

    /**
     * Method to make POST request
     *
     * @param url The url on which request is to be made
     * @return The PostRequestBuilder
     */
    public static AWSRequest.PostRequestBuilder post(String url) {
        return new AWSRequest.PostRequestBuilder(url);
    }

    /**
     * Method to make PUT request
     *
     * @param url The url on which request is to be made
     * @return The PutRequestBuilder
     */
    public static AWSRequest.PutRequestBuilder put(String url) {
        return new AWSRequest.PutRequestBuilder(url);
    }

    /**
     * Method to make DELETE request
     *
     * @param url The url on which request is to be made
     * @return The DeleteRequestBuilder
     */
    public static AWSRequest.DeleteRequestBuilder delete(String url) {
        return new AWSRequest.DeleteRequestBuilder(url);
    }

    /**
     * Method to make PATCH request
     *
     * @param url The url on which request is to be made
     * @return The PatchRequestBuilder
     */
    public static AWSRequest.PatchRequestBuilder patch(String url) {
        return new AWSRequest.PatchRequestBuilder(url);
    }

    /**
     * Method to make download request
     *
     * @param url      The url on which request is to be made
     * @param dirPath  The directory path on which file is to be saved
     * @param fileName The file name with which file is to be saved
     * @return The DownloadBuilder
     */
    public static AWSRequest.DownloadBuilder download(String url, String dirPath, String fileName) {
        return new AWSRequest.DownloadBuilder(url, dirPath, fileName);
    }

    /**
     * Method to make upload request
     *
     * @param url The url on which request is to be made
     * @return The MultiPartBuilder
     */
    public static AWSRequest.MultiPartBuilder upload(String url) {
        return new AWSRequest.MultiPartBuilder(url);
    }

    /**
     * Method to make Dynamic request
     *
     * @param url    The url on which request is to be made
     * @param method The HTTP METHOD for the request
     * @return The DynamicRequestBuilder
     */
    public static AWSRequest.DynamicRequestBuilder request(String url, int method) {
        return new AWSRequest.DynamicRequestBuilder(url, method);
    }

    /**
     * Method to cancel requests with the given tag
     *
     * @param tag The tag with which requests are to be cancelled
     */
    public static void cancel(Object tag) {
        AWSRequestQueue.getInstance().cancelRequestWithGivenTag(tag, false);
    }

    /**
     * Method to force cancel requests with the given tag
     *
     * @param tag The tag with which requests are to be cancelled
     */
    public static void forceCancel(Object tag) {
        AWSRequestQueue.getInstance().cancelRequestWithGivenTag(tag, true);
    }

    /**
     * Method to cancel all given request
     */
    public static void cancelAll() {
        AWSRequestQueue.getInstance().cancelAll(false);
    }

    /**
     * Method to force cancel all given request
     */
    public static void forceCancelAll() {
        AWSRequestQueue.getInstance().cancelAll(true);
    }

    /**
     * Method to enable logging
     */
    public static void enableLogging() {
        enableLogging(Level.BASIC);
    }

    /**
     * Method to enable logging with tag
     *
     * @param level The level for logging
     */
    public static void enableLogging(Level level) {
        InternalNetworking.enableLogging(level);
    }

    /**
     * Method to evict a bitmap with given key from AWSCache
     *
     * @param key The key of the bitmap
     */
    public static void evictBitmap(String key) {
        final AWSImageLoader.ImageCache imageCache = AWSImageLoader.getInstance().getImageCache();
        if (imageCache != null && key != null) {
            imageCache.evictBitmap(key);
        }
    }

    /**
     * Method to clear AWSCache
     */
    public static void evictAllBitmap() {
        final AWSImageLoader.ImageCache imageCache = AWSImageLoader.getInstance().getImageCache();
        if (imageCache != null) {
            imageCache.evictAllBitmap();
        }
    }

    /**
     * Method to set userAgent globally
     *
     * @param userAgent The userAgent
     */
    public static void setUserAgent(String userAgent) {
        InternalNetworking.setUserAgent(userAgent);
    }

    /**
     * Method to get currentBandwidth
     *
     * @return currentBandwidth
     */
    public static int getCurrentBandwidth() {
        return ConnectionClassManager.getInstance().getCurrentBandwidth();
    }

    /**
     * Method to get currentConnectionQuality
     *
     * @return currentConnectionQuality
     */
    public static ConnectionQuality getCurrentConnectionQuality() {
        return ConnectionClassManager.getInstance().getCurrentConnectionQuality();
    }

    /**
     * Method to set ParserFactory
     *
     * @param parserFactory The ParserFactory
     */
    public static void setParserFactory(Parser.Factory parserFactory) {
        ParseUtil.setParserFactory(parserFactory);
    }

    /**
     * Method to find if the request is running or not
     *
     * @param tag The tag with which request running status is to be checked
     * @return The request is running or not
     */
    public static boolean isRequestRunning(Object tag) {
        return AWSRequestQueue.getInstance().isRequestRunning(tag);
    }

    /**
     * Shuts AWS down
     */
    public static void shutDown() {
        Core.shutDown();
        evictAllBitmap();
        ConnectionClassManager.getInstance().removeListener();
        ConnectionClassManager.shutDown();
        ParseUtil.shutDown();
    }
}
