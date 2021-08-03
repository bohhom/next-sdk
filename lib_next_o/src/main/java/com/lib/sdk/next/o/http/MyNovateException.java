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

    public static final int UNAUTHORIZED = 401;

    public static final int FORBIDDEN = 403;

    /**
     * 请检查网络连接是否正确
     */
    public static final int NOT_FOUND = 404;

    public static final int REQUEST_TIMEOUT = 408;

    public static final int INTERNAL_SERVER_ERROR = 500;

    public static final int BAD_GATEWAY = 502;

    public static final int SERVICE_UNAVAILABLE = 503;

    public static final int GATEWAY_TIMEOUT = 504;

    public static final int ACCESS_DENIED = 302;

    public static final int HANDEL_ERRROR = 417;

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
     * 返回json格式解析错误
     */
    public static final int FORMAT_JSON_ERROR = 2001;


    /**
     * 未绑定实体
     */
    public static final int BIND_UNKNOWN_ERROR = 2002;



}
