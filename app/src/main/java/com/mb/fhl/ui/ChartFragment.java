package com.mb.fhl.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.mb.fhl.R;
import com.mb.fhl.base.BaseFragment;
import com.mb.fhl.utils.MPChartHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChartFragment extends BaseFragment {


    @Bind(R.id.lineChart)
    LineChart mLineChart;
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
        initData();
        MPChartHelper.setLineChart(mLineChart,xAxisValues,yAxisValues,"折线图（单）",false);
    }

    @Override
    protected void initOperation() {

    }

    private void initData(){
        xAxisValues = new ArrayList<>();
        yAxisValues = new ArrayList<>();
        for(int i=1;i<11;++i){
            if (i == 10){
                xAxisValues.add(String.valueOf(i)+"     日");
            }else {
                xAxisValues.add(String.valueOf(i));
            }
        }
            yAxisValues.add((float)50);
            yAxisValues.add((float)10);
            yAxisValues.add((float)50);
            yAxisValues.add((float)30);
            yAxisValues.add((float)50);
            yAxisValues.add((float)90);
            yAxisValues.add((float)100);
            yAxisValues.add((float)60);
            yAxisValues.add((float)10);
            yAxisValues.add((float)70);

    }

}
