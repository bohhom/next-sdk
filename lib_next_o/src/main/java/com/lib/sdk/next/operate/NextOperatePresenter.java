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
package com.lib.sdk.next.operate;

import com.lib.sdk.next.o.http.BaseDataCallback;
import com.lib.sdk.next.o.http.HttpResponse;
import com.lib.sdk.next.o.map.model.CustomModel;
import com.lib.sdk.next.o.map.presenter.HttpPresenter;
import com.lib.sdk.next.o.model.DataModel;

import java.util.HashMap;

/**
 * FileName: NextPresener
 * Author: zhikai.jin
 * Date: 2021/6/30 11:18
 * Description:
 */
 class NextOperatePresenter extends HttpPresenter {
    /**
     * 请求2d导航
     * @param url
     * @param params
     */
    public void request2DNavGoal(String url, HashMap<String, Object> params) {
        realGetData(DataModel.invoke(CustomModel.class).params(params), url, new BaseDataCallback<HttpResponse>() {
            @Override
            public void dataCallback(HttpResponse data) {
                ((NextOperateHelper) getHelper()).onSet2DNavGoalCallBack(data);
            }
        });
    }

    /**
     * 请求2d导航
     * @param url
     * @param params
     */
    public void request2DNavCancel(String url, HashMap<String, Object> params) {
        realGetData(DataModel.invoke(CustomModel.class).params(params), url, new BaseDataCallback<HttpResponse>() {
            @Override
            public void dataCallback(HttpResponse data) {
                ((NextOperateHelper) getHelper()).onCancel2DNavGoalCallBack(data);
            }
        });
    }

    /**
     * 巡线导航
     * @deprecated  暂未提供
     * @param url
     * @param params
     */
    public void requestPathNav(String url, HashMap<String, Object> params){
//        realGetData(DataModel.invoke(CustomModel.class).params(params), url, new BaseDataCallback<HttpResponse>() {
//            @Override
//            public void dataCallback(HttpResponse data) {
//                ((NextOperateHelper) getHelper()).changeProjectDataCallBack(data);
//            }
//        });
    }


    /**
     * 任务发送
     * @param url
     * @param params
     */
    public void startTask(String url, HashMap<String, Object> params) {
        realGetData(DataModel.invoke(CustomModel.class).params(params), url, new BaseDataCallback<HttpResponse>() {
            @Override
            public void dataCallback(HttpResponse data) {
                ((NextOperateHelper) getHelper()).startTaskDataCallBack(data);
            }
        });
    }


}
