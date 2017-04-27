package com.mb.fhl.utils;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.mb.fhl.R;
import com.mb.fhl.adapter.PopDownAdapter;
import com.mb.fhl.models.ChangBean;
import com.mb.fhl.models.PhoneBean;
import com.mb.fhl.models.ShopBean;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by Administrator on 2017/4/27 0027.
 */

public class DialogUtils {

    public static void showPopTitleDown(Activity activity,View view){
        LayoutInflater mLayoutInflater = (LayoutInflater) activity.getSystemService(LAYOUT_INFLATER_SERVICE);
        ViewGroup menuView = (ViewGroup) mLayoutInflater.inflate(R.layout.title_down, null, true);
        TextView tvWm = (TextView) menuView.findViewById(R.id.tv_wm);
        TextView tvTc = (TextView) menuView.findViewById(R.id.tv_tc);

        PopupWindow pw = new PopupWindow(menuView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // 设置点击返回键使其消失，且不影响背景，此时setOutsideTouchable函数即使设置为false
        // 点击PopupWindow 外的屏幕，PopupWindow依然会消失；相反，如果不设置BackgroundDrawable
        // 则点击返回键PopupWindow不会消失，同时，即时setOutsideTouchable设置为true
        // 点击PopupWindow 外的屏幕，PopupWindow依然不会消失
        pw.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pw.setOutsideTouchable(false); // 设置是否允许在外点击使其消失，到底有用没？
      //  pw.setAnimationStyle(R.style.PopupAnimation); // 设置动画

     //   pw.getContentView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        tvWm.setOnClickListener(new TvListener(pw));
        tvTc.setOnClickListener(new TvListener(pw));

        int screenWidth = ScreenUtils.getScreenWidth()/2; //获取屏幕的宽度

        //测量布局的大小
        menuView.measure(0, 0);
       //将布局大小设置为PopupWindow的宽高
        PopupWindow popWindow = new PopupWindow(view, menuView.getMeasuredWidth(), menuView.getMeasuredHeight(), true);
        int width = popWindow.getWidth()/2;
        int i = screenWidth - width;

        pw.showAsDropDown(view,i,0);

    }

    public static void showPopPhoneDown(Activity activity, View view, List<ShopBean.OrderinfoBean.DeliveryinfoBean> deliveryinfo, final int point){
        LayoutInflater mLayoutInflater = (LayoutInflater) activity.getSystemService(LAYOUT_INFLATER_SERVICE);
        ViewGroup menuView = (ViewGroup) mLayoutInflater.inflate(R.layout.phone_down, null, true);

        RecyclerView recyclerView = (RecyclerView) menuView.findViewById(R.id.phone_pop_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        final List<String> list = new ArrayList<>();
        for (int i = 0; i <deliveryinfo.size() ; i++) {
            list.add(deliveryinfo.get(i).deliverystaff+":"+deliveryinfo.get(i).deliverytel);
        }
        list.add(deliveryinfo.get(0).deliverystaff+":"+deliveryinfo.get(0).deliverytel);

        int PopHeight = list.size() * 35;
        PopDownAdapter adapter = new PopDownAdapter(R.layout.phone_item,list);
        recyclerView.setAdapter(adapter);

        final PopupWindow pw = new PopupWindow(menuView, view.getWidth(),
                DisplayUtil.dip2px(activity,PopHeight), true);


        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                RxBus.getInstance().post(new PhoneBean(point,list.get(position)));
                pw.dismiss();
            }
        });
        // 设置点击返回键使其消失，且不影响背景，此时setOutsideTouchable函数即使设置为false
        // 点击PopupWindow 外的屏幕，PopupWindow依然会消失；相反，如果不设置BackgroundDrawable
        // 则点击返回键PopupWindow不会消失，同时，即时setOutsideTouchable设置为true
        // 点击PopupWindow 外的屏幕，PopupWindow依然不会消失
        pw.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pw.setOutsideTouchable(false); // 设置是否允许在外点击使其消失，到底有用没？
        //  pw.setAnimationStyle(R.style.PopupAnimation); // 设置动画

        pw.showAsDropDown(view);

    }


    private static class TvListener implements View.OnClickListener {
        private PopupWindow pw;

        public TvListener(PopupWindow pw) {
            this.pw = pw;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.tv_wm:
                    RxBus.getInstance().post(new ChangBean("外卖订单",1));
                    pw.dismiss();
                    break;
                case R.id.tv_tc:
                    RxBus.getInstance().post(new ChangBean("堂吃订单",2));
                    pw.dismiss();
                    break;
            }
        }
    }
}
