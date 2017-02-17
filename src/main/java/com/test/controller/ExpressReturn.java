package com.test.controller;

import java.io.Serializable;
import java.util.List;

/**
 * 快递物流接口查询返回对象
 * Created by admin on 2016/12/27.
 */
public class ExpressReturn implements Serializable {
    private static final long serialVersionUID = 2114163622904688073L;
    //快递公司编码
    private String expressCode;
    //快递公司名称
    private String expressName;
    //快递单号
    private String trackingNo;
    //详情参考ExpressInfoStateEnum
    private Integer status;
    //异常原因
    private String reason;
    private List<ExpressTraces> tracesList;

    public String getExpressCode() {
        return expressCode;
    }

    public void setExpressCode(String expressCode) {
        this.expressCode = expressCode;
    }

    public String getExpressName() {
        return expressName;
    }

    public void setExpressName(String expressName) {
        this.expressName = expressName;
    }

    public String getTrackingNo() {
        return trackingNo;
    }

    public void setTrackingNo(String trackingNo) {
        this.trackingNo = trackingNo;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public List<ExpressTraces> getTracesList() {
        return tracesList;
    }

    public void setTracesList(List<ExpressTraces> tracesList) {
        this.tracesList = tracesList;
    }
}
