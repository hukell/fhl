package com.mb.fhl;

import android.app.Application;

import com.mb.fhl.utils.SharePreHelper;
import com.mb.fhl.utils.Utils;

/**
 * Created by Administrator on 2016/10/11 0011.
 */

public class App extends Application {
    public static App INS;
    // 单例模式获取唯一的MyApplication实例
    public static App getInstance() {
        if (null == INS) {
            INS = new App();
        }
        return INS;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        INS = this;
        SharePreHelper.getIns().initialize(this, null);
        Utils.init(this);
    }
}
