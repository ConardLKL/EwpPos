package com.limahao.ticket.view;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.format.DateFormat;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.limahao.ticket.R;
import com.limahao.ticket.entity.ItemPreDate;
import com.limahao.ticket.view.NumberPicker.OnSelectedValueClickListener;
import com.limahao.ticket.view.NumberPicker.OnValueChangeListener;

public class NumberPickerPopupWindow extends PopupWindow implements
		OnValueChangeListener {
	private Context context;
	private final NumberPicker mNumberPicker;
	private TextView mNumberDisplay;
//	private Calendar mDate = Calendar.getInstance();
	private ArrayList<String> mDateDisStrings = new ArrayList<String>();
	private SpannableStringBuilder spannableStringBuilder;
	private int tagnewVal = 0;
	private int predate;
	private String curdate;

	public interface OnNumberSetListener {

		void onNumberSet(NumberPicker picker, int oldVal, int newVal);
	}

	public NumberPickerPopupWindow(Context context, TextView mNumberDisplay,ItemPreDate item) {
		super(context);
		this.context = context;
		this.predate = Integer.parseInt(item.getPreDate());
		this.curdate = item.getCurDate();
		this.mNumberDisplay = mNumberDisplay;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.number_picker_dialog, null);
		mNumberPicker = (NumberPicker) view.findViewById(R.id.number);
		mNumberPicker.setOnValueChangedListener(this);
		
		// 点击选中的数据，一般认为选择完毕，做退出处理
		mNumberPicker.setOnSelectedValueClickListener(new OnSelectedValueClickListener() {
			public void OnSelectedValueClick() {
				dismiss();
			}
		});
		setContentView(view);
		// pupopWindow
		setpopupWindow(view);
		// mNumberPicker
		init(); // 初期化日期
		updateDateControl();

		
	}
	
	/**
	 * 初期化
	 */
	private void init(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		try {
			Date date = sdf.parse(curdate);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			mDateDisStrings.add((String) DateFormat.format("yyyy-MM-dd 今天",calendar));
			for (int i = 0; i < predate -1; ++i) {
				calendar.add(Calendar.DAY_OF_YEAR, 1);
				switch (i) {
				case 0:
					mDateDisStrings.add((String) DateFormat.format("yyyy-MM-dd 明天", calendar));
					break;
				case 1:
					mDateDisStrings.add((String) DateFormat.format("yyyy-MM-dd 后天", calendar));
					break;
				default:
					mDateDisStrings.add((String) DateFormat.format("yyyy-MM-dd EEEE", calendar));
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		mNumberPicker.setMinValue(0);
		mNumberPicker.setMaxValue(predate -1);
		mNumberPicker.setDisplayedValues(mDateDisStrings);
		mNumberPicker.invalidate();
		mNumberPicker.setWrapSelectorWheel(false); //  取消循环滚动 注：这个方法要在数据添加后调用，否则空数据不会设置
	}

	/**
	 * 上下滚动时改变选中的日期值
	 */
	private void updateDateControl() {
		switch (tagnewVal) {
		case 0:
			spannableStringBuilder = new SpannableStringBuilder(mDateDisStrings.get(0));
			spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.RED),11, 13, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
			mNumberDisplay.setText(spannableStringBuilder);
			break;
		case 1:
			spannableStringBuilder = new SpannableStringBuilder(mDateDisStrings.get(1));
			spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.RED),11, 13, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
			mNumberDisplay.setText(spannableStringBuilder);
			break;
		case 2:
			spannableStringBuilder = new SpannableStringBuilder(mDateDisStrings.get(2));
			spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.RED),11, 13, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
			mNumberDisplay.setText(spannableStringBuilder);
			break;
		default:
			// int flag = DateUtils.FORMAT_SHOW_YEAR |
			// DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_WEEKDAY;
			// mNumberDisplay.setText(DateUtils.formatDateTime(context,mDate.getTimeInMillis(),
			// flag));
//			mNumberDisplay.setText((String) DateFormat.format("yyyy-MM-dd EEEE", mDate.getTimeInMillis()));
			// 没必要使用mDate 当本地时间和传入时间不相等的时候 日期会混乱
			mNumberDisplay.setText(mDateDisStrings.get(tagnewVal));
			break;
		}
	}

	@SuppressWarnings("deprecation")
	private void setpopupWindow(final View view) {
		// TODO Auto-generated method stub
		this.setWidth(LayoutParams.FILL_PARENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		this.setFocusable(true);
		// ColorDrawable dw = new ColorDrawable(000000);
		// this.setBackgroundDrawable(dw);
		Drawable pupbg = context.getResources().getDrawable(
				R.drawable.numberpicker_bg);
		this.setBackgroundDrawable(pupbg);
		
		// 假想 能进入触摸
		view.setOnTouchListener(new OnTouchListener() {
			@SuppressLint("ClickableViewAccessibility")
			public boolean onTouch(View v, MotionEvent event) {
				int height = view.findViewById(R.id.numberPicker).getTop();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < height) {
						dismiss();
					}
				}
				return true;
			}
		});
	}

	@Override
	public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
//		// 使用 DAY_OF_YEAR 当本地时间和传入时间不相等的时候 日期会混乱
//		mDate.add(Calendar.DAY_OF_YEAR, newVal - oldVal);
		tagnewVal = newVal;
		updateDateControl();
	}

}
