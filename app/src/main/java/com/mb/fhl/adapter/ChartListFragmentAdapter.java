package com.mb.fhl.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mb.fhl.R;

import java.util.List;

/**
 * Created by Administrator on 2017/4/27 0027.
 */

public class ChartListFragmentAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public ChartListFragmentAdapter(@LayoutRes int layoutResId, @Nullable List<String> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
    }
}
