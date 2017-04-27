package com.mb.fhl.net;

import com.github.simonpercic.oklog3.OkLogInterceptor;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

/**
 * Created by Administrator on 2016/10/8 0008.
 */

public class Api {
    public static Retrofit mRetrofit;
    private static ApiService mApiService;
    public static String base_url;
    private static OkHttpClient okHttpClient = null;
    public static int TIME_OUT = 20;

    private static boolean checkNull() {
        return mRetrofit == null ? true : false;
    }

    private static void init() {

        // create an instance of OkLogInterceptor using a builder()
        OkLogInterceptor okLogInterceptor = OkLogInterceptor.builder().build();

       // create an instance of OkHttpClient builder
        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();

       // add OkLogInterceptor to OkHttpClient's application interceptors
        okHttpBuilder.addInterceptor(okLogInterceptor);

        OkHttpClient okHttpClient = okHttpBuilder.build();

        base_url = getServerUrl();
        mRetrofit = new Retrofit.Builder()
                .baseUrl(base_url )
                .client(okHttpClient)
                .addConverterFactory(CustomGsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        mApiService = mRetrofit.create(ApiService.class);
    }

    public static ApiService getRetrofit() {
        if (checkNull()) {
            init();
        }
        return mApiService;
    }

    public static String getServerUrl() {

        return "https://wechat.huokequan.com/index.php/Admin/OrderReceive/";
    }

    public static void resetRetrofit() {
        mRetrofit = null;
    }
}