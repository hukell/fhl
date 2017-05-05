package com.mb.fhl.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/5/5 0005.
 */

public class DayBean implements Serializable{

    /**
     * order_count : [{"day":"1","ordercount":"0"},{"day":"2","ordercount":"1"},{"day":"3","ordercount":"7"}]
     * order_amount : [{"day":"1","orderamount":"0.00"},{"day":"2","orderamount":"0.01"},{"day":"3","orderamount":"0.13"}]
     * data_type : month
     */

    public String data_type;
    public List<OrderCountBean> order_count;
    public List<OrderAmountBean> order_amount;

    public static class OrderCountBean implements Serializable {

        /**
         * day : 1
         * ordercount : 0
         */


        public String day;
        public String month;
        public String ordercount;

        public String getDay(){

            if (null==day||"".equals(day))
            return month;
            else return day;
        };



    }

    public static class OrderAmountBean implements Serializable{
        /**
         * day : 1
         * orderamount : 0.00
         */

        public String day;
        public String month;
        public String orderamount;

        public String getDay(){

            if (null==day||"".equals(day))
                return month;
            else return day;
        };

    }
}
