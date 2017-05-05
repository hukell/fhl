package com.mb.fhl.ui;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mb.fhl.R;
import com.mb.fhl.base.BaseFragment;
import com.mb.fhl.models.BaseBean;
import com.mb.fhl.models.DateBean;
import com.mb.fhl.models.Deliver;
import com.mb.fhl.models.OutOrderBean;
import com.mb.fhl.models.PhoneBean;
import com.mb.fhl.models.ReFundBean;
import com.mb.fhl.models.ShopBean;
import com.mb.fhl.net.Api;
import com.mb.fhl.net.BaseSubscriber;
import com.mb.fhl.utils.DialogUtils;
import com.mb.fhl.utils.DisplayUtil;
import com.mb.fhl.utils.RxBus;
import com.mb.fhl.utils.T;
import com.mb.fhl.utils.TimeUtils;
import com.mb.fhl.utils.TypeUtils;
import com.mb.fhl.utils.UserManager;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class OrderFragment extends BaseFragment implements TabLayout.OnTabSelectedListener
        , BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {


    @Bind(R.id.gap)
    View mGap;
    @Bind(R.id.tv_price)
    TextView mTvPrice;
    @Bind(R.id.merchant_xrecyc)
    RecyclerView mRecyclerView;
    @Bind(R.id.refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.tabs)
    TabLayout mTabs;

    private String[] Titles;
    private List<ShopBean.OrderinfoBean> mOrderinfo;
    private MerchantAdapter pullToRefreshAdapter;
    private int page = 1;
    private Subscription mSubscribe1;
    private Subscription mSubscribe2;
    private int mCurrentCounter;

    private int mOrderStytle = 1;
    private int mOrderStatus = 1;
    private int PAGE_SIZE = 20;
    private Calendar mNow;
    private List<Deliver.DeliverlistBean> mDeliverlist;
    private String mDate1 = TimeUtils.getNowTimeString("yyyy-MM-dd");

    private Subscription mSubscribe3;
    private String mDater;

    public static OrderFragment newInstance(int orderStytle) {
        Bundle bundle = new Bundle();
        OrderFragment orderFragment = new OrderFragment();
        bundle.putInt("orderStytle", orderStytle);
        orderFragment.setArguments(bundle);
        return orderFragment;
    }

    @Override
    protected void init() {
        mOrderStytle = getArguments().getInt("orderStytle");
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_order;
    }

    @Override
    protected void initView(View view) {
        getDeLiverList();
        getStatus(mOrderStytle);
        final LinearLayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.color_4e76e5));

        pullToRefreshAdapter = new MerchantAdapter(R.layout.recycle_item_merchant, mOrderinfo);
        pullToRefreshAdapter.setOnLoadMoreListener(this, mRecyclerView);
        pullToRefreshAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
//        pullToRefreshAdapter.setAutoLoadMoreSize(3);
        mRecyclerView.setAdapter(pullToRefreshAdapter);
        mCurrentCounter = pullToRefreshAdapter.getData().size();
        mNow = Calendar.getInstance();
        mDater = (1 + mNow.get(Calendar.MONTH)) + "月" + mNow.get(Calendar.DAY_OF_MONTH) + "日";

        mSubscribe1 = RxBus.getInstance().toObserverable(PhoneBean.class)
                .subscribe(new Action1<PhoneBean>() {
                    @Override
                    public void call(PhoneBean phoneBean) {
                        mOrderinfo.get(phoneBean.point).deliveryinfo.get(0).deliverytel = phoneBean.deliverlistBean.deliverytel;
                        mOrderinfo.get(phoneBean.point).deliveryinfo.get(0).deliverystaff = phoneBean.deliverlistBean.deliverystaff;
                        mOrderinfo.get(phoneBean.point).deliveryinfo.get(0).deliveryid = phoneBean.deliverlistBean.deliveryid;
                        pullToRefreshAdapter.notifyItemChanged(phoneBean.point);
                    }
                });

        mSubscribe2 = RxBus.getInstance().toObserverable(OutOrderBean.class)
                .subscribe(new Action1<OutOrderBean>() {
                    @Override
                    public void call(OutOrderBean OutOrderBean) {
                        merchantOrderRefund(OutOrderBean.layoutPosition, OutOrderBean.ordernum);
                    }
                });


        mSubscribe3 = RxBus.getInstance().toObserverable(DateBean.class)
                .subscribe(new Action1<DateBean>() {
                    @Override
                    public void call(DateBean dateBean) {
                        mDate1 = dateBean.date;
                        mDater = dateBean.datar;
                        page=1;
                        getData();
                    }
                });
    }

    @Override
    protected void initOperation() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (!mSubscribe1.isUnsubscribed()){
            mSubscribe1.unsubscribe();
        }if (!mSubscribe2.isUnsubscribed()){
            mSubscribe2.unsubscribe();
        }if (!mSubscribe3.isUnsubscribed()){
            mSubscribe3.unsubscribe();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            System.out.println("不可见");

        } else {
            System.out.println("当前可见");
        }
    }

    //刷新
    @Override
    public void onRefresh() {
        page = 1;
        getData();
    }

    //加载更多  棒棒鸡
    @Override
    public void onLoadMoreRequested() {
        if (pullToRefreshAdapter.getData().size() < PAGE_SIZE) {
            pullToRefreshAdapter.loadMoreEnd(true);
        } else {
            page++;
            getData();
        }
    }

    private void getStatus(int orderStytle) {
        if (orderStytle == 1) {
            Titles = new String[]{"待处理", "进行中", "已完成","已取消"};
        } else {
            Titles = new String[]{"进行中", "已完成","已取消"};
        }
        for (int i = 0; i < Titles.length; i++) {
            mTabs.addTab(mTabs.newTab().setText(Titles[i])); //添加tab
        }
        mTabs.addOnTabSelectedListener(this);
        mTabs.getTabAt(0).select();
        getData();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if (tab.getText().equals("待处理")) {
            mOrderStatus = 1;
        } else if (tab.getText().equals("进行中")) {
            mOrderStatus = 2;
        } else if (tab.getText().equals("已完成")) {
            mOrderStatus = 3;
        }
        else if(tab.getText().equals("已取消")){
            mOrderStatus = 4;
        }
        page = 1;
        getData();
    }


    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    //得到配送人员
    private void getDeLiverList() {
        Api.getRetrofit().getDeliverList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseBean<Deliver>>(getActivity()) {
                    @Override
                    public void onNext(BaseBean<Deliver> deliverBaseBean) {
                        super.onNext(deliverBaseBean);
                        mDeliverlist = deliverBaseBean.getData().deliverlist;
                    }
                });
    }


    //请求列表数据
    public void getData() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("orderDate", mDate1);
        params.put("orderStyle", mOrderStytle);
        params.put("orderStatus", mOrderStatus);
        params.put("page", page);
        params.put("userId", UserManager.getIns().getUser().uid);
        params.put("token", UserManager.getIns().getUser().accessToken);

        Api.getRetrofit().getMerchantOrderList(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseBean<ShopBean>>(getActivity()) {
                    @Override
                    public void onNext(BaseBean<ShopBean> shopBeanBaseBean) {
                        super.onNext(shopBeanBaseBean);
                        mOrderinfo = shopBeanBaseBean.getData().orderinfo;

                         mTvPrice.setText(mDater+ TypeUtils.getString(mOrderStatus) + shopBeanBaseBean.getData().totalCount+"单，总额"+shopBeanBaseBean.getData().totalAmount+"元");
                        if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                            pullToRefreshAdapter.setEnableLoadMore(true);
                        }

                        if (page == 1) {
                            pullToRefreshAdapter.setNewData(mOrderinfo);
                        } else {
                            pullToRefreshAdapter.addData(mOrderinfo);
                            pullToRefreshAdapter.loadMoreComplete();

                        }

                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        mSwipeRefreshLayout.setEnabled(true);
                        if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                            pullToRefreshAdapter.setEnableLoadMore(true);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mSwipeRefreshLayout.setEnabled(true);
                        if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                            pullToRefreshAdapter.setEnableLoadMore(true);
                        } else {
                            pullToRefreshAdapter.loadMoreFail();
                        }
                    }
                });
    }


    //商户确认外卖订单
    public void ConfirmTakeoutOrder(final int point, String ordernum, String deliverid) {

        HashMap<String, Object> params = new HashMap<>();
        params.put("orderId", ordernum);
        params.put("deliverid", deliverid);
        params.put("userId", UserManager.getIns().getUser().uid);
        params.put("token", UserManager.getIns().getUser().accessToken);

        Api.getRetrofit().merchantConfirmTakeoutOrder(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseBean>(getActivity()) {
                    @Override
                    public void onNext(BaseBean baseBean) {
                        super.onNext(baseBean);
                        mOrderinfo.remove(point);
                        pullToRefreshAdapter.notifyDataSetChanged();
                    }
                });

    }

    //退款
    public void merchantOrderRefund(final int point, String ordernum) {

        HashMap<String, Object> params = new HashMap<>();
        params.put("orderId", ordernum);
        params.put("userId", UserManager.getIns().getUser().uid);
        params.put("token", UserManager.getIns().getUser().accessToken);

        Api.getRetrofit().merchantOrderRefund(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseBean<ReFundBean>>(getActivity()) {
                    @Override
                    public void onNext(BaseBean<ReFundBean> baseBean) {
                        super.onNext(baseBean);
                        if (baseBean.getData().isRefund.equals("1")){
                            T.showLong(getActivity(),"退款成功");
                            mOrderinfo.remove(point);
                            pullToRefreshAdapter.notifyDataSetChanged();
                        }else {
                            T.showLong(getActivity(),"退款失败");
                        }
                    }
                });
    }


    //商户打印订单  merchantPrintOrder
    public void merchantPrintOrder(final int point, String ordernum) {

        HashMap<String, Object> params = new HashMap<>();
        params.put("orderId", ordernum);
        params.put("userId", UserManager.getIns().getUser().uid);
        params.put("token", UserManager.getIns().getUser().accessToken);

        Api.getRetrofit().merchantPrintOrder(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseBean>(getActivity()) {
                    @Override
                    public void onNext(BaseBean baseBean) {
                        super.onNext(baseBean);
                    }
                });

    }

    //商户确认堂吃订单
    public void merchantCookingFinishEatinOrder(final int point, String ordernum) {

        HashMap<String, Object> params = new HashMap<>();
        params.put("orderId", ordernum);
        params.put("userId", UserManager.getIns().getUser().uid);
        params.put("token", UserManager.getIns().getUser().accessToken);

        Api.getRetrofit().merchantCookingFinishEatinOrder(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseBean>(getActivity()) {
                    @Override
                    public void onNext(BaseBean baseBean) {
                        super.onNext(baseBean);
                        mOrderinfo.remove(point);
                        pullToRefreshAdapter.notifyDataSetChanged();
                    }
                });
    }


    public class MerchantAdapter extends BaseQuickAdapter<ShopBean.OrderinfoBean, BaseViewHolder> {

        private final Drawable mDrawable;
        private final Drawable mDrawable1;

        public MerchantAdapter(@LayoutRes int layoutResId, @Nullable List<ShopBean.OrderinfoBean> data) {
            super(layoutResId, data);

            mDrawable =getResources().getDrawable(R.mipmap.img_sl);
            mDrawable1 =getResources().getDrawable(R.mipmap.img_zk);

            mDrawable.setBounds(0, 0, mDrawable.getMinimumWidth(), mDrawable.getMinimumHeight());
            mDrawable1.setBounds(0, 0, mDrawable1.getMinimumWidth(), mDrawable1.getMinimumHeight());

        }

        @Override
        protected void convert(final BaseViewHolder helper, final ShopBean.OrderinfoBean item) {
            final int layoutPosition = helper.getLayoutPosition();

            final RecyclerView recyclerView = (RecyclerView) helper.getView(R.id.item_recyclerview);
            final TextView tvSq = (TextView) helper.getView(R.id.tv_sq);
            final TextView tvPsPhone = (TextView) helper.getView(R.id.tv_ps_phone);
            final TextView tvTime = (TextView) helper.getView(R.id.tv_time);
            final TextView tvStatus = (TextView) helper.getView(R.id.tv_status);
            final TextView tvRefundRight = (TextView) helper.getView(R.id.refund_right);
            final TextView tvRefundLeft = (TextView) helper.getView(R.id.refund_left);
            final TextView tvDdh = (TextView) helper.getView(R.id.tv_ddh);
            final LinearLayout linPsName = (LinearLayout) helper.getView(R.id.lin_ps_name);
            final ImageView imgCall = (ImageView) helper.getView(R.id.img_call);
            final RelativeLayout reLeftRight = (RelativeLayout) helper.getView(R.id.re_left_right);


            tvDdh.setText("#" + item.serialnum); //流水号
            helper.setText(R.id.tv_zj, "￥" + item.goodstotal); //总价

            tvRefundLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogUtils.outOrder(getActivity(), layoutPosition, item.ordernum);
                }
            });

            if (mOrderStytle == 1) { //外卖
                linPsName.setVisibility(View.VISIBLE);
                tvTime.setVisibility(View.VISIBLE);
                long l = Long.parseLong(item.deliverytime);
                tvTime.setText(TimeUtils.millis2String2(item.deliverytime)); //送达时间
                helper.setText(R.id.tv_name, item.customername + " :"); //顾客名字
                helper.setText(R.id.tv_phone, item.customertel);
                helper.setText(R.id.tv_address, "地址:" + item.orderaddress);
                tvPsPhone.setText(item.deliveryinfo.size() != 0 ? item.deliveryinfo.get(0).deliverystaff + ": " + item.deliveryinfo.get(0).deliverytel : "暂无配送人员");

                imgCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        Uri data = Uri.parse("tel:" + (item.deliveryinfo.size() != 0 ? item.deliveryinfo.get(0).deliverytel : ""));
                        intent.setData(data);
                        startActivity(intent);

                    }
                });

                switch (mOrderStatus) {
                    case 1:
                        reLeftRight.setVisibility(View.VISIBLE);
                        tvStatus.setText("待处理");
                        tvRefundRight.setText("出菜");
                        tvRefundRight.setOnClickListener(new View.OnClickListener() {  //点击出菜
                            @Override
                            public void onClick(View v) {
                                ConfirmTakeoutOrder(layoutPosition, item.ordernum, item.deliveryinfo.size() != 0 ? item.deliveryinfo.get(0).deliveryid : "");
                            }
                        });

                        tvPsPhone.setClickable(true);
                        tvPsPhone.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DialogUtils.showPopPhoneDown((Activity) mContext, tvPsPhone, mDeliverlist, layoutPosition);
                            }
                        });
                        tvPsPhone.setCompoundDrawables(null,null,mDrawable1,null);
                        tvPsPhone.setBackgroundColor(getResources().getColor(R.color.color_e5e5e5));
                        break;
                    case 2:
                        reLeftRight.setVisibility(View.VISIBLE);
                        tvStatus.setText("进行中");
                        tvRefundRight.setText("补打订单");
                        tvPsPhone.setClickable(false);
                        //打印订单
                        tvRefundRight.setOnClickListener(new View.OnClickListener() {  //点击出菜
                            @Override
                            public void onClick(View v) {
                                merchantPrintOrder(layoutPosition, item.ordernum);
                            }
                        });
                        tvPsPhone.setCompoundDrawables(null,null,null,null);
                        tvPsPhone.setBackgroundColor(getResources().getColor(R.color.white));
                        break;
                    case 3:
                        reLeftRight.setVisibility(View.VISIBLE);
                        tvStatus.setText("已完成");
                        tvRefundRight.setText("补打订单");
                        tvPsPhone.setClickable(false);
                        //打印订单
                        tvRefundRight.setOnClickListener(new View.OnClickListener() {  //点击出菜
                            @Override
                            public void onClick(View v) {
                                merchantPrintOrder(layoutPosition, item.ordernum);
                            }
                        });

                        tvPsPhone.setCompoundDrawables(null,null,null,null);
                        tvPsPhone.setBackgroundColor(getResources().getColor(R.color.white));
                        break;

                    case 4:
                        reLeftRight.setVisibility(View.GONE);
                        tvPsPhone.setClickable(false);
                        tvStatus.setText("已取消");
                        tvPsPhone.setCompoundDrawables(null,null,null,null);
                        tvPsPhone.setBackgroundColor(getResources().getColor(R.color.white));
                        break;

                }
            } else if (mOrderStytle == 2) { //堂吃
                tvTime.setVisibility(View.GONE);
                helper.setText(R.id.tv_address, item.eatin.shopname + "  " + item.eatin.tablenumber + "桌");
                linPsName.setVisibility(View.GONE);
                helper.setText(R.id.tv_name, item.customername); //顾客名字
                helper.setText(R.id.tv_phone, "");
                switch (mOrderStatus) {
                    case 1:
                        reLeftRight.setVisibility(View.VISIBLE);
                        tvStatus.setText("待处理");
                        tvRefundRight.setText("出菜");

                        break;
                    case 2:
                        reLeftRight.setVisibility(View.VISIBLE);
                        tvStatus.setText("进行中");
                        tvRefundRight.setText("出菜完成");

                        tvRefundRight.setOnClickListener(new View.OnClickListener() {  //点击出菜
                            @Override
                            public void onClick(View v) {
                                merchantCookingFinishEatinOrder(layoutPosition, item.ordernum);
                            }
                        });

                        break;
                    case 3:
                        reLeftRight.setVisibility(View.VISIBLE);
                        tvStatus.setText("已完成");
                        tvRefundRight.setText("补打订单");
                        //打印订单
                        tvRefundRight.setOnClickListener(new View.OnClickListener() {  //点击出菜
                            @Override
                            public void onClick(View v) {
                                merchantPrintOrder(layoutPosition, item.ordernum);
                            }
                        });
                        break;

                    case 4:
                        reLeftRight.setVisibility(View.GONE);
                        tvStatus.setText("已取消");
                        break;
                }
            }

            ItemAdapter itemAdapter = new ItemAdapter(mContext, item.goods);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };

            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(itemAdapter);

            final int itemCount = itemAdapter.getItemCount() * 40;

            tvSq.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final int measuredHeight = recyclerView.getMeasuredHeight();

                    ValueAnimator anim;

                    if (measuredHeight != 0) {
                        anim = ValueAnimator.ofFloat(DisplayUtil.dip2px(mContext, itemCount), 0);
                        tvSq.setText("展开");
                        tvSq.setCompoundDrawables(null,null,mDrawable1,null);
                    } else {
                        anim = ValueAnimator.ofFloat(0, DisplayUtil.dip2px(mContext, itemCount));
                        tvSq.setText("收起");
                        tvSq.setCompoundDrawables(null,null,mDrawable,null);
                    }
                    anim.setDuration(300);
                    anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            float currentValue = (float) animation.getAnimatedValue();
                            long duration = animation.getDuration();
                            LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) recyclerView.getLayoutParams();
                            linearParams.height = (int) currentValue;// 控件的高强制设成20
                            recyclerView.setLayoutParams(linearParams); //使设置好的布局参数应用到控件
                        }
                    });
                    anim.start();
                }
            });

        }

    }

}
