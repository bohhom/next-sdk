package com.lib.sdk.next.robot.bean;

/**
 * FileName: RobotPostionBean
 * Author: zhikai.jin
 * Date: 2021/7/16 15:28
 * Description: 机器人位置
 */
public class RobotPostionBean {

    private double worldX = 0;

    private double worldY = 0;

    private double theta = 0;

    public double getWorldX() {
        return worldX;
    }

    public void setWorldX(double worldX) {
        this.worldX = worldX;
    }

    public double getWorldY() {
        return worldY;
    }

    public void setWorldY(double worldY) {
        this.worldY = worldY;
    }

    public double getTheta() {
        return theta;
    }

    public void setTheta(double theta) {
        this.theta = theta;
    }
}
