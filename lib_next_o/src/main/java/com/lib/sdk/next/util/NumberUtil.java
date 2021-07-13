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

package com.lib.sdk.next.util;

import java.math.BigDecimal;

/**
 * Created by maqing on 2018/1/20.
 * Email:2856992713@qq.com
 */

public class NumberUtil {

    /**
     * 提供精确的小数位四舍五入处理。
     *
     */
    public static double keep2Precision(double f) {
        BigDecimal b = new BigDecimal(f);
        double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return f1;
    }

    /**
     * 提供精确的小数位四舍五入处理。
     *
     */
    public static double keep1Precision(double f) {
        BigDecimal b = new BigDecimal(f);
        double f1 = b.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
        return f1;
    }
}
