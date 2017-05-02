package com.mb.fhl.models;

/**
 * Created by Administrator on 2017/4/28 0028.
 */

public class PhoneBean {
    public int point;
    public String s;
    public Deliver.DeliverlistBean deliverlistBean;

    public PhoneBean(int point, Deliver.DeliverlistBean deliverlistBean) {
        this.point = point;
        this.deliverlistBean = deliverlistBean;
    }
}
