package com.bozhon.sdk.next.one;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lib.sdk.next.robot.bean.RobotStatusBean;
import com.lib.sdk.next.robot.constant.RobotConstant;

/**
 * FileName: RobotSpeedViewModel
 * Author: zhikai.jin
 * Date: 2021/6/28 15:52
 * Description:
 */
public class RobotSpeedViewModel extends ViewModel {

    private MutableLiveData<RobotStatusBean> robotStatusBean;

    public MutableLiveData<RobotStatusBean> getRobotStatusBean() {
        if (robotStatusBean == null) {
            robotStatusBean = new MutableLiveData<RobotStatusBean>();
        }
        return robotStatusBean;
    }

}
