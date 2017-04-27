package com.mb.fhl.models;

import java.util.List;

/**
 * Created by Administrator on 2017/4/20 0020.
 */

public class OrderBean {


    /**
     * data : [{"orderid":"201704201003029312176742","transportime":"1492655582","destinationadress":"10-920","customername":"文磊","customertel":"15882402572","shopname":"棒棒鸡","takeadress":"成都双流天王街231号"}]
     * state : 1
     * totalCount : 1
     * totalPage : 1
     * currentPage : 1
     * pageSize : 20
     */

    public String state;
    public String totalCount;
    public int totalPage;
    public int currentPage;
    public int pageSize;
    public List<DataBean> data;

    public static class DataBean {
        /**
         * orderid : 201704201003029312176742
         * transportime : 1492655582
         * destinationadress : 10-920
         * customername : 文磊
         * customertel : 15882402572
         * shopname : 棒棒鸡
         * takeadress : 成都双流天王街231号
         */

        public String orderid;
        public long transportimeint;
        public long ordertimeint;
        public String destinationadress;
        public String customername;
        public String customertel;
        public String shopname;
        public String takeadress;

    }
}
