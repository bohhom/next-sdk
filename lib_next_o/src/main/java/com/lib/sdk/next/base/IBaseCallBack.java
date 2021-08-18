package com.lib.sdk.next.base;

/**
 * FileName: IBaseCallBack
 * Author: zhikai.jin
 * Date: 2021/8/1 16:05
 * Description:
 */
public interface IBaseCallBack {
    void onHttpError(String url,int code,String msg);
}
