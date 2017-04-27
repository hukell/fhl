package com.mb.fhl.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;

/**
 * Activity基类
 */
public abstract class BaseActivity extends AppCompatActivity {
	@Override
	public final void onCreate(Bundle savedInstanceState) {
		setParams();
		super.onCreate(savedInstanceState);
		setContentView(getLayoutId());

		ButterKnife.bind(this);
		initView();
		setAdapter();
	}

	/**
	 * 在setContentView之前，设置的参数
	 */
	protected  void setParams(){};


	/**
	 * 设置适配器
	 */
	protected void setAdapter() {

	}

	/**
	 * 添加布局ID,初始化布局
	 *
	 * @return
	 */
	protected abstract int getLayoutId();

	/**
	 * 执行查找控件等基本操作
	 */
	protected abstract void initView();


	@Override
	protected void onDestroy() {
		super.onDestroy();
		releaseResource();
		ButterKnife.unbind(this);
	}

	/**
	 * 结束应用的时候释放资源
	 */
	protected abstract void releaseResource();


}

