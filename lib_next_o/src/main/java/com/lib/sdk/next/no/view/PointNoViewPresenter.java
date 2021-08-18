package com.lib.sdk.next.no.view;

import com.lib.sdk.next.o.http.BaseDataCallback;
import com.lib.sdk.next.o.http.HttpResponse;
import com.lib.sdk.next.o.map.presenter.HttpPresenter;
import com.lib.sdk.next.o.model.DataModel;
import com.lib.sdk.next.o.model.JsonRequestModel;
import com.lib.sdk.next.point.PointHelper;

import org.json.JSONObject;

/**
 * FileName: PointNoViewPresenter
 * Author: zhikai.jin
 * Date: 2021/8/12 9:40
 * Description:
 */
public class PointNoViewPresenter extends HttpPresenter {

    public void pushSyncConfig(String url, JSONObject params) {
        realGetData(DataModel.invoke(JsonRequestModel.class).params(params), url, new BaseDataCallback<HttpResponse>() {
            @Override
            public void dataCallback(HttpResponse data) {
                ((PointNoViewHelper) getHelper()).pushSyncConfigDataCallBack(data);
            }
        });
    }
}
