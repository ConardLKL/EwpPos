package com.limahao.ticket.fragment;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.limahao.ticket.R;
import com.limahao.ticket.log.DebugLog;

/**
 * 
 * @className FragmentTabAdapter
 * @author wdg
 * @date 2014-1-7下午5:38:22
 * @类描述
 */
public class FragmentTabAdapter implements RadioGroup.OnCheckedChangeListener {
	private String Tag = "FragmentTabAdapter";
	private List<Fragment> fragments; // 一个tab页面对应一个Fragment
	private RadioGroup rgs; // 用于切换tab
	private FragmentActivity fragmentActivity; // Fragment所属的Activity
	private int fragmentContentId; // Activity中所要被替换的区域的id

	private int currentTab; // 当前Tab页面索引

	private OnRgsExtraCheckedChangedListener onRgsExtraCheckedChangedListener; // 用于让调用者在切换tab时候增加新的功能

	public FragmentTabAdapter(FragmentActivity fragmentActivity,
			List<Fragment> fragments, int fragmentContentId, RadioGroup rgs) {
		this.fragments = fragments;
		this.rgs = rgs;
		this.fragmentActivity = fragmentActivity;
		this.fragmentContentId = fragmentContentId;

		// 默认显示第一页
		FragmentTransaction ft = fragmentActivity.getSupportFragmentManager()
				.beginTransaction();
		Fragment ff = fragmentActivity.getSupportFragmentManager().findFragmentById(fragmentContentId);
		if( ff != null){
			DebugLog.d(Tag, "发现已经存在的fragmentContentId" + fragmentContentId);
			ft.remove(ff);
		}
	
		ft.add(fragmentContentId, fragments.get(0));

		ft.commitAllowingStateLoss();

		
		rgs.setOnCheckedChangeListener(this);

	}
	@Override
	public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
		int j = 0;
		for (int i = 0; i < rgs.getChildCount(); i++) {
			if (rgs.getChildAt(i) instanceof RadioButton) {
				if (rgs.getChildAt(i).getId() == checkedId) {
					Fragment fragment = fragments.get(j);
					FragmentTransaction ft = obtainFragmentTransaction(j);
					getCurrentFragment().onPause(); // 暂停当前tab
					// getCurrentFragment().onStop(); // 暂停当前tab
					if (fragment.isAdded()) {
						// fragment.onStart(); // 启动目标tab的onStart()
						fragment.onResume(); // 启动目标tab的onResume()
					} else {
						ft.add(fragmentContentId, fragment);
						ft.commitAllowingStateLoss();
					}
					showTab(j); // 显示目标tab
					// 如果设置了切换tab额外功能功能接口
					if (null != onRgsExtraCheckedChangedListener) {
						onRgsExtraCheckedChangedListener
								.OnRgsExtraCheckedChanged(radioGroup,
										checkedId, j);
					}

				}
				j++;
			}
		}

	}

	public void SwithFragment(int idx) {
		Fragment fragment = fragments.get(idx);
		FragmentTransaction ft = obtainFragmentTransaction(idx);
		getCurrentFragment().onPause(); // 暂停当前tab
		RadioButton button = null;
		if (rgs.getChildAt(idx) instanceof RadioButton) {
			button = (RadioButton) rgs.getChildAt(idx);
		} else {
			for (int i = idx + 1; i < rgs.getChildCount(); i++) {
				if(rgs.getChildAt(i) instanceof RadioButton){
					button = (RadioButton) rgs.getChildAt(i);
					break;
				}
			}
		}
		if (fragment.isAdded()) {
			fragment.onResume(); // 启动目标tab的onResume()
		} else {
			ft.add(fragmentContentId, fragment);
		}
		if(button != null){
			button.setChecked(true);
		}
		showTab(idx); // 显示目标tab
	}

	/**
	 * 切换tab
	 * 
	 * @param idx
	 */

	public void showTab(int idx) {
		for (int i = 0; i < fragments.size(); i++) {
			Fragment fragment = fragments.get(i);
			FragmentTransaction ft = obtainFragmentTransaction(idx);

			if (idx == i) {
				ft.show(fragment);
			} else {
				ft.hide(fragment);
			}
			ft.commitAllowingStateLoss();
		}
		currentTab = idx; // 更新目标tab为当前tab
	}

	/**
	 * 获取一个带动画的FragmentTransaction
	 * 
	 * @param index
	 * @return
	 */
	private FragmentTransaction obtainFragmentTransaction(int index) {
		FragmentTransaction ft = fragmentActivity.getSupportFragmentManager()
				.beginTransaction();
		// 设置切换动画
		if (index > currentTab) {
			ft.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out);
		} else {
			ft.setCustomAnimations(R.anim.push_right_in, R.anim.push_right_out);
		}
		return ft;
	}

	public int getCurrentTab() {
		return currentTab;
	}
	public Fragment getCurrentFragment() {
		return fragments.get(currentTab);
	}

	public OnRgsExtraCheckedChangedListener getOnRgsExtraCheckedChangedListener() {
		return onRgsExtraCheckedChangedListener;
	}

	public void setOnRgsExtraCheckedChangedListener(
			OnRgsExtraCheckedChangedListener onRgsExtraCheckedChangedListener) {
		this.onRgsExtraCheckedChangedListener = onRgsExtraCheckedChangedListener;
	}

	/**
	 * 切换tab额外功能功能接口
	 */
	public static class OnRgsExtraCheckedChangedListener {
		public void OnRgsExtraCheckedChanged(RadioGroup radioGroup,
				int checkedId, int index) {

		}
	}

}
