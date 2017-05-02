package com.mb.fhl.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2017/4/27 0027.
 */

public class ShopBean {

    /**
     * orderinfo : [{"serialnum":"341","ordernum":"513","deliverytime":"1493283938","customername":"0","customertel":"0","goodstotal":"0.00","orderstatus":"1","goods":[{"goodnum":"1","goodname":"测试菜品1","goodsprice":"0.01"}],"deliveryinfo":[{"deliveryid":"1","deliverystaff":"游德彬","deliverytel":"18980110132","deliverystatus":"2"}],"shopstatus":"0","eat-in":{"shopname":"棒棒鸡","tablenumber":"1"}}]
     * totalAmount : 0.01
     * totalCount : 1
     * totalPage : 1
     * currentPage : 1
     * pageSize : 20
     */

    public String totalAmount;
    public String totalCount;
    public int totalPage;
    public int currentPage;
    public int pageSize;

    public List<OrderinfoBean> orderinfo;

    public static class OrderinfoBean {
        /**
         * serialnum : 341
         * ordernum : 513
         * deliverytime : 1493283938
         * customername : 0
         * customertel : 0
         * goodstotal : 0.00
         * orderstatus : 1
         * goods : [{"goodnum":"1","goodname":"测试菜品1","goodsprice":"0.01"}]
         * deliveryinfo : [{"deliveryid":"1","deliverystaff":"游德彬","deliverytel":"18980110132","deliverystatus":"2"}]
         * shopstatus : 0
         * eat-in : {"shopname":"棒棒鸡","tablenumber":"1"}
         */

        public String serialnum;
        public String ordernum;
        public String deliverytime;
        public String customername;
        public String customertel;
        public String goodstotal;
        public String orderaddress;

        public String orderstatus;
        public int orderStyle;

        public String shopstatus;
        public EatinBean eatin;
        public List<GoodsBean> goods;
        public List<DeliveryinfoBean> deliveryinfo;

        public static class EatinBean {
            /**
             * shopname : 棒棒鸡
             * tablenumber : 1
             */

            public String shopname;
            public String tablenumber;
        }

        public static class GoodsBean {
            /**
             * goodnum : 1
             * goodname : 测试菜品1
             * goodsprice : 0.01
             */

            public String goodnum;
            public String goodname;
            public String goodsprice;
        }

        public static class DeliveryinfoBean {
            /**
             * deliveryid : 1
             * deliverystaff : 游德彬
             * deliverytel : 18980110132
             * deliverystatus : 2
             */

            public String deliveryid;
            public String deliverystaff;
            public String deliverytel;
            public String deliverystatus;
        }
    }
}
