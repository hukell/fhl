package com.mb.fhl.ui;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.mb.fhl.R;
import com.mb.fhl.base.BaseActivity;
import com.mb.fhl.models.BaseBean;
import com.mb.fhl.models.ChangBean;
import com.mb.fhl.models.PhoneBean;
import com.mb.fhl.models.ShopBean;
import com.mb.fhl.net.Api;
import com.mb.fhl.net.BaseSubscriber;
import com.mb.fhl.utils.DialogUtils;
import com.mb.fhl.utils.DisplayUtil;
import com.mb.fhl.utils.ImageLoader;
import com.mb.fhl.utils.RxBus;
import com.mb.fhl.utils.TimeUtils;
import com.mb.fhl.utils.TypeUtils;
import com.mb.fhl.utils.UserManager;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.mb.fhl.R.id.tv_change;

public class MerchantActivity extends BaseActivity implements TabLayout.OnTabSelectedListener,
        DatePickerDialog.OnDateSetListener,BaseQuickAdapter.RequestLoadMoreListener,SwipeRefreshLayout.OnRefreshListener{

    @Bind(R.id.img_head)
    ImageView mImgHead;
    @Bind(tv_change)
    TextView mTvChange;
    @Bind(R.id.tv_date)
    TextView mTvDate;
    @Bind(R.id.tabs)
    TabLayout mTabs;
    @Bind(R.id.gap)
    View mGap;
    @Bind(R.id.tv_price)
    TextView mTvPrice;
    @Bind(R.id.re_title)
    RelativeLayout mReTitle;

    @Bind(R.id.merchant_xrecyc)
    RecyclerView mRecyclerView;

    @Bind(R.id.refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    private String[] Titles;
    private int page = 1;

    private Calendar mNow;
    private int mMonthOfYear = -1;
    private int mDayOfMonth= -1;
    private int mYear= -1;
    private MerchantAdapter pullToRefreshAdapter;

    List<ShopBean.OrderinfoBean> mOrderinfo = new ArrayList<>();
    private int mCurrentCounter;
    private Subscription mSubscribe;
    private String mDate1 = TimeUtils.getNowTimeString("yyyy-MM-dd");
    private int mOrderStytle = 1;
    private int mOrderStatus = 1;
    private int  PAGE_SIZE = 20;
    private Subscription mSubscribe1;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_merchant;
    }

    @Override
    protected void initView() {
        Titles = new String[]{"待处理", "进行中", "已完成"};
        ImageLoader.loadCicleImage(this,UserManager.getIns().getUser().logo,R.mipmap.img_home,mImgHead);
        mNow = Calendar.getInstance();
        mTvDate.setText((1+mNow.get(Calendar.MONTH))+"月"+mNow.get(Calendar.DAY_OF_MONTH)+"日");

        for (int i = 0; i < Titles.length; i++) {
            mTabs.addTab(mTabs.newTab().setText(Titles[i])); //添加tab
        }

        mTabs.addOnTabSelectedListener(this);
        mTabs.getTabAt(0).select();


        final LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.color_4e76e5));

        pullToRefreshAdapter = new MerchantAdapter(R.layout.recycle_item_merchant,mOrderinfo);
        pullToRefreshAdapter.setOnLoadMoreListener(this, mRecyclerView);
        pullToRefreshAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
//        pullToRefreshAdapter.setAutoLoadMoreSize(3);
        mRecyclerView.setAdapter(pullToRefreshAdapter);
        mCurrentCounter = pullToRefreshAdapter.getData().size();


        mSubscribe = RxBus.getInstance().toObserverable(ChangBean.class)
                          .subscribe(new Action1<ChangBean>() {
                              @Override
                              public void call(ChangBean changBean) {
                                  mTvChange.setText(changBean.mString);
                                  mOrderStytle = changBean.orderStyle;
                                  page=1;
                                  getData();
                              }
                          });

        mSubscribe1 = RxBus.getInstance().toObserverable(PhoneBean.class)
                          .subscribe(new Action1<PhoneBean>() {
                              @Override
                              public void call(PhoneBean phoneBean) {
                                  String substring = phoneBean.s.substring(phoneBean.s.length() - 11, phoneBean.s.length());

                                  mOrderinfo.get(phoneBean.point).deliveryinfo.get(0).deliverytel=phoneBean.s.substring(phoneBean.s.length()-11,phoneBean.s.length());
                                  mOrderinfo.get(phoneBean.point).deliveryinfo.get(0).deliverystaff=phoneBean.s.substring(0,phoneBean.s.length()-12);
                                  pullToRefreshAdapter.notifyItemChanged(phoneBean.point);
                              }
                          });
        getData();

    }
    @Override
    protected void releaseResource() {
        if (!mSubscribe.isUnsubscribed()){
            mSubscribe.unsubscribe();
        } if (!mSubscribe1.isUnsubscribed()){
            mSubscribe1.unsubscribe();
        }
    }

    @OnClick({R.id.img_head, tv_change, R.id.tv_date})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_head:
                break;
            case tv_change:
                DialogUtils.showPopTitleDown(this,mReTitle);
                break;
            case R.id.tv_date:
                  //日历选择日期
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        MerchantActivity.this,
                        mYear!=-1?mYear:mNow.get(Calendar.YEAR),
                        mMonthOfYear!=-1?mMonthOfYear:mNow.get(Calendar.MONTH),
                        mDayOfMonth!=-1?mDayOfMonth:mNow.get(Calendar.DAY_OF_MONTH)
                );

                dpd.show(getFragmentManager(), "Datepickerdialog");

                break;
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if (tab.getText().equals("待处理")){
            mOrderStatus = 1;
        }
        else if (tab.getText().equals("进行中")){
            mOrderStatus = 2;
        }
        else if (tab.getText().equals("已完成")){
            mOrderStatus = 3;
        }
        page=1;
        getData();

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    //刷新
    @Override
    public void onRefresh() {
        page=1;
        getData();
    }

    //加载更多  棒棒鸡
    @Override
    public void onLoadMoreRequested() {
        if (pullToRefreshAdapter.getData().size() < PAGE_SIZE) {
            pullToRefreshAdapter.loadMoreEnd(true);
        }else {
            page++;
            getData();
        }
    }

    //日历选择回调
    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date =(++monthOfYear)+"月"+ dayOfMonth +"日";
        mDate1 = year+"-"+(monthOfYear)+"-"+ dayOfMonth;

        mYear = year;
        mMonthOfYear = monthOfYear-1;
        mDayOfMonth = dayOfMonth;
        mTvDate.setText(date);
        page=1;
        getData();
    }

    //请求列表数据
    public void getData(){
        HashMap<String, Object> params = new HashMap<>();
        params.put("orderDate",mDate1);
        params.put("orderStyle",mOrderStytle);
        params.put("orderStatus", mOrderStatus);
        params.put("page",page);
        params.put("userId",UserManager.getIns().getUser().uid);
        params.put("token",UserManager.getIns().getUser().accessToken);

        Api.getRetrofit().getMerchantOrderList(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseBean<ShopBean>>(MerchantActivity.this){
                    @Override
                    public void onNext(BaseBean<ShopBean> shopBeanBaseBean) {
                        super.onNext(shopBeanBaseBean);
                        mOrderinfo = shopBeanBaseBean.getData().orderinfo;
                        mTvPrice.setText(mTvDate.getText().toString()+ TypeUtils.getString(mOrderStatus)+shopBeanBaseBean.getData().totalCount+"单，总额"+shopBeanBaseBean.getData().totalAmount+"元");
                        if (mSwipeRefreshLayout!=null&&mSwipeRefreshLayout.isRefreshing()){
                            mSwipeRefreshLayout.setRefreshing(false);
                            pullToRefreshAdapter.setEnableLoadMore(true);
                        }

                        if (page == 1){
                            pullToRefreshAdapter.setNewData(mOrderinfo);
                        }else {
                            pullToRefreshAdapter.addData(mOrderinfo);
                            pullToRefreshAdapter.loadMoreComplete();

                        }

                    }
                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        mSwipeRefreshLayout.setEnabled(true);
                        if (mSwipeRefreshLayout!=null&&mSwipeRefreshLayout.isRefreshing()){
                            mSwipeRefreshLayout.setRefreshing(false);
                            pullToRefreshAdapter.setEnableLoadMore(true);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mSwipeRefreshLayout.setEnabled(true);
                        if (mSwipeRefreshLayout!=null&&mSwipeRefreshLayout.isRefreshing()){
                            mSwipeRefreshLayout.setRefreshing(false);
                            pullToRefreshAdapter.setEnableLoadMore(true);
                        }else {
                            pullToRefreshAdapter.loadMoreFail();
                        }
                    }
                });
    }



    //商户确认外卖订单
      public void ConfirmTakeoutOrder(final int point, String ordernum, String deliverid){

          HashMap<String, Object> params = new HashMap<>();
          params.put("orderId",ordernum);
          params.put("deliverid",deliverid);
          params.put("userId",UserManager.getIns().getUser().uid);
          params.put("token",UserManager.getIns().getUser().accessToken);

          Api.getRetrofit().merchantConfirmTakeoutOrder(params)
                  .subscribeOn(Schedulers.io())
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe(new BaseSubscriber<BaseBean>(MerchantActivity.this){
                      @Override
                      public void onNext(BaseBean baseBean) {
                          super.onNext(baseBean);
                          mOrderinfo.remove(point);
                          pullToRefreshAdapter.notifyDataSetChanged();
                      }
                  });

      }

      //退款
      public void merchantOrderRefund(final int point, String ordernum){

          HashMap<String, Object> params = new HashMap<>();
          params.put("orderId",ordernum);
          params.put("userId",UserManager.getIns().getUser().uid);
          params.put("token",UserManager.getIns().getUser().accessToken);

          Api.getRetrofit().merchantOrderRefund(params)
                  .subscribeOn(Schedulers.io())
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe(new BaseSubscriber<BaseBean>(MerchantActivity.this){
                      @Override
                      public void onNext(BaseBean baseBean) {
                          super.onNext(baseBean);
                          mOrderinfo.remove(point);
                          pullToRefreshAdapter.notifyDataSetChanged();
                      }
                  });

      }


      //商户打印订单  merchantPrintOrder
    public void merchantPrintOrder(final int point, String ordernum){

        HashMap<String, Object> params = new HashMap<>();
        params.put("orderId",ordernum);
        params.put("userId",UserManager.getIns().getUser().uid);
        params.put("token",UserManager.getIns().getUser().accessToken);

        Api.getRetrofit().merchantPrintOrder(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseBean>(MerchantActivity.this){
                    @Override
                    public void onNext(BaseBean baseBean) {
                        super.onNext(baseBean);
                    }
                });

    }

    //商户确认堂吃订单
    public void merchantConfirmEatinOrder(final int point, String ordernum){

        HashMap<String, Object> params = new HashMap<>();
        params.put("orderId",ordernum);
        params.put("userId",UserManager.getIns().getUser().uid);
        params.put("token",UserManager.getIns().getUser().accessToken);

        Api.getRetrofit().merchantConfirmEatinOrder(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseBean>(MerchantActivity.this){
                    @Override
                    public void onNext(BaseBean baseBean) {
                        super.onNext(baseBean);
                        mOrderinfo.remove(point);
                        pullToRefreshAdapter.notifyDataSetChanged();
                    }
                });

    }


    public class MerchantAdapter extends BaseQuickAdapter<ShopBean.OrderinfoBean, BaseViewHolder>{

        public MerchantAdapter(@LayoutRes int layoutResId, @Nullable List<ShopBean.OrderinfoBean> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(final BaseViewHolder helper, final ShopBean.OrderinfoBean item) {
            final int layoutPosition = helper.getLayoutPosition();

            final RecyclerView recyclerView = (RecyclerView) helper.getView(R.id.item_recyclerview);
            TextView tvSq = (TextView) helper.getView(R.id.tv_sq);
            final TextView tvPsPhone = (TextView) helper.getView(R.id.tv_ps_phone);
            final TextView tvTime = (TextView) helper.getView(R.id.tv_time);
            final TextView tvStatus = (TextView) helper.getView(R.id.tv_status);
            final TextView tvRefundRight = (TextView) helper.getView(R.id.refund_right);
            final TextView tvRefundLeft = (TextView) helper.getView(R.id.refund_left);
            final TextView tvDdh = (TextView) helper.getView(R.id.tv_ddh);
            final LinearLayout linPsName = (LinearLayout) helper.getView(R.id.lin_ps_name);

            tvDdh.setText("#"+item.serialnum); //流水号
            helper.setText(R.id.tv_zj,"￥"+item.goodstotal); //总价

            tvRefundLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    merchantOrderRefund(layoutPosition,item.ordernum);
                }
            });

            if (mOrderStytle==1){ //外卖
                linPsName.setVisibility(View.VISIBLE);
                tvTime.setVisibility(View.VISIBLE);
                long l = Long.parseLong(item.deliverytime);
                tvTime.setText(TimeUtils.millis2String2(item.deliverytime)); //送达时间
                helper.setText(R.id.tv_name,item.customername+" :"); //顾客名字
                helper.setText(R.id.tv_phone,item.customertel);
                helper.setText(R.id.tv_address,"地址");
                tvPsPhone.setText(item.deliveryinfo.size()!=0?item.deliveryinfo.get(0).deliverystaff+": "+item.deliveryinfo.get(0).deliverytel:"暂无配送人员");

                switch (mOrderStatus){
                    case 1:
                        tvStatus.setText("待处理");
                        tvRefundRight.setText("出菜");
                        tvRefundRight.setOnClickListener(new View.OnClickListener() {  //点击出菜
                            @Override
                            public void onClick(View v) {
                                ConfirmTakeoutOrder(layoutPosition,item.ordernum,item.deliveryinfo.size()!=0?item.deliveryinfo.get(0).deliveryid:"");
                            }
                        });


                        tvPsPhone.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DialogUtils.showPopPhoneDown((Activity) mContext,tvPsPhone,item.deliveryinfo,layoutPosition);
                            }
                        });
                        break;
                    case 2:
                        tvStatus.setText("进行中");
                        tvRefundRight.setText("补打订单");
                        //打印订单
                        tvRefundRight.setOnClickListener(new View.OnClickListener() {  //点击出菜
                            @Override
                            public void onClick(View v) {
                                merchantPrintOrder(layoutPosition,item.ordernum);
                            }
                        });
                        break;
                    case 3:
                        tvStatus.setText("已完成");
                        tvRefundRight.setText("补打订单");
                        //打印订单
                        tvRefundRight.setOnClickListener(new View.OnClickListener() {  //点击出菜
                            @Override
                            public void onClick(View v) {
                                merchantPrintOrder(layoutPosition,item.ordernum);
                            }
                        });
                        break;
                }
            }
              else if (mOrderStytle==2){ //堂吃
                tvTime.setVisibility(View.GONE);
                helper.setText(R.id.tv_address,item.eatin.shopname+"  "+item.eatin.tablenumber+"桌");
                linPsName.setVisibility(View.GONE);
                helper.setText(R.id.tv_name,item.customername); //顾客名字
                helper.setText(R.id.tv_phone,"");
                 switch (mOrderStatus){
                     case 1:
                         tvStatus.setText("待处理");
                         tvRefundRight.setText("出菜");
                         tvRefundRight.setOnClickListener(new View.OnClickListener() {  //点击出菜
                             @Override
                             public void onClick(View v) {
                                 merchantConfirmEatinOrder(layoutPosition,item.ordernum);
                             }
                         });
                         break;
                     case 2:
                         tvStatus.setText("进行中");
                         tvRefundRight.setText("出菜完成");
                         break;
                     case 3:
                         tvStatus.setText("已完成");
                         tvRefundRight.setText("补打订单");
                         break;
                 }
            }

            ItemAdapter itemAdapter = new ItemAdapter(mContext,item.goods);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };

            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(itemAdapter);

            final int itemCount = itemAdapter.getItemCount()*40;

            tvSq.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final int measuredHeight = recyclerView.getMeasuredHeight();

                    ValueAnimator anim;

                    if (measuredHeight != 0){
                        anim = ValueAnimator.ofFloat(DisplayUtil.dip2px(mContext,itemCount), 0);
                    }else {
                        anim = ValueAnimator.ofFloat(0, DisplayUtil.dip2px(mContext,itemCount));
                    }
                    anim.setDuration(300);
                    anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            float currentValue = (float) animation.getAnimatedValue();
                            long duration = animation.getDuration();
                            LinearLayout.LayoutParams linearParams =(LinearLayout.LayoutParams) recyclerView.getLayoutParams();
                            linearParams.height = (int)currentValue;// 控件的高强制设成20
                            recyclerView.setLayoutParams(linearParams); //使设置好的布局参数应用到控件
                        }
                    });
                    anim.start();
                }
            });

        }

    }

}

