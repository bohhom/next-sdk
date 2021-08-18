
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
package com.lib.sdk.next.o.map.presenter;

import com.lib.sdk.next.base.BasePresenter;
import com.lib.sdk.next.o.R;
import com.lib.sdk.next.o.http.BaseDataCallback;
import com.lib.sdk.next.o.http.HttpResponse;
import com.lib.sdk.next.o.http.MyNovateException;
import com.lib.sdk.next.global.GlobalOperate;
import com.lib.sdk.next.o.model.BaseModel;
import com.tamic.novate.Throwable;
import com.tamic.novate.callback.RxStringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by aruba on 2018/2/6.
 */

public abstract class HttpPresenter  extends BasePresenter {

    private RxStringCallback rxResultCallback;

    private HashMap<Object, BaseDataCallback> baseDataCallbackHashMap = new HashMap<>();

    protected void realGetData(BaseModel baseModel, String url, BaseDataCallback baseDataCallback) {
        baseDataCallbackHashMap.put(url, baseDataCallback);
        reqGetData(baseModel, url, null);
    }

    protected void realGetDataShowProgress(BaseModel baseModel, String url, BaseDataCallback baseDataCallback) {
        baseDataCallbackHashMap.put(url, baseDataCallback);
        reqGetData(baseModel, url, null);
    }

    /**
     * 获取网络数据
     */
    protected void reqGetData(BaseModel baseModel, String url, RxStringCallback rxResultCallback) {
        if (!isHelperAttached()) {
            //是否绑定实体类
            return;
        }

        if(!getHelper().checkNetWork()){
            getHelper().showErr(url,MyNovateException.NETWORD_ERROR, GlobalOperate.getApp().getString(R.string.not_found2));
            return;
        }

        if (rxResultCallback == null) {
            if (this.rxResultCallback == null)
                this.rxResultCallback = getDefaultRxResultCallback();
        } else {
            this.rxResultCallback = rxResultCallback;
        }
        baseModel.execute(url, this.rxResultCallback);
    }

    private RxStringCallback defaultRxResultCallback;

    protected RxStringCallback getDefaultRxResultCallback() {
        if (defaultRxResultCallback == null) {
            defaultRxResultCallback = new RxStringCallback() {

                @Override
                public void onError(Object tag, Throwable e) {
                    getHelper().showErr((String) tag,e.getCode(), e.getMessage());
                }

                @Override
                public void onNext(Object tag, String responseStr) {

                    if(isHelperAttached()){
                        BaseDataCallback baseDataCallback = baseDataCallbackHashMap.remove(tag);
                        try {
                            JSONObject responseJsonObject = new JSONObject(responseStr);
                            HttpResponse httpResponse = new HttpResponse();
                            httpResponse.code = responseJsonObject.optInt("result_code");
                            httpResponse.time = responseJsonObject.optString("result_stamp");
                            httpResponse.info = responseJsonObject.optString("result_msg");
                            httpResponse.data = responseJsonObject.optString("data");
                            baseDataCallback.dataCallback(httpResponse);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            getHelper().showErr((String) tag,MyNovateException.FORMAT_JSON_ERROR,e.getMessage());

                        }
                    }
                    else{
                        getHelper().showErr((String) tag, MyNovateException.BIND_UNKNOWN_ERROR,"未绑定实体类");
                    }
                }



                @Override
                public void onCancel(Object tag, Throwable e) {
                    baseDataCallbackHashMap.remove(tag);
                    getHelper().showErr((String) tag, e.getCode(),e.getMessage());
                }

            };
        }
        return defaultRxResultCallback;
    }


}
