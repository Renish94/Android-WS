/*
 *
 *  *    Copyright (C) 2018 Renish Patel
 *  *    Copyright (C) 2011 Android Open Source Project
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 *
 */

package com.android.aws.internal;

import com.android.aws.common.AWSConstants;
import com.android.aws.common.AWSRequest;
import com.android.aws.common.AWSResponse;
import com.android.aws.common.ResponseType;
import com.android.aws.error.AWSError;
import com.android.aws.utils.SourceCloseUtil;
import com.android.aws.utils.Utils;

import okhttp3.Response;

import static com.android.aws.common.RequestType.DOWNLOAD;
import static com.android.aws.common.RequestType.MULTIPART;
import static com.android.aws.common.RequestType.SIMPLE;

@SuppressWarnings("unchecked")
public final class SynchronousCall {

    private SynchronousCall() {

    }

    public static <T> AWSResponse<T> execute(AWSRequest request) {
        switch (request.getRequestType()) {
            case SIMPLE:
                return executeSimpleRequest(request);
            case DOWNLOAD:
                return executeDownloadRequest(request);
            case MULTIPART:
                return executeUploadRequest(request);
        }
        return new AWSResponse<>(new AWSError());
    }

    private static <T> AWSResponse<T> executeSimpleRequest(AWSRequest request) {
        Response okHttpResponse = null;
        try {
            okHttpResponse = InternalNetworking.performSimpleRequest(request);
            if (okHttpResponse == null) {
                return new AWSResponse<>(Utils.getErrorForConnection(new AWSError()));
            }

            if (request.getResponseAs() == ResponseType.OK_HTTP_RESPONSE) {
                AWSResponse response = new AWSResponse(okHttpResponse);
                response.setOkHttpResponse(okHttpResponse);
                return response;
            }
            if (okHttpResponse.code() >= 400) {
                AWSResponse response = new AWSResponse<>(Utils.getErrorForServerResponse(new AWSError(okHttpResponse),
                        request, okHttpResponse.code()));
                response.setOkHttpResponse(okHttpResponse);
                return response;
            }
            AWSResponse response = request.parseResponse(okHttpResponse);
            response.setOkHttpResponse(okHttpResponse);
            return response;
        } catch (AWSError se) {
            return new AWSResponse<>(Utils.getErrorForConnection(new AWSError(se)));
        } catch (Exception e) {
            return new AWSResponse<>(Utils.getErrorForConnection(new AWSError(e)));
        } finally {
            SourceCloseUtil.close(okHttpResponse, request);
        }
    }

    private static <T> AWSResponse<T> executeDownloadRequest(AWSRequest request) {
        Response okHttpResponse;
        try {
            okHttpResponse = InternalNetworking.performDownloadRequest(request);
            if (okHttpResponse == null) {
                return new AWSResponse<>(Utils.getErrorForConnection(new AWSError()));
            }
            if (okHttpResponse.code() >= 400) {
                AWSResponse response = new AWSResponse<>(Utils.getErrorForServerResponse(new AWSError(okHttpResponse),
                        request, okHttpResponse.code()));
                response.setOkHttpResponse(okHttpResponse);
                return response;
            }
            AWSResponse response = new AWSResponse(AWSConstants.SUCCESS);
            response.setOkHttpResponse(okHttpResponse);
            return response;
        } catch (AWSError se) {
            return new AWSResponse<>(Utils.getErrorForConnection(new AWSError(se)));
        } catch (Exception e) {
            return new AWSResponse<>(Utils.getErrorForConnection(new AWSError(e)));
        }
    }

    private static <T> AWSResponse<T> executeUploadRequest(AWSRequest request) {
        Response okHttpResponse = null;
        try {
            okHttpResponse = InternalNetworking.performUploadRequest(request);

            if (okHttpResponse == null) {
                return new AWSResponse<>(Utils.getErrorForConnection(new AWSError()));
            }

            if (request.getResponseAs() == ResponseType.OK_HTTP_RESPONSE) {
                AWSResponse response = new AWSResponse(okHttpResponse);
                response.setOkHttpResponse(okHttpResponse);
                return response;
            }
            if (okHttpResponse.code() >= 400) {
                AWSResponse response = new AWSResponse<>(Utils.getErrorForServerResponse(new AWSError(okHttpResponse),
                        request, okHttpResponse.code()));
                response.setOkHttpResponse(okHttpResponse);
                return response;
            }
            AWSResponse response = request.parseResponse(okHttpResponse);
            response.setOkHttpResponse(okHttpResponse);
            return response;
        } catch (AWSError se) {
            return new AWSResponse<>(Utils.getErrorForConnection(se));
        } catch (Exception e) {
            return new AWSResponse<>(Utils.getErrorForConnection(new AWSError(e)));
        } finally {
            SourceCloseUtil.close(okHttpResponse, request);
        }
    }
}
