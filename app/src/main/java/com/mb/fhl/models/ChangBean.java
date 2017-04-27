package com.mb.fhl.models;

/**
 * Created by Administrator on 2017/4/27 0027.
 */

public class ChangBean {
    public String mString;
    public int orderStyle;

    public ChangBean(String string, int orderStyle) {
        mString = string;
        this.orderStyle = orderStyle;
    }

    @Override
    public String toString() {
        return "ChangBean{" +
                "mString='" + mString + '\'' +
                ", orderStyle=" + orderStyle +
                '}';
    }
}
