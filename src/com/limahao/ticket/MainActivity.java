package com.limahao.ticket;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.limahao.ticket.config.Constants;
import com.limahao.ticket.fragment.TabHome;
import com.limahao.ticket.fragment.TabLogin;
import com.limahao.ticket.fragment.TabMore;
import com.limahao.ticket.fragment.TabPerson;
import com.limahao.ticket.log.DebugLog;
import com.umeng.update.UmengUpdateAgent;

public class MainActivity extends BaseActivity {
	private final String TAG = "MainActivity";
	private int keyBackClickCount = 0;
	private RadioGroup rgs;
	private View vNotice;

	private Fragment tabHome;
	private Fragment tabLogin;
	private TabPerson tabPersion;
	private Fragment tabMore;

	private Map<Fragment, Integer> tabIndex; // tab顺序
	private Fragment currentTab; // 按钮按下去时被选中的tab
	private Fragment preTab; // 上一次被选中的tab

	// 界面状态控制
	public Handler mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DebugLog.i(TAG, "onCreate");
		setContentView(R.layout.activity_main);
		UmengUpdateAgent.update(this); // 在wifi条件下友盟自动更新
		findView();
		init();
	}

	@Override
	protected void findView() {
		vNotice = findViewById(R.id.tabs_iv_msg_notice); // 通知图标
		rgs = (RadioGroup) findViewById(R.id.tabs_rg);
	}

	/**
	 * 初期化
	 */
	private void init() {
		// 初期化登录状态
		mApplication.initLogin();
		
		// 初期化frangment 默认选择首页
		tabIndex = new HashMap<Fragment, Integer>();
		rgs.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup rg, int id) {
				swichTab(id);
			}
		});
		swichTab(R.id.tabs_rb_home);

		// 初期化消息通知图标放在初期化tab后面
		initNotice();

		// 界面状态控制
		mHandler = new Handler() {
			@Override
			public void dispatchMessage(Message msg) {
				super.dispatchMessage(msg);
				switch (msg.what) {
					// 有消息通知
					case Constants.ActionCode.ACT_MESSAGE_NOTICE:
						mApplication.setNotice(true);
						vNotice.setVisibility(View.VISIBLE);;
						noticePerson(msg.what);
						break;
					// 取消消息通知
					case Constants.ActionCode.ACT_MESSAGE_CANCEL:
						mApplication.setNotice(false);
						vNotice.setVisibility(View.GONE);
						noticePerson(msg.what);
						break;
					// 切换下登录状态
					case Constants.ActionCode.ACT_LOGIN_SWICH:
						switchLongState();
						break;
					default:
						break;
				}
			}
		};
	}

	/**
	 * 初期化消息通知图标
	 */
	private void initNotice() {
		// 处于登录状态并且有通知消息则展示消息图标，否则隐藏
		if (mApplication.isLogin() && mApplication.isNotice()) {
			// 有消息通知
			vNotice.setVisibility(View.VISIBLE);
			noticePerson(Constants.ActionCode.ACT_MESSAGE_NOTICE);
		} else {
			// 取消消息通知
			vNotice.setVisibility(View.GONE);
			noticePerson(Constants.ActionCode.ACT_MESSAGE_CANCEL);
		}
	}
	
	/**
	 * 刷新个人中心中订单消息的状态
	 * @param msg
	 */
	private void noticePerson(int what){
		// 刷新个人中心中订单消息的状态
		if(tabPersion != null && tabPersion.mHandler != null){
			Message msg = new Message();
			msg.what = what;
			tabPersion.mHandler.sendMessage(msg);
		}
	}
	
	/**
	 * 展示tab
	 * 
	 * @param id
	 */
	public void swichTab(int id) {

		switch (id) {
		// RadioGroup已设置为默认选中第一个 tabs_rb_home
		case R.id.tabs_rb_home:
			if (tabHome == null) {
				tabHome = new TabHome();
				tabIndex.put(tabHome, 0);
				currentTab = tabHome; // 默认选中第一个
			}
			preTab = currentTab; // 保存上一个被选中tab
			currentTab = tabHome;
			switchFragment(currentTab); // 切换fragment
			break;

		case R.id.tabs_rb_person:
			if (!mApplication.isLogin()) { // 判断登录状态
				if (tabLogin == null) {
					tabLogin = new TabLogin();
					tabIndex.put(tabLogin, 1);
				}
				preTab = currentTab; // 保存上一个被选中tab
				currentTab = tabLogin;
			} else {
				if (tabPersion == null) {
					tabPersion = new TabPerson();
					tabIndex.put(tabPersion, 2);
				}
				preTab = currentTab; // 保存上一个被选中tab
				currentTab = tabPersion;
			}
			switchFragment(currentTab); // 切换fragment
			break;
		case R.id.tabs_rb_more:
			if (tabMore == null) {
				tabMore = new TabMore();
				tabIndex.put(tabMore, 3);
			}
			preTab = currentTab; // 保存上一个被选中tab
			currentTab = tabMore;
			switchFragment(currentTab); // 切换fragment
			break;
		default:
			break;
		}

	}
	// 当前选中的是第二个tab 登录或者注销是切换 登录tab和个人tab
	private void switchLongState() {
		// 当前选中的是第二个tab无需切换动画
		if (mApplication.isLogin() && currentTab.equals(tabLogin)) { // 当前选中的是第二个tab并处于登录状态
			DebugLog.d(TAG, "登录切换");
			if (tabPersion == null) {
				tabPersion = new TabPerson();
				tabIndex.put(tabPersion, 1);
			}
			currentTab = tabPersion;
			switchFragment(currentTab);
		} else if (!mApplication.isLogin() && currentTab.equals(tabPersion)) { // 当前选中的是第二个tab并处于注销状态
			DebugLog.d(TAG, "注销切换");
			if (tabLogin == null) {
				tabLogin = new TabLogin();
				tabIndex.put(tabLogin, 2);
			}
			currentTab = tabLogin;
			switchFragment(currentTab);
		} else {
			// 其他情况暂不处理
			DebugLog.d(TAG, "其他切换");
		}

		// 重新初期化消息通知图标
		initNotice();
	}

	public void switchFragment(Fragment currentTab) {

		FragmentTransaction ft = obtainFragmentTransaction(tabIndex
				.get(currentTab));
		if (preTab != null && !preTab.equals(currentTab)) {
			preTab.onPause(); // 暂停上次选中的tab
		}

		if (currentTab.isAdded()) {
			currentTab.onResume(); // 启动目标tab的onResume()
		} else {
			ft.add(R.id.tab_content, currentTab);
			ft.commitAllowingStateLoss();
		}

		showTab(currentTab); // 显示目标tab
	}

	/**
	 * 切换tab
	 * 
	 * @param currentTab
	 */
	public void showTab(Fragment currentTab) {
		// 循环显示tab
		for (Map.Entry<Fragment, Integer> tab : tabIndex.entrySet()) {
			FragmentTransaction ft = obtainFragmentTransaction(tabIndex
					.get(currentTab));
			if (tab.getKey().equals(currentTab)) {
				ft.show(tab.getKey());
			} else {
				ft.hide(tab.getKey());
			}
			ft.commitAllowingStateLoss();
		}
	}

	/**
	 * 获取一个带动画的FragmentTransaction
	 * 
	 * @param index
	 * @return
	 */
	private FragmentTransaction obtainFragmentTransaction(int index) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		// 设置切换动画
		if (index > tabIndex.get(preTab)) {
			DebugLog.d(TAG, "push_left_in");
			ft.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out);
		} else {
			DebugLog.d(TAG, "push_right_in");
			ft.setCustomAnimations(R.anim.push_right_in, R.anim.push_right_out);
		}
		return ft;
	}

	@Override
	public void onResume() {
		super.onResume();

		// // 判断是否有网络
		// if (NetWorkHelper.isNetworkAvailable(this)) {
		// // 提示网络没连接
		// showShortToast(getResources().getString(R.string.httpisNull));
		// }
	}

	@Override
	protected void onStop() {
		// 如果您同时使用了手动更新和自动检查更新，请加上下面这句代码，因为这些配置是全局静态的。
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.setUpdateAutoPopup(true);
		UmengUpdateAgent.setUpdateListener(null);
		UmengUpdateAgent.setDownloadListener(null);
		UmengUpdateAgent.setDialogListener(null);
		DebugLog.i(TAG, "onStop");
		super.onStop();
	}
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		DebugLog.i(TAG, "onPause");
		super.onPause();
	}
	
	/*	super.onSaveInstanceState(outState);
	 *  推测  super.onSaveInstanceState(outState) 能保存当用第三方软件清除内存时fragment不被销毁
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		DebugLog.i(TAG, "outState");
		// 用第三方杀进程的软件时重新加载Activity 不需要保存任何东西，不如fragment会乱掉
		// super.onSaveInstanceState(outState);
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			switch (keyBackClickCount++) {
			case 0:
				showShortToast(getResources().getString(R.string.press_again_exit));
				Timer timer = new Timer();
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						keyBackClickCount = 0;
					}
				}, 3000);
				break;
			case 1:
				// 退出
				AppManager.getAppManager().AppExit(MainActivity.this);
				break;
			default:
				break;
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}
}
