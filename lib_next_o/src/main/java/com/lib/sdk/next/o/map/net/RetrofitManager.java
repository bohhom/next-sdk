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
package com.lib.sdk.next.o.map.net;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by maqing on 2017/8/10.
 * Email:2856992713@qq.com
 * Retrofit封装
 */
public class RetrofitManager {
    private Retrofit mRetrofit;
    private OkHttpClient mOkHttpClient;
    private String mBaseUrl;
    private long mConnectTimeout;
    private long mReadTimeout;
    private long mWriteTimeout;
    private static final long DEFAULT_TIMEOUT = 15000L;

    private RetrofitManager(Builder builder) {
        this.mConnectTimeout = builder.mConnectTimeout;
        this.mReadTimeout = builder.mReadTimeout;
        this.mWriteTimeout = builder.mWriteTimeout;
        this.mBaseUrl = builder.mBaseUrl;
        initRetrofit();
    }

    private void initRetrofit() {

        mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(mConnectTimeout, TimeUnit.MILLISECONDS)
                .writeTimeout(mReadTimeout, TimeUnit.MILLISECONDS)
                .readTimeout(mWriteTimeout, TimeUnit.MILLISECONDS)
                .hostnameVerifier(SSLSocketClient.getHostnameVerifier())
                .sslSocketFactory(SSLSocketClient.getSSLSocketFactory(), new SSLTrustAllManager())
                .build();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(mBaseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(mOkHttpClient)
                .build();
    }

    public <T> T createRequest(Class<T> requestClass) {
        return mRetrofit.create(requestClass);
    }

    public static final class Builder {
        private String mBaseUrl;
        private long mConnectTimeout;
        private long mReadTimeout;
        private long mWriteTimeout;

        public Builder() {
            mConnectTimeout = DEFAULT_TIMEOUT;
            mReadTimeout = DEFAULT_TIMEOUT;
            mWriteTimeout = DEFAULT_TIMEOUT;
        }

        public Builder baseUrl(String baseUrl) {
            mBaseUrl = baseUrl;
            return this;
        }

        public Builder connectTimeout(long timeout) {
            mConnectTimeout = timeout;
            return this;
        }

        public Builder readTimeout(long timeout) {
            mReadTimeout = timeout;
            return this;
        }

        public Builder writeTimeout(long timeout) {
            mWriteTimeout = timeout;
            return this;
        }

        public RetrofitManager build() {
            return new RetrofitManager(this);
        }
    }

}
