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

import com.lib.sdk.next.o.config.UnitSystemConfig;
import com.tamic.novate.Novate;

import java.util.HashMap;

/**
 * Created by aruba on 2018/7/16.
 */

public class NovateHelper {
    private static String base_url = "";
    private static NovateHelper instance;
    private Novate novate;
    private HashMap<String, Novate> novateMap = new HashMap();

    public static NovateHelper getInstance() {
        if (instance == null) {
            instance = new NovateHelper();
        }
        return instance;
    }

    public void init(Context context, String baseUrl) {
        this.base_url = baseUrl;

        novate = new Novate.Builder(context.getApplicationContext())
                .baseUrl(base_url)
                .skipSSLSocketFactory(true)
                .connectTimeout(60)
                .readTimeout(60)
                .writeTimeout(60)
                .addLog(true)
                .addCache(false)
                .build();

        novateMap.put(baseUrl, novate);
    }

    public Novate getNovate() {
        return novate;
    }

    public Novate getNovate(String baseUrl) {
        return novateMap.get(baseUrl);
    }

    public void parseToken(Context context, String token) {
        HashMap<String, Object> headers = new HashMap<>();
        headers.put("Token", token);

        UnitSystemConfig.token = token;
        UnitSystemConfig.base_url = base_url;

        novate = new Novate.Builder(context.getApplicationContext())
                .baseUrl(base_url)
//                .addLog(true)
                .skipSSLSocketFactory(true)
                .addCache(false)
                .addHeader(headers)
                .build();
    }

    public ToMainClear toMainClear;

    public void setToMainClear(ToMainClear toMainClear) {
        this.toMainClear = toMainClear;
    }

    public interface ToMainClear {
        void toMainClear();
    }

}
