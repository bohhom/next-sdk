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

package com.lib.sdk.next.setting;

import com.lib.sdk.next.o.model.BaseModel;
import com.tamic.novate.callback.RxStringCallback;

/**
 * Created by aruba on 2018/7/11.
 */

public class GetModel extends BaseModel {

    @Override
    public void execute(String url, Object rxResultCallback) {
        requestGetAPI(url, (RxStringCallback) rxResultCallback);
    }

}
