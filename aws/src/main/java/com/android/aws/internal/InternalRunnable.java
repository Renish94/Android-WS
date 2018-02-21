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

package com.android.aws.internal;

import com.android.aws.common.AWSRequest;
import com.android.aws.common.AWSResponse;
import com.android.aws.common.Priority;
import com.android.aws.common.ResponseType;
import com.android.aws.core.Core;
import com.android.aws.error.AWSError;
import com.android.aws.utils.SourceCloseUtil;
import com.android.aws.utils.Utils;

import okhttp3.Response;

import static com.android.aws.common.RequestType.DOWNLOAD;
import static com.android.aws.common.RequestType.MULTIPART;
import static com.android.aws.common.RequestType.SIMPLE;

public class InternalRunnable implements Runnable {

    private final Priority priority;
    public final int sequence;
    public final AWSRequest request;

    public InternalRunnable(AWSRequest request) {
        this.request = request;
        this.sequence = request.getSequenceNumber();
        this.priority = request.getPriority();
    }

    @Override
    public void run() {
        request.setRunning(true);
        switch (request.getRequestType()) {
            case SIMPLE:
                executeSimpleRequest();
                break;
            case DOWNLOAD:
                executeDownloadRequest();
                break;
            case MULTIPART:
                executeUploadRequest();
                break;
        }
        request.setRunning(false);
    }

    private void executeSimpleRequest() {
        Response okHttpResponse = null;
        try {
            okHttpResponse = InternalNetworking.performSimpleRequest(request);

            if (okHttpResponse == null) {
                deliverError(request, Utils.getErrorForConnection(new AWSError()));
                return;
            }

            if (request.getResponseAs() == ResponseType.OK_HTTP_RESPONSE) {
                request.deliverOkHttpResponse(okHttpResponse);
                return;
            }
            if (okHttpResponse.code() >= 400) {
                deliverError(request, Utils.getErrorForServerResponse(new AWSError(okHttpResponse),
                        request, okHttpResponse.code()));
                return;
            }

            AWSResponse response = request.parseResponse(okHttpResponse);
            if (!response.isSuccess()) {
                deliverError(request, response.getError());
                return;
            }
            response.setOkHttpResponse(okHttpResponse);
            request.deliverResponse(response);
        } catch (Exception e) {
            deliverError(request, Utils.getErrorForConnection(new AWSError(e)));
        } finally {
            SourceCloseUtil.close(okHttpResponse, request);
        }
    }

    private void executeDownloadRequest() {
        Response okHttpResponse;
        try {
            okHttpResponse = InternalNetworking.performDownloadRequest(request);
            if (okHttpResponse == null) {
                deliverError(request, Utils.getErrorForConnection(new AWSError()));
                return;
            }
            if (okHttpResponse.code() >= 400) {
                deliverError(request, Utils.getErrorForServerResponse(new AWSError(okHttpResponse),
                        request, okHttpResponse.code()));
                return;
            }
            request.updateDownloadCompletion();
        } catch (Exception e) {
            deliverError(request, Utils.getErrorForConnection(new AWSError(e)));
        }
    }

    private void executeUploadRequest() {
        Response okHttpResponse = null;
        try {
            okHttpResponse = InternalNetworking.performUploadRequest(request);

            if (okHttpResponse == null) {
                deliverError(request, Utils.getErrorForConnection(new AWSError()));
                return;
            }

            if (request.getResponseAs() == ResponseType.OK_HTTP_RESPONSE) {
                request.deliverOkHttpResponse(okHttpResponse);
                return;
            }

            if (okHttpResponse.code() >= 400) {
                deliverError(request, Utils.getErrorForServerResponse(new AWSError(okHttpResponse),
                        request, okHttpResponse.code()));
                return;
            }
            AWSResponse response = request.parseResponse(okHttpResponse);
            if (!response.isSuccess()) {
                deliverError(request, response.getError());
                return;
            }
            response.setOkHttpResponse(okHttpResponse);
            request.deliverResponse(response);
        } catch (Exception e) {
            deliverError(request, Utils.getErrorForConnection(new AWSError(e)));
        } finally {
            SourceCloseUtil.close(okHttpResponse, request);
        }
    }

    public Priority getPriority() {
        return priority;
    }

    private void deliverError(final AWSRequest request, final AWSError awsError) {
        Core.getInstance().getExecutorSupplier().forMainThreadTasks().execute(new Runnable() {
            public void run() {
                request.deliverError(awsError);
                request.finish();
            }
        });
    }
}
