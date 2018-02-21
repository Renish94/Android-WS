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

package com.android.aws.common;

import com.android.aws.error.AWSError;

import okhttp3.Response;

public class AWSResponse<T> {

    private final T mResult;

    private final AWSError awsError;

    private Response response;

    public static <T> AWSResponse<T> success(T result) {
        return new AWSResponse<>(result);
    }

    public static <T> AWSResponse<T> failed(AWSError awsError) {
        return new AWSResponse<>(awsError);
    }

    public AWSResponse(T result) {
        this.mResult = result;
        this.awsError = null;
    }

    public AWSResponse(AWSError awsError) {
        this.mResult = null;
        this.awsError = awsError;
    }

    public T getResult() {
        return mResult;
    }

    public boolean isSuccess() {
        return awsError == null;
    }

    public AWSError getError() {
        return awsError;
    }

    public void setOkHttpResponse(Response response) {
        this.response = response;
    }

    public Response getOkHttpResponse() {
        return response;
    }

}
