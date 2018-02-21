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

package com.android.aws.error;

import com.android.aws.common.AWSConstants;
import com.android.aws.utils.ParseUtil;

import okhttp3.Response;

@SuppressWarnings({"unchecked", "unused"})
public class AWSError extends Exception {

    private String errorBody;

    private int errorCode = 0;

    private String errorDetail;

    private Response response;

    public AWSError() {
    }

    public AWSError(Response response) {
        this.response = response;
    }

    public AWSError(String message) {
        super(message);
    }

    public AWSError(String message, Response response) {
        super(message);
        this.response = response;
    }

    public AWSError(String message, Throwable throwable) {
        super(message, throwable);
    }

    public AWSError(String message, Response response, Throwable throwable) {
        super(message, throwable);
        this.response = response;
    }

    public AWSError(Response response, Throwable throwable) {
        super(throwable);
        this.response = response;
    }

    public AWSError(Throwable throwable) {
        super(throwable);
    }

    public Response getResponse() {
        return response;
    }

    public void setErrorDetail(String errorDetail) {
        this.errorDetail = errorDetail;
    }

    public String getErrorDetail() {
        return this.errorDetail;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public void setCancellationMessageInError() {
        this.errorDetail = AWSConstants.REQUEST_CANCELLED_ERROR;
    }

    public String getErrorBody() {
        return errorBody;
    }

    public void setErrorBody(String errorBody) {
        this.errorBody = errorBody;
    }

    public <T> T getErrorAsObject(Class<T> objectClass) {
        try {
            return (T) (ParseUtil
                    .getParserFactory()
                    .getObject(errorBody, objectClass));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
