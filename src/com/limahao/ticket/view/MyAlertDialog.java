package com.limahao.ticket.view;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.limahao.ticket.R;
import com.limahao.ticket.utils.CommonUtil;

public class MyAlertDialog extends Dialog {
	private TextView title, message, sure, no;
	private Activity context;
	private LinearLayout sure_layout;
	private View line;

	public static final int Mode_Yes = 0;
	public static final int Mode_Yes_No = 1;

	private int mode = Mode_Yes;

	public MyAlertDialog(Activity context) {
		super(context, R.style.alert_dialog);
		this.mode = Mode_Yes;
		this.context = context;
		initview();
	}

	public MyAlertDialog(Activity context, int mode) {
		super(context, R.style.alert_dialog);
		this.mode = mode;
		this.context = context;
		initview();
	}

	private void initview() {
		View view = View.inflate(context, R.layout.alert_dialog_title, null);
		title = (TextView) view.findViewById(R.id.alert_title_tv);
		message = (TextView) view.findViewById(R.id.alert_message_tv);
		sure_layout = (LinearLayout) view.findViewById(R.id.alert_sure_layout);
		sure = (TextView) view.findViewById(R.id.alert_sure_tv);
		line = (View) view.findViewById(R.id.alert_line);
		sure.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				MyAlertDialog.this.dismiss();
			}
		});
		no = (TextView) view.findViewById(R.id.alert_no_tv);
		no.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				MyAlertDialog.this.dismiss();
			}
		});

		if (mode == Mode_Yes) {
			no.setVisibility(View.GONE);
			line.setVisibility(View.GONE);
		}
		
		setContentView(view);
		
		// 调整对话框大小， 注：在setContentView(view)后设置否则布局又被设置成默认
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
		lp.width = (int) (CommonUtil.getScreenWidth(context) * 0.8);
		lp.height = LayoutParams.WRAP_CONTENT;
		getWindow().setAttributes(lp);
	}

	public LinearLayout getsure_layout() {
		return sure_layout;
	}

	public void setsure_layout(LinearLayout sure_layout) {
		this.sure_layout = sure_layout;
	}

	public TextView getTitle() {
		return title;
	}

	public void setTitle(TextView title) {
		this.title = title;
	}

	public TextView getMessage() {
		return message;
	}

	public void setMessage(TextView message) {
		this.message = message;
	}

//	public void setMessage(String message, float fontsize) {
//		this.message.setText(message);
//		this.message.setTextSize(fontsize);
//	}

	public void setMessage(String message) {
		this.message.setText(message);
	}

	public TextView getSure() {
		return sure;
	}

	public void setSure(TextView sure) {
		this.sure = sure;
	}

	public TextView getNo() {
		return no;
	}

	public void setNo(TextView no) {
		this.no = no;
	}

	public void setOnYesclick(View.OnClickListener yesListiener) {
		this.sure.setOnClickListener(yesListiener);
	}

	public void setOnNoClick(View.OnClickListener noListiener) {
		this.no.setOnClickListener(noListiener);
	}

}
