package com.mb.fhl.models;

/**
 * Created by Administrator on 2016/10/8 0008.
 */

public class BaseBean<T>{

    /**
     * msg : success
     * result : {"address":["黄良路","天河西路","黄村镇宋庄","黄村镇太福庄","黄村镇大庄村","北臧村镇砖楼","北臧村镇西大营","黄村镇韩园子村","北臧村镇新立村","北臧村镇六合庄"],"city":"北京市","district":"大兴区","postNumber":"102629","province":"北京市","size":"10"}
     * retCode : 200
     */

    private String message;
    /**
     * address : ["黄良路","天河西路","黄村镇宋庄","黄村镇太福庄","黄村镇大庄村","北臧村镇砖楼","北臧村镇西大营","黄村镇韩园子村","北臧村镇新立村","北臧村镇六合庄"]
     * city : 北京市
     * district : 大兴区
     * postNumber : 102629
     * province : 北京市
     * size : 10
     */

    private int status;
    private T data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
