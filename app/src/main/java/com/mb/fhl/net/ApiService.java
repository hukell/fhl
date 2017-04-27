package com.mb.fhl.net;

import com.mb.fhl.models.BaseBean;
import com.mb.fhl.models.OrderBean;
import com.mb.fhl.models.ShopBean;
import com.mb.fhl.models.User;

import java.util.HashMap;

import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by Administrator on 2016/10/8 0008.
 */

public interface  ApiService {

    /**
     *
     *获得我的所有订单（配送员）
     *
     */
   @GET("getOrderList")
   Observable<BaseBean<OrderBean>> getOrderList(@QueryMap HashMap<String, Object> params);

    /**
     * 处理订单
     */
    @FormUrlEncoded
    @POST("dealOrder")
    Observable<BaseBean> dealOrder(@FieldMap HashMap<String, Object> params);

    /**
     * 登录
     */
    @FormUrlEncoded
    @POST("login")
    Observable<BaseBean<User>> login(@FieldMap HashMap<String, Object> params);

    /**
     *
     *获得我的所有订单（商户）
     *
     */
    @GET("getMerchantOrderList")
    Observable<BaseBean<ShopBean>> getMerchantOrderList(@QueryMap HashMap<String, Object> params);


     /**
     * 商户确认外卖订单
     */
    @FormUrlEncoded
    @POST("merchantConfirmTakeoutOrder")
    Observable<BaseBean> merchantConfirmTakeoutOrder(@FieldMap HashMap<String, Object> params);

     /**
     * 退款
     */
    @FormUrlEncoded
    @POST("merchantOrderRefund")
    Observable<BaseBean> merchantOrderRefund(@FieldMap HashMap<String, Object> params);

     /**
     * 打印订单
     */
    @FormUrlEncoded
    @POST("merchantPrintOrder")
    Observable<BaseBean> merchantPrintOrder(@FieldMap HashMap<String, Object> params);

    /**
     *
     商户确认堂吃订单
     */
    @FormUrlEncoded
    @POST("merchantConfirmEatinOrder")
    Observable<BaseBean> merchantConfirmEatinOrder(@FieldMap HashMap<String, Object> params);

}
