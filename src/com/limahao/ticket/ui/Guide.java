package com.limahao.ticket.ui;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.limahao.ticket.BaseActivity;
import com.limahao.ticket.R;
import com.limahao.ticket.adapter.ViewPagerAdapter;
import com.limahao.ticket.log.DebugLog;

public class Guide extends BaseActivity implements OnPageChangeListener {
	private static final String TAG = "Guide";
	private ViewPager vPager;
	private ViewPagerAdapter vpAdapter;
	private List<View> views;
	// 底部小点图片
    private ImageView[] dots;
    // 记录当前选中位置
    private int currentIndex;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.guide);
		initViews();
		
		// 初始化底部小点
        initDots();
	}

	private void initDots() {
		// TODO Auto-generated method stub
		 LinearLayout ll = (LinearLayout) findViewById(R.id.ll);

	        dots = new ImageView[views.size()];

	        // 循环取得小点图片
	        for (int i = 0; i < views.size(); i++) {
	            dots[i] = (ImageView) ll.getChildAt(i);
	            dots[i].setEnabled(true);// 都设为灰色
	        }

	        currentIndex = 0;
	        dots[currentIndex].setEnabled(false);// 设置为白色，即选中状态
	}

	private void initViews() {
		// TODO Auto-generated method stub
		vPager = (ViewPager) findViewById(R.id.viewpager);
		LayoutInflater inflater = LayoutInflater.from(Guide.this);
		views = new ArrayList<View>();
		views.add(inflater.inflate(R.layout.guide_one, null));
		views.add(inflater.inflate(R.layout.guide_two, null));
		views.add(inflater.inflate(R.layout.guide_there, null));
		views.add(inflater.inflate(R.layout.guide_start, null));
		vpAdapter = new ViewPagerAdapter(views, this,mApplication);
		vPager.setAdapter(vpAdapter);

		vPager.setOnPageChangeListener(this);
	}

	// 当滑动状态改变时调用
	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		DebugLog.i(TAG, "arg0=" + arg0);
	}
	// 当当前页面被滑动时调用
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		DebugLog.i(TAG, "arg0" + arg0 + "arg1" + arg1 + "arg2" + arg2);
	}
	// 当新的页面被选中时调用
	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		setCurrentDot(arg0);
	}
	private void setCurrentDot(int position) {
		// TODO Auto-generated method stub
		 if (position < 0 || position > views.size() - 1
	                || currentIndex == position) {
	            return;
	        }

	        dots[position].setEnabled(false);
	        dots[currentIndex].setEnabled(true);

	        currentIndex = position;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void findView() {
		// TODO Auto-generated method stub
		
	}

}
