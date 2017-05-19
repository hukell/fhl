package com.mb.fhl.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.mb.fhl.R;
import com.mb.fhl.adapter.PopDownAdapter;
import com.mb.fhl.models.BaseBean;
import com.mb.fhl.models.ChangBean;
import com.mb.fhl.models.Deliver;
import com.mb.fhl.models.OutOrderBean;
import com.mb.fhl.models.PhoneBean;
import com.mb.fhl.net.Api;
import com.mb.fhl.net.BaseSubscriber;
import com.mb.fhl.ui.LoginActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
        TextView tvYd = (TextView) menuView.findViewById(R.id.tv_yd);
        TextView tvOrderData = (TextView) menuView.findViewById(R.id.tv_order_data);

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
        tvYd.setOnClickListener(new TvListener(pw));
        tvOrderData.setOnClickListener(new TvListener(pw));

        int screenWidth = ScreenUtils.getScreenWidth()/2; //获取屏幕的宽度

        //测量布局的大小
        menuView.measure(0, 0);
       //将布局大小设置为PopupWindow的宽高
        PopupWindow popWindow = new PopupWindow(view, menuView.getMeasuredWidth(), menuView.getMeasuredHeight(), true);
        int width = popWindow.getWidth()/2;
        int i = screenWidth - width;

        pw.showAsDropDown(view,i,0);

    }

    public static void showPopPhoneDown(Activity activity, View view, final List<Deliver.DeliverlistBean> mDeliverlist, final int point){
        LayoutInflater mLayoutInflater = (LayoutInflater) activity.getSystemService(LAYOUT_INFLATER_SERVICE);
        ViewGroup menuView = (ViewGroup) mLayoutInflater.inflate(R.layout.phone_down, null, true);

        RecyclerView recyclerView = (RecyclerView) menuView.findViewById(R.id.phone_pop_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        final List<String> list = new ArrayList<>();
        for (int i = 0; i <mDeliverlist.size() ; i++) {
            list.add(mDeliverlist.get(i).deliverystaff+":"+mDeliverlist.get(i).deliverytel);
        }

        int PopHeight = list.size() * 35;
        PopDownAdapter adapter = new PopDownAdapter(R.layout.phone_item,list);
        recyclerView.setAdapter(adapter);

        final PopupWindow pw = new PopupWindow(menuView, view.getWidth(),
                DisplayUtil.dip2px(activity,PopHeight), true);


        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                RxBus.getInstance().post(new PhoneBean(point,mDeliverlist.get(position)));
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


    public static void submitLogOut(final Activity activity) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View v = inflater.inflate(R.layout.logout_dialog, null);
        TextView tvLogout = (TextView) v.findViewById(R.id.tv_logout);
        TextView tvCansal = (TextView) v.findViewById(R.id.tv_cansal);

        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HashMap<String, Object> params = new HashMap<>();
                params.put("userId", UserManager.getIns().getUser().uid);
                params.put("token", UserManager.getIns().getUser().accessToken);
                params.put("userType",UserManager.getIns().getUser().userType);

                Api.getRetrofit().logoff(params)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new BaseSubscriber<BaseBean>(activity){
                            @Override
                            public void onNext(BaseBean baseBean) {
                                super.onNext(baseBean);
                                UserManager.getIns().clearUserInfo();
                                activity.startActivity(new Intent(activity, LoginActivity.class));
                                activity.finish();
                            }
                        });
            }
        });

        // 创建自定义样式dialog
        final Dialog mLoadingDialog = new Dialog(activity, R.style.loading_dialog);

        mLoadingDialog.setCancelable(true);// 可以用"返回键"取消
        mLoadingDialog.setCanceledOnTouchOutside(false);

        tvCansal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadingDialog.dismiss();
            }
        });

        mLoadingDialog.setContentView(v, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        Window window = mLoadingDialog.getWindow();
        window.setGravity(Gravity.CENTER);  //此处可以设置dialog显示的位置

        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        mLoadingDialog.show();
    }
   //退款
   public static void outOrder(final Activity activity, final int layoutPosition, final String ordernum) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View v = inflater.inflate(R.layout.outorder_dialog, null);
        TextView tvLogout = (TextView) v.findViewById(R.id.tv_logout);
        TextView tvCansal = (TextView) v.findViewById(R.id.tv_cansal);



        // 创建自定义样式dialog
        final Dialog mLoadingDialog = new Dialog(activity, R.style.loading_dialog);

        mLoadingDialog.setCancelable(true);// 可以用"返回键"取消
        mLoadingDialog.setCanceledOnTouchOutside(false);

        tvCansal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadingDialog.dismiss();
            }
        });

       tvLogout.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               RxBus.getInstance().post(new OutOrderBean(layoutPosition,ordernum));
               mLoadingDialog.dismiss();
           }
       });

        mLoadingDialog.setContentView(v, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        Window window = mLoadingDialog.getWindow();
        window.setGravity(Gravity.CENTER);  //此处可以设置dialog显示的位置

        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        mLoadingDialog.show();
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
                case R.id.tv_order_data:
                    RxBus.getInstance().post(new ChangBean("销售数据",4));
                    pw.dismiss();
                    break;
                case R.id.tv_yd:
                    RxBus.getInstance().post(new ChangBean("堂吃订单",3));
                    pw.dismiss();
                    break;
            }
        }
    }
}
