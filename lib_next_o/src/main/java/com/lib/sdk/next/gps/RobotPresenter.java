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
package com.lib.sdk.next.gps;


import com.lib.sdk.next.o.http.BaseDataCallback;
import com.lib.sdk.next.o.http.HttpResponse;
import com.lib.sdk.next.o.map.model.CustomModel;
import com.lib.sdk.next.o.map.presenter.HttpPresenter;
import com.lib.sdk.next.o.model.DataModel;

import java.util.HashMap;

/**
 * Created by aruba on 2020/9/22.
 */

public class RobotPresenter extends HttpPresenter {

    public void initLocation(String url, HashMap<String, Object> params) {
        realGetData(DataModel.invoke(CustomModel.class).params(params), url, new BaseDataCallback<HttpResponse>() {
            @Override
            public void dataCallback(HttpResponse data) {
                ((LocationHelper) getHelper()).initLocationDataCallBack(data);
            }
        });
    }

    public void initLocationForce(String url, HashMap<String, Object> params) {
        realGetData(DataModel.invoke(CustomModel.class).params(params), url, new BaseDataCallback<HttpResponse>() {
            @Override
            public void dataCallback(HttpResponse data) {
                ((LocationHelper) getHelper()).initLocationDataCallBack(data);
            }
        });
    }

//    public void startTask(String url, HashMap<String, Object> params) {
//        realGetData(DataModel.invoke(CustomModel.class).params(params), url, new BaseDataCallback<HttpResponse>() {
//            @Override
//            public void dataCallback(HttpResponse data) {
//                ((StartTaskView) getView()).startTaskDataCallBack(data);
//            }
//        });
//    }

//    public void getTaskLastRunStatus(String url, HashMap<String, Object> params) {
//        realGetData(DataModel.invoke(CustomModel.class).params(params), url, new BaseDataCallback<HttpResponse>() {
//            @Override
//            public void dataCallback(HttpResponse data) {
//                ((GetTaskLastStatusView) getView()).getTaskLastStatusDataCallBack(data);
//            }
//        });
//    }

//    //地图闭环操作
//    public void mapCloser(String url, HashMap<String, Object> params) {
//        realGetData(DataModel.invoke(CustomModel.class).params(params), url, new BaseDataCallback<HttpResponse>() {
//            @Override
//            public void dataCallback(HttpResponse data) {
//                ((MapCloserView) getView()).mapCloserDataCallBack(data);
//            }
//        });
//    }
//
//    //去除噪点 v1
//    public void updateMapPixmap(String url, HashMap<String, Object> params) {
//        realGetData(DataModel.invoke(CustomModel.class).params(params), url, new BaseDataCallback<HttpResponse>() {
//            @Override
//            public void dataCallback(HttpResponse data) {
//                ((UpdateMapPixView) getView()).updateMapPixDataCallBack(data);
//            }
//        });
//    }
    
}
