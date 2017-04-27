package com.mb.fhl.ui;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.mb.fhl.R;
import com.mb.fhl.adapter.MerchantAdapter;
import com.mb.fhl.base.BaseActivity;
import com.mb.fhl.models.BaseBean;
import com.mb.fhl.models.ChangBean;
import com.mb.fhl.models.OrderBean;
import com.mb.fhl.models.ShopBean;
import com.mb.fhl.net.Api;
import com.mb.fhl.net.BaseSubscriber;
import com.mb.fhl.utils.DialogUtils;
import com.mb.fhl.utils.RxBus;
import com.mb.fhl.utils.TimeUtils;
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

import static android.R.attr.name;
import static android.R.attr.type;
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


    @Override
    protected int getLayoutId() {
        return R.layout.activity_merchant;
    }

    @Override
    protected void initView() {
        Titles = new String[]{"待处理", "进行中", "已完成"};
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
        getData();

    }

    @Override
    protected void releaseResource() {
        if (!mSubscribe.isUnsubscribed()){
            mSubscribe.unsubscribe();
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
        pullToRefreshAdapter.setEnableLoadMore(false);
        page=1;
        getData();
    }

    //加载更多  棒棒鸡
    @Override
    public void onLoadMoreRequested() {
        mSwipeRefreshLayout.setEnabled(false);
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
        mDate1 = year+"-"+(++monthOfYear)+"-"+ dayOfMonth +"日";

        mYear = year;
        mMonthOfYear = monthOfYear-1;
        mDayOfMonth = dayOfMonth;
        mTvDate.setText(date);
        page=1;
        getData();
    }

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



                        if (mSwipeRefreshLayout!=null&&mSwipeRefreshLayout.isRefreshing()){
                            mSwipeRefreshLayout.setRefreshing(false);
                            pullToRefreshAdapter.setEnableLoadMore(true);
                        }

                        if (page == 1){
                            pullToRefreshAdapter.setNewData(mOrderinfo);
                        }else {
                            pullToRefreshAdapter.addData(mOrderinfo);
                            pullToRefreshAdapter.loadMoreComplete();
                            mSwipeRefreshLayout.setEnabled(true);
                        }

                    }
                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        if (mSwipeRefreshLayout!=null&&mSwipeRefreshLayout.isRefreshing()){
                            mSwipeRefreshLayout.setRefreshing(false);
                            pullToRefreshAdapter.setEnableLoadMore(true);
                        }else {
                            Toast.makeText(MerchantActivity.this, "加载失败", Toast.LENGTH_LONG).show();
                            pullToRefreshAdapter.loadMoreFail();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if (mSwipeRefreshLayout!=null&&mSwipeRefreshLayout.isRefreshing()){
                            mSwipeRefreshLayout.setRefreshing(false);
                            pullToRefreshAdapter.setEnableLoadMore(true);
                        }else {
                            Toast.makeText(MerchantActivity.this, "加载失败", Toast.LENGTH_LONG).show();
                            pullToRefreshAdapter.loadMoreFail();
                        }
                    }
                });

    }




    /*private class ContentLinearAdapter extends RecyclerView.Adapter<ContentLinearAdapter.ListHolder>{
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
            View  inflate = LayoutInflater.from(mContext).inflate(R.layout.recycle_item_merchant, parent, false);
            return new ListHolder(inflate);
        }

        @Override
        public void onBindViewHolder(final ListHolder holder, final int position) {

           ItemAdapter itemAdapter = new ItemAdapter(mContext);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };

            holder.mRecyclerView.setLayoutManager(linearLayoutManager);
            holder.mRecyclerView.setAdapter(itemAdapter);

            final int itemCount = itemAdapter.getItemCount()*40;

            holder.tvSq.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final int measuredHeight = holder.mRecyclerView.getMeasuredHeight();

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
                            LinearLayout.LayoutParams linearParams =(LinearLayout.LayoutParams) holder.mRecyclerView.getLayoutParams();
                            linearParams.height = (int)currentValue;// 控件的高强制设成20
                            holder.mRecyclerView.setLayoutParams(linearParams); //使设置好的布局参数应用到控件
                        }
                    });
                    anim.start();
                }
            });


        }

        @Override
        public int getItemCount() {
            return 6;
        }

        class ListHolder extends RecyclerView.ViewHolder{


            private final RecyclerView mRecyclerView;
            private final TextView tvSq;

            public ListHolder(View itemView) {
                super(itemView);
                mRecyclerView = (RecyclerView) itemView.findViewById(R.id.item_recyclerview);
                tvSq = (TextView) itemView.findViewById(R.id.tv_sq);

            }

        }

        private class ClickListener implements View.OnClickListener {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.tv_sq:

                        break;
                }
            }
        }
    }*/
}
