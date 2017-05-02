package com.mb.fhl.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.mb.fhl.R;
import com.mb.fhl.base.BaseActivity;
import com.mb.fhl.models.BaseBean;
import com.mb.fhl.models.OrderBean;
import com.mb.fhl.net.Api;
import com.mb.fhl.net.BaseSubscriber;
import com.mb.fhl.utils.T;
import com.mb.fhl.utils.TimeUtils;
import com.mb.fhl.utils.UserManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import butterknife.Bind;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HomeActivity extends BaseActivity implements TabLayout.OnTabSelectedListener {

    @Bind(R.id.tabs)
    TabLayout mTabs;
    @Bind(R.id.activity_xrecyc)
    XRecyclerView mActivityXrecyc;

    private String[] Titles;
    private ContentLinearAdapter mAdapter;
    private List<OrderBean.DataBean>  mData = new ArrayList<>();
    private int status = 1;
    private int page = 1;
    private String type = "";
    private String orderId;
    private int mPot;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected void initView() {

        Titles = new String[]{"待取货", "配送中", "已完成"};

        for (int i = 0; i < Titles.length; i++) {
            mTabs.addTab(mTabs.newTab().setText(Titles[i])); //添加tab
        }

        mTabs.addOnTabSelectedListener(this);
        mTabs.getTabAt(0).select();

        final LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mActivityXrecyc.setLayoutManager(manager);
        mAdapter = new ContentLinearAdapter(this,mData);
        mActivityXrecyc.setAdapter(mAdapter);
        mActivityXrecyc.setRefreshProgressStyle(ProgressStyle.BallPulse );
        mActivityXrecyc.setLoadingMoreProgressStyle(ProgressStyle.BallPulse );

        mActivityXrecyc.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                if (type!=""){
                    getOrderStstus();
                }
                else {
                    page = 1;
                    getData();
                }

            }

            @Override
            public void onLoadMore() {
                page++;
                getData();
            }
        });

        mActivityXrecyc.refresh();

    }

    @Override
    protected void releaseResource() {

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if (tab.getText().equals("待取货")){
            status = 1;
        }
        else if (tab.getText().equals("配送中")){
            status = 2;
        }
        else if (tab.getText().equals("已完成")){
            status = 3;
        }
        mActivityXrecyc.refresh();

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }


    public void getData(){
        HashMap<String, Object> params = new HashMap<>();
        params.put("phoneNum",UserManager.getIns().getUser().phoneNum);
        params.put("state", status);
        params.put("page",page);
        params.put("userId",UserManager.getIns().getUser().uid);
        params.put("token",UserManager.getIns().getUser().accessToken);
        Api.getRetrofit().getOrderList(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseBean<OrderBean>>(HomeActivity.this){
                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        mActivityXrecyc.refreshComplete();
                    }
                    @Override
                    public void onNext(BaseBean<OrderBean> orderBeanBaseBean) {
                        super.onNext(orderBeanBaseBean);
                        mData = orderBeanBaseBean.getData().data;
                        mAdapter.resetData(mData);
                        mActivityXrecyc.refreshComplete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mActivityXrecyc.refreshComplete();
                    }

                });
    }

    public void getOrderStstus(){

        HashMap<String, Object> params = new HashMap<>();

        params.put("orderId",orderId);
        params.put("phoneNum",UserManager.getIns().getUser().phoneNum);
        params.put("type", type);
        params.put("userId",UserManager.getIns().getUser().uid);
        params.put("token",UserManager.getIns().getUser().accessToken);

        String s = params.toString();

        Api.getRetrofit().dealOrder(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                 .subscribe(new BaseSubscriber<BaseBean>(HomeActivity.this){
                     @Override
                     public void onNext(BaseBean baseBean) {
                         super.onNext(baseBean);
                         type = "";
                         mAdapter.data.remove(mPot);
                         T.showLong(HomeActivity.this,"操作成功");
                         mAdapter.notifyDataSetChanged();
                         mActivityXrecyc.refreshComplete();
                     }

                     @Override
                     public void onError(Throwable e) {
                         super.onError(e);
                         mActivityXrecyc.refreshComplete();
                         type = "";
                     }

                     @Override
                     public void onCompleted() {
                         super.onCompleted();
                         type = "";
                     }
                 });

    }

    private class ContentLinearAdapter extends RecyclerView.Adapter<ContentLinearAdapter.ListHolder>{
        private Context mContext;
        public List<OrderBean.DataBean> data ;
        public ContentLinearAdapter(Context context,List<OrderBean.DataBean> data) {
            mContext = context;
            this.data = data;
        }

        public void resetData(List<OrderBean.DataBean> list){
            if (page==1){
                data = list;
            }else {
                data.addAll(list);
            }
            notifyDataSetChanged();
        }
        @Override
        public ListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View  inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_item_content1, null);
            return new ListHolder(inflate);
        }

        @Override
        public void onBindViewHolder(ListHolder holder, final int position) {

            holder.mTvTime.setText("期望送达时间"+TimeUtils.millis2String(((data.get(position).transportimeint)*1000) ,"HH:mm"));
            long time = data.get(position).transportimeint-TimeUtils.getNowTimeMills();
            if (time>0) {
                holder.mTvTimeOver.setVisibility(View.VISIBLE);
                holder.mTvTimeOver.setText((time / 60000) + "");
            }else {
                holder.mTvTimeOver.setVisibility(View.GONE);
            }
            holder.mTvName.setText(data.get(position).shopname);
            holder.mTvStarAddress.setText(data.get(position).takeadress);
            holder.mTvEndAddress.setText(data.get(position).destinationadress);
            holder.mTvPhone.setText(data.get(position).customername+": "+data.get(position).customertel);
            if (status==3){
                holder.mTvSure.setBackgroundResource(R.drawable.shape_login_bg);
            }else {
                holder.mTvSure.setBackgroundResource(R.drawable.shape_login);
            }
            holder.mTvSure.setText(statusToStr(status));

            holder.mTvSure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    orderId = data.get(position).orderid;
                    mPot = position;

                    if (status==3){
                        return;
                    }
                   else if (status==1){
                        type = "shipping";
                    }
                    else if (status==2){
                        type = "finish";
                    }

                    mActivityXrecyc.refresh();
                }
            });

            holder.mTvPhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    Uri url = Uri.parse("tel:" + data.get(position).customertel);
                    intent.setData(url);
                    startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class ListHolder extends RecyclerView.ViewHolder{

            private final TextView mTvTime;
            private final TextView mTvName;
            private final TextView mTvEndAddress;
            private final TextView mTvStarAddress;
            private final TextView mTvPhone;
            private final TextView mTvSure;
            private final TextView mTvTimeOver;

            public ListHolder(View itemView) {
                super(itemView);

                mTvTime = (TextView) itemView.findViewById(R.id.tv_time);
                mTvName = (TextView) itemView.findViewById(R.id.tv_name);
                mTvStarAddress = (TextView) itemView.findViewById(R.id.tv_star_address);
                mTvEndAddress = (TextView) itemView.findViewById(R.id.tv_end_address);
                mTvPhone = (TextView) itemView.findViewById(R.id.tv_phone);
                mTvSure = (TextView) itemView.findViewById(R.id.tv_sure);
                mTvTimeOver = (TextView) itemView.findViewById(R.id.tv_time_over);

            }

        }

    }

    public String statusToStr(int status){
        switch (status){
            case 1:
              return "待取货";
            case 2:
                return "配送中";
            case 3:
                return "已完成";
        }
        return "未知状态";

    }

}
