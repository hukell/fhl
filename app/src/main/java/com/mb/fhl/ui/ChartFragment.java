package com.mb.fhl.ui;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.mb.fhl.R;
import com.mb.fhl.base.BaseFragment;
import com.mb.fhl.models.BaseBean;
import com.mb.fhl.models.DayBean;
import com.mb.fhl.net.Api;
import com.mb.fhl.net.BaseSubscriber;
import com.mb.fhl.utils.MPChartHelper;
import com.mb.fhl.utils.TimeUtils;
import com.mb.fhl.utils.UserManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ChartFragment extends BaseFragment implements RadioGroup.OnCheckedChangeListener{

    @Bind(R.id.lineChart)
    LineChart mLineChart;
    @Bind(R.id.radiogroup)
    RadioGroup mRadiogroup;
    @Bind(R.id.tv_day_month)
    TextView mTvDayMonth;
    private List<String> xAxisValues;
    private List<Float> yAxisValues;

    public static ChartFragment newInstance() {
        Bundle bundle = new Bundle();
        ChartFragment chartFragment = new ChartFragment();
        chartFragment.setArguments(bundle);
        return chartFragment;
    }

    @Override
    protected void init() {
    }
    @Override
    public int getLayoutId() {
        return R.layout.fragment_chart;
    }
    @Override
    protected void initView(View view) {
        initData("month");
        mRadiogroup.setOnCheckedChangeListener(this);
    }

    @Override
    protected void initOperation() {

    }

    private void initData(String type) {
        xAxisValues = new ArrayList<>();
        yAxisValues = new ArrayList<>();

        HashMap<String, Object> params = new HashMap<>();
        params.put("userId", UserManager.getIns().getUser().uid);
        params.put("token", UserManager.getIns().getUser().accessToken);
        params.put("dataType", type);
        params.put("data", TimeUtils.getNowTimeMills() / 1000);

        Api.getRetrofit().getMerchantStat(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseBean<DayBean>>(getActivity()) {
                    @Override
                    public void onNext(BaseBean<DayBean> dayBean) {
                        super.onNext(dayBean);
                        List<DayBean.OrderAmountBean> order_amount = dayBean.getData().order_amount;

                        for (int i = 0; i < order_amount.size(); ++i) {
                            xAxisValues.add(order_amount.get(i).getDay());
                        }
                        for (int i = 0; i < order_amount.size(); ++i) {
                            yAxisValues.add(Float.parseFloat(order_amount.get(i).orderamount));
                        }

                        MPChartHelper.setLineChart(mLineChart, xAxisValues, yAxisValues, "销售量走势图", false);
                    }
                });
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId){
            case R.id.ra_month:
                xAxisValues = null;
                initData("month");
                mTvDayMonth.setText("日");
                break;
            case R.id.ra_quarter:
                yAxisValues= null;
                initData("year");
                mTvDayMonth.setText("月");
                break;
        }
    }
}
