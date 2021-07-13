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
package com.lib.sdk.next.o.map.resp;

import java.io.Serializable;

/**
 * 工程简单信息
 * Created by aruba on 2019/5/27.
 */

public class SampleProject implements Serializable {
    private String project_id;
    private String project_stamp;

    public String getProjectId() {
        return project_id;
    }

    public void setProjectId(String project_id) {
        this.project_id = project_id;
    }

    public String getProjectStamp() {
        return project_stamp;
    }

    public void setProjectStamp(String project_stamp) {
        this.project_stamp = project_stamp;
    }
}
