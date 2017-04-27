package com.mb.fhl.net;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.mb.fhl.ui.LoginActivity;
import com.mb.fhl.utils.UserManager;

import java.io.IOException;

import retrofit2.adapter.rxjava.HttpException;

/**
 * Created by Administrator on 2016/10/11 0011.
 * /辅助处理异常
 */

public class ApiErrorHelper {

    public static void handleCommonError(Context context, Throwable e) {
        if (e instanceof HttpException) {
            Toast.makeText(context, "服务暂不可用", Toast.LENGTH_SHORT).show();
        } else if (e instanceof IOException) {
            Toast.makeText(context, "连接失败", Toast.LENGTH_SHORT).show();
        } else if (e instanceof ApiException) {
            if (((ApiException) e).getErrorCode()==2){
                UserManager.getIns().clearUserInfo();
                context.startActivity(new Intent(context,LoginActivity.class));
                ((Activity)context).finish();
            }
             Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "未知错误", Toast.LENGTH_SHORT).show();
        }
    }
}
