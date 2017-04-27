package com.mb.fhl.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mb.fhl.R;

/**
 * Created by Administrator on 2017/4/26 0026.
 */

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ListHolder>{
    private Context mContext;

    public ItemAdapter(Context context) {
        mContext = context;
    }

    @Override
    public ItemAdapter.ListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View  inflate = LayoutInflater.from(mContext).inflate(R.layout.recycle_item_item, parent, false);
        return new ListHolder(inflate);
    }

    @Override
    public void onBindViewHolder(ItemAdapter.ListHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    class ListHolder extends RecyclerView.ViewHolder{


        public ListHolder(View itemView) {
            super(itemView);

        }

    }
}
