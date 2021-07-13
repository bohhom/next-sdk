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
package com.lib.sdk.next.o.map.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by maqing 2018/11/19 11:28
 * Email：2856992713@qq.com
 * 工程信息实体类
 */
public class ProjectInfoBean implements Parcelable {
    /**
     * 工程ID
     */
    private String mProjectId ="";
    /**
     * 工程时间戳
     */
    private String mProjectStamp ="";
    /**
     * 工程名（地图名）
     */
    private String mProjectName ="";
    /**
     * 地图基本信息缓存文件路径
     */
    private String mMapInfoCacheFilePath = "";
    /**
     * 虚拟墙信息缓存文件路径
     */
    private String mObstacleCacheInfoFilePath ="";
    /**
     * 地图上的位置点数据缓存文件路径
     */
    private String mPositionCacheInfoFilePath = "";

    /**
     * 是否可编辑名字
     */
    private boolean mNameEditable =false;

    public ProjectInfoBean() {

    }

    protected ProjectInfoBean(Parcel in) {
        mProjectId = in.readString();
        mProjectStamp = in.readString();
        mProjectName = in.readString();
        mMapInfoCacheFilePath = in.readString();
        mObstacleCacheInfoFilePath = in.readString();
        mPositionCacheInfoFilePath = in.readString();
        mNameEditable = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mProjectId);
        dest.writeString(mProjectStamp);
        dest.writeString(mProjectName);
        dest.writeString(mMapInfoCacheFilePath);
        dest.writeString(mObstacleCacheInfoFilePath);
        dest.writeString(mPositionCacheInfoFilePath);
        dest.writeByte((byte) (mNameEditable ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ProjectInfoBean> CREATOR = new Creator<ProjectInfoBean>() {
        @Override
        public ProjectInfoBean createFromParcel(Parcel in) {
            return new ProjectInfoBean(in);
        }

        @Override
        public ProjectInfoBean[] newArray(int size) {
            return new ProjectInfoBean[size];
        }
    };

    public String getProjectId() {
        return mProjectId;
    }

    public void setProjectId(String projectId) {
        mProjectId = projectId;
    }

    public String getProjectStamp() {
        return mProjectStamp;
    }

    public void setProjectStamp(String projectStamp) {

        mProjectStamp = projectStamp;
    }

    public String getProjectName() {
        return mProjectName;
    }

    public void setProjectName(String projectName) {
        mProjectName = projectName;
    }

    public String getMapInfoCacheFilePath() {
        return mMapInfoCacheFilePath;
    }

    public void setMapInfoCacheFilePath(String mapInfoCacheFilePath) {
        mMapInfoCacheFilePath = mapInfoCacheFilePath;
    }

    public String getObstacleCacheInfoFilePath() {
        return mObstacleCacheInfoFilePath;
    }

    public void setObstacleCacheInfoFilePath(String obstacleCacheInfoFilePath) {
        mObstacleCacheInfoFilePath = obstacleCacheInfoFilePath;
    }

    public String getPositionCacheInfoFilePath() {
        return mPositionCacheInfoFilePath;
    }

    public void setPositionCacheInfoFilePath(String positionCacheInfoFilePath) {
        mPositionCacheInfoFilePath = positionCacheInfoFilePath;
    }

    public boolean isNameEditable() {
        return mNameEditable;
    }

    public void setNameEditable(boolean nameEditable) {
        mNameEditable = nameEditable;
    }


}
