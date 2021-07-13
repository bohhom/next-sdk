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

package com.lib.sdk.next.point;

import com.lib.sdk.next.o.http.BaseDataCallback;
import com.lib.sdk.next.o.http.HttpResponse;
import com.lib.sdk.next.o.map.presenter.HttpPresenter;
import com.lib.sdk.next.o.model.DataModel;
import com.lib.sdk.next.o.model.JsonRequestModel;

import org.json.JSONObject;

/**
 * FileName: PointInitPresenter
 * Author: zhikai.jin
 * Date: 2021/6/18 15:55
 * Description: 初始点请求
 */
public class PointInitPresenter extends HttpPresenter {
    public void pushSyncConfig(String url, JSONObject params) {
        realGetData(DataModel.invoke(JsonRequestModel.class).params(params), url, new BaseDataCallback<HttpResponse>() {
            @Override
            public void dataCallback(HttpResponse data) {
                ((PointInitHelper) getHelper()).pushSyncConfigDataCallBack(data);
            }
        });
    }
}
