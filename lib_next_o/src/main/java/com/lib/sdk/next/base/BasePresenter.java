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
package com.lib.sdk.next.base;

/**
 * FileName: BasePresenter
 * Author: zhikai.jin
 * Date: 2021/6/2 19:03
 * Description:
 */
public class BasePresenter <V extends IBaseHelper>{

    /**
     * 绑定的view
     */
    private V mHelper;

    /**
     * 绑定view，一般在初始化中调用该方法
     */
    public void attachHelper(V helper) {
        this.mHelper = helper;
    }

    /**
     * 断开view，一般在onDestroy中调用
     */
    public void detachView() {
        this.mHelper = null;
    }

    /**
     * 是否与View建立连接
     * 每次调用业务请求的时候都要出先调用方法检查是否与View建立连接
     */
    public boolean isHelperAttached() {
        return mHelper != null;
    }

    /**
     * 获取连接的view
     */
    public V getHelper() {
        return mHelper;
    }

}
