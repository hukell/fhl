package com.mb.fhl.ui;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.mb.fhl.R;
import com.mb.fhl.adapter.ChartListFragmentAdapter;
import com.mb.fhl.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChartListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener{


    @Bind(R.id.recyclerview)
    RecyclerView mRecyclerView;
    @Bind(R.id.refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;


    public static ChartListFragment newInstance() {
        Bundle bundle = new Bundle();
        ChartListFragment chartListFragment = new ChartListFragment();
        chartListFragment.setArguments(bundle);
        return chartListFragment;
    }

    @Override
    protected void init() {

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_chart_list;
    }

    @Override
    protected void initView(View view) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 10 ; i++) {
            list.add("item");
        }

        final LinearLayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
        ChartListFragmentAdapter adapter = new ChartListFragmentAdapter(R.layout.fragment_chart_list_item,list);
        mRecyclerView.setAdapter(adapter);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.color_4e76e5));


    }

    @Override
    protected void initOperation() {

    }

    @Override
    public void onRefresh() {

    }
}
