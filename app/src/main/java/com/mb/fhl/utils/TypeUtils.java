package com.mb.fhl.utils;

/**
 * Created by Administrator on 2017/4/28 0028.
 */

public class TypeUtils {
    public static String getString(int type){
        switch (type){
            case 1:
                return "待处理";
            case 2:
                return "进行中";
            case 3:
                return "已完成";
        }
        return "";
    }
}
