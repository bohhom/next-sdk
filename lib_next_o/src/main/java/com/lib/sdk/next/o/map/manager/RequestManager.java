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

package com.lib.sdk.next.o.map.manager;



import com.lib.sdk.next.o.map.net.RetrofitManager;
import com.lib.sdk.next.o.map.util.MD5Util;

import java.util.Arrays;
import java.util.Map;

/**
 * Created by maqing on 2017/8/11.
 * Email:2856992713@qq.com
 * RequestManager
 */
public class RequestManager {

    public static RetrofitManager mRetrofitManager;

    /**
     * Http IP地址
     */
//    public static final String IP = "174055wd25.51mypc.cn";

    public static String IP = "";

    /**
     * Socket IP地址(只能是IP地址不能是域名)
     */
//    public static final String SOCKET_IP = "103.46.128.47";
    public static String SOCKET_IP = "192.168.168.10";

    /**
     * Http端口号
     */
//    public static final String HTTP_PORT = "15627";
    public static final String HTTP_PORT = "9003";
//    public static final String HTTP_PORT = "9004";

    /**
     * Socket端口号
     */
//    public static final String SOCKET_PORT = "39670";
    public static final String SOCKET_PORT = "9002";

    /**
     * Http请求地址
     */
    public static String mHttpBaseUrl = "http://192.168.168.10:9003/";
    /**
     * Socket请求地址
     */
    public static String mSocketBaseUrl = "ws://" + SOCKET_IP + ":" + SOCKET_PORT + "/";


    /**
     * 加密:增加时间戳 MD5签名
     *
     * @param map Map集合
     * @return 加密之后的Map集合
     */
    public static Map<String, String> encryptParams(Map<String, String> map) {
        String timestamp = Long.toString(System.currentTimeMillis()).substring(0, 10);
        map.put("timestamp", timestamp);
        String[] array = new String[map.size()];
        int i = 0;
        for (String key : map.keySet()) {
            array[i] = key;
            i++;
        }
        Arrays.sort(array);
        String signature = "";
        for (int j = 0; j < array.length; j++) {
            String key = array[j];
            if (signature.equals("")) {
                signature = signature + key + "=" + map.get(key);
            } else {
                signature = signature + "&" + key + "=" + map.get(key);
            }
            if (j == array.length - 1) {
                signature = signature + "&nado";
            }
        }
        map.put("sig", MD5Util.getMD5Str(signature));
        return map;
    }

}
