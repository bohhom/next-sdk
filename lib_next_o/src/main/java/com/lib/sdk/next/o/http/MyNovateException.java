/*
Copyright 2021 kino jin
zhikai.jin@bozhon.com
This file is part of next-sdk.
next-sdk is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.
next-sdk is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.
You should have received a copy of the GNU Lesser General Public License
along with next-sdk.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.lib.sdk.next.o.http;

import android.content.Context;
import android.text.TextUtils;

import com.lib.sdk.next.o.R;
import com.tamic.novate.Throwable;
import com.tamic.novate.exception.NovateException;
import com.tamic.novate.util.LogWraper;

/**
 * Created by aruba on 2020/9/23.
 */
public class MyNovateException {
    private static final int UNAUTHORIZED = 401;
    private static final int FORBIDDEN = 403;
    private static final int NOT_FOUND = 404;
    private static final int REQUEST_TIMEOUT = 408;
    private static final int INTERNAL_SERVER_ERROR = 500;
    private static final int BAD_GATEWAY = 502;
    private static final int SERVICE_UNAVAILABLE = 503;
    private static final int GATEWAY_TIMEOUT = 504;
    private static final int ACCESS_DENIED = 302;
    private static final int HANDEL_ERRROR = 417;

    public static Throwable handleException(Context context, Throwable e) {

        LogWraper.e("Novate", e.getMessage());
        String detail = "";
        if (e.getCause() != null) {
            detail = e.getCause().getMessage();
        }
        LogWraper.e("Novate", detail);
        Throwable ex;
        ex = new Throwable(e, e.getCode());
        switch (ex.getCode()) {
            case UNAUTHORIZED:
                ex.setMessage(context.getString(R.string.unauthorized));
                break;
            case FORBIDDEN:
                ex.setMessage(context.getString(R.string.unauthorized));
                break;
            case NOT_FOUND:
                ex.setMessage(context.getString(R.string.not_found));
                break;
            case REQUEST_TIMEOUT:
                ex.setMessage(context.getString(R.string.request_timeout));
                break;
            case GATEWAY_TIMEOUT:
                ex.setMessage(context.getString(R.string.unauthorized));
                break;
            case INTERNAL_SERVER_ERROR:
                ex.setMessage(context.getString(R.string.unauthorized));
            case BAD_GATEWAY:
                ex.setMessage(context.getString(R.string.bad_gateway));
                break;
            case SERVICE_UNAVAILABLE:
                ex.setMessage(context.getString(R.string.unauthorized));
                break;
            case ACCESS_DENIED:
                ex.setMessage(context.getString(R.string.not_found));
                break;
            case HANDEL_ERRROR:
                ex.setMessage(context.getString(R.string.unauthorized));
                break;
            case NovateException.ERROR.PARSE_ERROR:
                ex.setMessage(context.getString(R.string.parse_error));
                break;
            case NovateException.ERROR.NETWORD_ERROR:
                ex.setMessage(context.getString(R.string.not_found));
                break;
            case NovateException.ERROR.SSL_ERROR:
                ex.setMessage(context.getString(R.string.unauthorized));
                break;

            case NovateException.ERROR.SSL_NOT_FOUND:
                ex.setMessage(context.getString(R.string.unauthorized));
                break;
            case NovateException.ERROR.TIMEOUT_ERROR:
                ex.setMessage(context.getString(R.string.request_timeout));
                break;
            case NovateException.ERROR.FORMAT_ERROR:
                ex.setMessage(context.getString(R.string.unauthorized));
                break;
            case NovateException.ERROR.NULL:
                ex.setMessage(context.getString(R.string.unauthorized));
                break;
            default:
                if (TextUtils.isEmpty(ex.getMessage())) {
                    ex.setMessage(e.getMessage());
                    break;
                }

                if (TextUtils.isEmpty(ex.getMessage()) && e.getLocalizedMessage() != null) {
                    ex.setMessage(e.getLocalizedMessage());
                    break;
                }
                if (TextUtils.isEmpty(ex.getMessage())) {
                    ex.setMessage(context.getString(R.string.unauthorized));
                }
                break;
        }
        return ex;
    }


    /**
     * 约定异常
     */
    public class ERROR {
        /**
         * 未知错误
         */
        public static final int UNKNOWN = 1000;
        /**
         * 解析错误
         */
        public static final int PARSE_ERROR = 1001;
        /**
         * 网络错误
         */
        public static final int NETWORD_ERROR = 1002;
        /**
         * 协议出错
         */
        public static final int HTTP_ERROR = 1003;

        /**
         * 证书出错
         */
        public static final int SSL_ERROR = 1005;

        /**
         * 连接超时
         */
        public static final int TIMEOUT_ERROR = 1006;

        /**
         * 证书未找到
         */
        public static final int SSL_NOT_FOUND = 1007;

        /**
         * 出现空值
         */
        public static final int NULL = -100;

        /**
         * 格式错误
         */
        public static final int FORMAT_ERROR = 1008;
        /**
         * 格式错误
         */
        public static final int NETWORK_ERROR = 1008;
    }
}
