package com.test.controller;

import java.io.Serializable;

/**
 * 快递物流追踪详情信息
 * Created by admin on 2016/12/27.
 */
public class ExpressTraces implements Serializable {
    private static final long serialVersionUID = 2183666913093145177L;
    //标准时间类型字符串：如：2017-01-01 12:00:00
    private String acceptTime;
    //快递到站信息详情：如：【四川省自贡市荣县公司】 取件人: 李超 已收件
    private String station;
    //备注
    private String remark;

    public String getAcceptTime() {
        return acceptTime;
    }

    public void setAcceptTime(String acceptTime) {
        this.acceptTime = acceptTime;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
