package com.limahao.ticket.view;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.os.CountDownTimer;
import android.widget.TextView;

public class CountTimer {

	public static final int Displey_Second = 0;
	public static final int Displey_Second_Minute = 1;

	private TextView countView;
	private CountTimerIN timerIN;
	private boolean isCount = false;
	private long time = 0;
	private String compeletText = "";
	private String countText = "";
	private int type;

	/**
	 * 
	 * @param countView
	 *            需要计时的TextView
	 * @param time
	 *            计时时间 毫秒
	 * @param compeletText
	 *            计时完后显示的内容
	 * @param countText
	 *            计时过程中评价内容 不需要时传空-- 例: 剩余 10 传入 剩余 %s
	 */
	public CountTimer(TextView countView, long time, String compeletText,
			String countText) {
		this(countView, time, compeletText, countText, Displey_Second);
	}

	/**
	 * 
	 * @param countView
	 *            需要计时的TextView
	 * @param time
	 *            计时时间 必须是分秒格式mm:ss
	 * @param compeletText
	 *            计时完后显示的内容
	 * @param countText
	 *            计时过程中评价内容 不需要时传空-- 例: 剩余 10 传入 剩余 %s
	 * @param type
	 *            显示方式 Displey_Second：10 Displey_Second_Minute 00:10
	 */
	public CountTimer(TextView countView, String mmss, String compeletText,
			String countText, int type) {
		String[] ms = mmss.split(":");
		this.time = (Integer.valueOf(ms[0]) * 60 + Integer.valueOf(ms[1])) * 1000; // 将时分格式转换为毫秒
		this.countView = countView;
		this.type = type;
		if (compeletText != null) {
			this.compeletText = compeletText;
		}
		if (countText != null) {
			this.countText = countText;
		}
	}

	/**
	 * 
	 * @param countView
	 *            需要计时的TextView
	 * @param time
	 *            计时时间 毫秒
	 * @param compeletText
	 *            计时完后显示的内容
	 * @param countText
	 *            计时过程中评价内容 不需要时传空-- 例: 剩余 10 传入 剩余 %s
	 * @param type
	 *            显示方式 Displey_Second：10 Displey_Second_Minute 00:10
	 */
	public CountTimer(TextView countView, long time, String compeletText,
			String countText, int type) {
		this.countView = countView;
		this.time = time;
		this.type = type;
		if (compeletText != null) {
			this.compeletText = compeletText;
		}
		if (countText != null) {
			this.countText = countText;
		}
	}

	// 获取系统时间
	public String GetSystime() {
		String systime;
		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = c.getTime();
		systime = df.format(date);
		return systime;
	}

	/**
	 * 开始计时
	 */
	public void Start() {
		Start(time);
	}

	private void Start(Long t) {
		// 如果为0 计时太快状态放后面设置会覆盖，所以状态设置放前面
		countView.setEnabled(false);
		isCount = true;
		
		timerIN = new CountTimerIN(t, 1000);
		timerIN.start();
	}

	/**
	 * 是否正在计时
	 */
	public boolean isCount() {
		return isCount;
	}

	class CountTimerIN extends CountDownTimer {

		private static final int SECONDS = 60; // 秒数
		private static final int MINUTES = 60 * 60; // 小时

		private long first = 0, twice = 0, third = 0;
		private long mtmp = 0, mtmp2 = 0;

		public CountTimerIN(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			countView.setText(compeletText); // "获取验证码"
			countView.setEnabled(true);
			isCount = false;
		}

		@Override
		public void onTick(long millisUntilFinished) {
			countView.setEnabled(false);
			String time = "";
			// 获取当前时间总秒数
			first = millisUntilFinished / 1000;
			if (first <= SECONDS) { // 小于一分钟 只显示秒
				if (type == Displey_Second_Minute) {
					time = String.format("00:%02d", first);

				} else {
					time = String.format("%02d", first);
				}
			} else if (first < MINUTES) { // 大于或等于一分钟，但小于一小时，显示分钟
				twice = first % 60; // 将秒转为分钟取余，余数为秒
				mtmp = first / 60; // 将秒数转为分钟
				time = String.format("%02d:%02d", mtmp, twice);// 显示分钟和秒
			} else {
				twice = first % 3600; // twice为余数 如果为0则小时为整数
				mtmp = first / 3600;
				if (twice == 0) {
					// 只剩下小时
					time = String.format("%02d:00:00", mtmp);
				} else {
					if (twice < SECONDS) { // twice小于60 为秒
						time = String.format("%02d:00:%02d", mtmp, twice);// 显示小时和秒
					} else {
						third = twice % 60; // third为0则剩下分钟 否则还有秒
						mtmp2 = twice / 60;
						if (third == 0) {
							// 显示小时,分
							time = String.format("%02d:%02d:00", mtmp, mtmp2);
						} else {
							// 显示小时,分和秒
							time = String.format("%02d:%02d:%02d", mtmp, mtmp2,
									third);
						}
					}
				}
			}
			time = String.format(countText, time);
			countView.setText(time);
		}
	}

	public void cancel() {
		if (timerIN != null) {
			timerIN.cancel();
		}

	}
}
