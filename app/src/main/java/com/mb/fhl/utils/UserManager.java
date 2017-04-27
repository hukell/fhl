package com.mb.fhl.utils;

import android.content.SharedPreferences;
import android.util.Base64;

import com.mb.fhl.App;
import com.mb.fhl.models.User;

/**
 * Created by MagicBean on 2016/03/05 14:14:53
 */
public class UserManager {
    private static UserManager mUserManager;

    private boolean isRegisterToServer;

    private UserManager() {

    }

    public static UserManager getIns() {
        if (mUserManager == null) {
            synchronized (UserManager.class) {
                mUserManager = new UserManager();
            }
        }
        return mUserManager;
    }

    public void logout() {

    }

    /**
     * 清除session
     */
    /*public void clearToken() {
        SharedPreferences mPreferences = App.getInst().getSharedPreferences("UserInfo", 0);
        mPreferences.edit().clear().commit();
    }*/

    /**
     * 刷新用户数据
     */
//    public synchronized void refreshUserInfo() {
//        if (getUser() == null) return;
//        if (getUser() != null && !TextUtil.isValidate(getUser().token)) {
//            return;
//        }
//        HashMap<String, Object> params = HttpParamsHelper.createParams();
//        params.put("token", getUser().token);
//        Api.getRetrofit().refreshUserInfo(params).enqueue(new Callback<HttpResponse<User>>() {
//            @Override
//            public void onResponse(Response<HttpResponse<User>> response) {
//                if (response.isSuccess()) {
//                    if (response.body() != null) {
//                        //token 过期或者 其他社保登录
//                        if (response.body().code == 401 || response.body().code == 403) {
//                            App.getInst().toLogin();
//                        } else {
//                            User user = response.body().getDataFrist();
//                            if (user != null) {
//                                App.getInst().setUser(user);
//                                saveUserInfo(user);
//                                if (!isRegisterToServer) {
//                                    isRegisterToServer = true;
//                                    PushManager.getInstance().initialize(App.getInst().getApplicationContext());
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//
//            }
//        });
//    }

    /***
     * 自动登录
     *
     * @return
     */


    /**
     * 获取用户信息
     *
     * @param
     */

    public synchronized User getUser() {
        SharedPreferences mPreferences = App.getInstance().getSharedPreferences("UserInfo", 0);
        String temp = mPreferences.getString("_user", "");
        byte[] base64Bytes = Base64.decode(temp, Base64.DEFAULT);
        String retStr = new String(base64Bytes);
        User user = JsonParser.deserializeByJson(retStr, User.class);
        return user;
    }

    /**
     * 保存用户信息
     * @param user
     */
    public synchronized void saveUserInfo(User user) {
        if (user == null) return;
        SharedPreferences mPreferences = App.getInstance().getSharedPreferences("UserInfo", 0);
        String json = JsonParser.serializeToJson(user);
        String temp = new String(Base64.encode(json.getBytes(), Base64.DEFAULT));
        mPreferences.edit().putString("_user", temp).commit();
    }
    /**
     * 清楚用户信息
     */

    public synchronized void clearUserInfo() {
        SharedPreferences mPreferences = App.getInstance().getSharedPreferences("UserInfo", 0);
        mPreferences.edit().putString("_user", "").commit();
    }

    /**
    /**
     * 清楚首页信息
     */

    public synchronized void clearHomeInfo() {
        SharedPreferences mPreferences = App.getInstance().getSharedPreferences("HomeInfo", 0);
        mPreferences.edit().putString("homeInfo", "").commit();
    }




    /**
     * 保存旅客姓名
     */

    public synchronized void saveUserName(String str) {
        SharedPreferences mPreferences = App.getInstance().getSharedPreferences("UserName", 0);
        mPreferences.edit().putString("UserName", str).commit();
    }

    /**
     * 清出旅客姓名
     */

    public synchronized void clearUserName() {
        SharedPreferences mPreferences = App.getInstance().getSharedPreferences("UserName", 0);
        mPreferences.edit().putString("UserName", "").commit();
    }

     /**
     * 得到旅客姓名
     */

    public synchronized String getUserName() {
        SharedPreferences mPreferences = App.getInstance().getSharedPreferences("UserName", 0);
        String userName = mPreferences.getString("UserName", "");
        return userName;
    }


    /**
     * 保存日期
     */

    public synchronized void saveData(String str) {
        SharedPreferences mPreferences = App.getInstance().getSharedPreferences("data", 0);
        mPreferences.edit().putString("data", str).commit();
    }

    /**
     * 得到日期
     */

    public synchronized String getData() {
        SharedPreferences mPreferences = App.getInstance().getSharedPreferences("data", 0);
        String data = mPreferences.getString("data", "");
        return data;
    }

    /**
     * 清楚日期
     */

    public synchronized void clearData() {
        SharedPreferences mPreferences = App.getInstance().getSharedPreferences("data", 0);
        mPreferences.edit().putString("data", "").commit();
    }


    public void setIsCancelUpdata(int code) {
        SharedPreferences mPreferences = App.getInstance().getSharedPreferences("isCancelUpdata", 0);
        mPreferences.edit().putInt("isCancelUpdata", code).commit();
    }

    public int getIsCancelUpdata() {
        SharedPreferences mPreferences = App.getInstance().getSharedPreferences("isCancelUpdata", 0);
        int isCancelUpdata = mPreferences.getInt("isCancelUpdata", 0);
        return isCancelUpdata;

    }


}
