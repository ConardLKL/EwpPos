package com.limahao.ticket.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.limahao.ticket.BaseApplication;
import com.limahao.ticket.MainActivity;
import com.limahao.ticket.R;
import com.limahao.ticket.utils.CommonUtil;

public class ViewPagerAdapter extends PagerAdapter {
	private List<View> views;
	private Activity activity;
	private BaseApplication mApplication;
	private static final String SHAREDPREFERENCES_NAME = "first_pref";
	private RelativeLayout.LayoutParams params;
	public ViewPagerAdapter(List<View> views,Activity activity,BaseApplication application){
		this.views = views;
		this.activity = activity;
		this.mApplication = application;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (views !=null) {
			return views.size();
		}
		return 0;
	}
	//销毁arg1位置的界面
	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		// TODO Auto-generated method stub
		((ViewPager) arg0).removeView(views.get(arg1));
	}
	@Override
    public void finishUpdate(View arg0) {
		
    }
	@Override
	public Object instantiateItem(View arg0, int arg1) {
		// TODO Auto-generated method stub
		((ViewPager) arg0).addView(views.get(arg1), 0);
        if (arg1 == views.size() - 1) {
            Button goMainBtn = (Button) arg0 .findViewById(R.id.start);
            params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        	params.addRule(RelativeLayout.CENTER_IN_PARENT);
            //在高度大于800 并且dpi很小下做的适配
        	if(CommonUtil.getScreenHeight(activity) >240 && CommonUtil.getScreenHeight(activity) <=480){
            	params.height = 35;
            	params.width = params.MATCH_PARENT;
            	params.setMargins(35, 0, 35, 55);
            	goMainBtn.setLayoutParams(params);
            }
            goMainBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 设置已经引导
                    setGuided();
                    goHome();
                }

            });
        }
        return views.get(arg1);
	}
	protected void goHome() {
		// TODO Auto-generated method stub
//	     if(mApplication.isLogin()){
//	    	 Intent intent = new Intent(activity, MainActivity.class);
//		     activity.startActivity(intent);
// 		} else {
// 			Intent intent = new Intent(activity, Login.class);
//		    activity.startActivity(intent);
// 		}
		 Intent intent = new Intent(activity, MainActivity.class);
	     activity.startActivity(intent);
	     activity.finish();
	}
	protected void setGuided() {
		// TODO Auto-generated method stub
		SharedPreferences preferences = activity.getSharedPreferences(
                SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        // 存入数据
        editor.putBoolean("isFirstIn", false);
        // 提交修改
        editor.commit();
	}
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == arg1;
	}

}
