package com.limahao.ticket.config;

public class Constants {

	public static final class TAGS {
		public static final String MORE_ABOUT = "关于我们";
		public static final String MORE_NOTICE = "用户指南";
		public static final String MORE_PROTOCOL = "免责声明";
	}

	public static final class DBContentType {
		public static final String Content_list = "list";
		public static final String Content_content = "content";
		public static final String Content_initdata = "initdata";
		public static final String Discuss="discuss";
		public static final String Content_search = "search"; // 查询缓存 10秒  因为服务器会抗不住弱爆了
	}

	public static final class WebSourceType{
		public static final String Json="json";
		public static final String Xml="xml";
	}
	
	public static final class ActionCode {
		public static final int ACT_CITY_SELECT = 1000;    // 目的城市选择
		public static final int ACT_UMS_PAY = 1001;        // 银联支付
		public static final int ACT_ALTER_PWD = 1002;      // 密码修改
		public static final int ACT_BIND_PHONE = 1003;     // 手机绑定
		public static final int ACT_WEBSIT = 1004;         // 周边网点
		public static final int ACT_REGISTER = 1005;       // 注册
		public static final int ACT_ORDER = 1006;          // 下订单
		public static final int ACT_ORDER_PAY = 1007;      // 订单支付
		public static final int ACT_MESSAGE_NOTICE = 1008; // 消息通知
		public static final int ACT_MESSAGE_CANCEL = 1009; // 消息通知取消
		public static final int ACT_LOGIN_SWICH = 1010;    // 登录状态切换
	}
	
	public static final class ParamKey {
		public static final String Key_City_Name = "city";
		public static final String Key_City_ID = "cityID";
		public static final String Key_End_Station= "end_station";
		public static final String Key_Leave_Date = "leave_date";
		public static final String Key_Vote_Type = "votetype";
		public static final String Key_Pre_Date ="predate";
		public static final String Key_Start_Date ="startdate";
		public static final String Key_Ticket_Detail = "ticketdetail";
		public static final String Key_Ticket_Order = "ticketorder";
		public static final String Key_Ticket_Order_No = "ticketorderno";
		public static final String Key_Ticket_Order_Status = "ticketorderstatus";
		public static final String Key_Ticket_Pay_Status = "ticketpaystatus"; // 订单支付状态
		public static final String Key_More_Tag = "tag";
		public static final String Key_Message_Code = "msmvode"; // 验证码
		public static final String Key_Message_Phone = "msmvodephone"; // 验证码对应的手机号
		public static final String Key_Notice = "notice"; // 个人中心的订单消息通知
		public static final String Key_Login_Phone = "loginphone"; // 修改密码，绑定手机后帮助用户减少一次输入手机号的操作
	}
	
	/**
	 * 订单状态 ：等待付款  0   成功订单 1   失败订单3         异常订单 9  
	 * @author Administrator
	 *
	 */
	public static final class OrderStatus{
		public static final String Status_Order_Waiting="0";
		public static final String Status_Order_Success="1";
		public static final String Status_Order_Fail="3";
		public static final String Status_Order_Exception="9";
	}
	
	public static final class PersonSex{
		public static final String Male="male";
		public static final String Female="female";
	}
	
	// 默认http传输时的编码为utf-8
	public static final String DEFAULT_URL_ENCODING = "UTF-8";
	// 成功code
	public static final String CD_SUCCESS= "00000000";
}
