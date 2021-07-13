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
package com.lib.sdk.next.wall;

import com.lib.sdk.next.o.map.bean.PositionPointBean;
import com.lib.sdk.next.o.map.bean.VirtualWallBean;

/**
 * FileName: VirtualWallOption
 * Author: zhikai.jin
 * Date: 2021/6/25 13:53
 * Description:
 */
 class VirtualWallOption {



    private int operateType = -1;


    private VirtualWallBean currentWall;


    public int getOperateType() {
        return operateType;
    }

    public void setOperateType(int operateType) {
        this.operateType = operateType;
    }


    public VirtualWallBean getCurrentWall() {
        return currentWall;
    }

    public void setCurrentWall(VirtualWallBean currentWall) {
        this.currentWall = currentWall;
    }
}
