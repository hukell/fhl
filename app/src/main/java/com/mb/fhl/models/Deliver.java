package com.mb.fhl.models;

import java.util.List;

/**
 * Created by Administrator on 2017/5/2 0002.
 */

public class Deliver {

    public List<DeliverlistBean> deliverlist;

    public static class DeliverlistBean {
        /**
         * deliveryid : 1
         * deliverystaff : 游德彬
         * deliverytel : 18980110132
         */

        public String deliveryid;
        public String deliverystaff;
        public String deliverytel;
    }
}
