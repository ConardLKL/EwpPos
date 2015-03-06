package com.limahao.ticket.dao;

import java.util.HashMap;

import android.app.Activity;

import com.limahao.ticket.config.Constants;
import com.limahao.ticket.config.Urls;
import com.limahao.ticket.entity.ResponseBase;
import com.limahao.ticket.entity.ResponseEndStation;
import com.limahao.ticket.entity.ResponseLogin;
import com.limahao.ticket.entity.ResponsePreDate;
import com.limahao.ticket.entity.ResponseTicker;
import com.limahao.ticket.entity.ResponseTickerOrder;
import com.limahao.ticket.entity.ResponseTickerOrderInfo;
import com.limahao.ticket.https.NetWorkHelper;

/**
 * @Title: TicketDao.java
 * 
 * @author email: <a href="cj@ewppay.com">陈杰</a>
 * @date 2014-7-28 上午11:04:47
 * @version :
 * @Description:
 */
public class TicketDao extends BaseDao {

	public TicketDao(Activity activity) {
		super(activity);
	}

	/**
	 * 3.4.	获取预售天数和单订单售票张数
	 * @param params
	 * @return
	 */
	public ResponseBase getPreDate() {
		// 签名顺序
		String[] order = { "intercodode", "terminalid", "agentid", "datatype","key" };
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("intercodode", "130003"); // 业务code
		return get(Urls.SERVER, ResponsePreDate.class, false,
				Constants.DBContentType.Content_list, params, order);
	}
	
	
	/**
	 * 3.2.	获取到站列表
	 * @param params
	 * @return
	 */
	public ResponseBase getEndStation() {
		// 签名顺序
		String[] order = { "intercodode", "terminalid", "agentid", "datatype","key" };
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("intercodode", "130001"); // 业务code
		return get(Urls.SERVER, ResponseEndStation.class, true,
				Constants.DBContentType.Content_initdata, params, order); // 缓存15天，站点列表不太会变
	}
	/**
	 * 获取车次列表
	 * @param params 传入参数
	 * @return
	 */
	public ResponseBase getTitckerinfo(HashMap<String, String> params){
		String[] order = { "intercodode", "terminalid", "agentid", "endstationid","leavedate","votetype","datatype","key" };
		params.put("intercodode", "130002"); // 业务code
		params.put("votetype", "1"); // 0为所有车次，1为有余票车次
		return get(Urls.SERVER, ResponseTicker.class,true,Constants.DBContentType.Content_search, params, order);
	}
	
	/**
	 * 3.5. 下订单
	 * 
	 * @param params
	 * @return
	 */
	public ResponseBase orderTicket(HashMap<String, String> params) {
		// 签名顺序
		String[] order = { "intercodode", "terminalid", "agentid", "ordernum",
				"busNo", "inSure", "ticketsPrice", "LeaveDate", "endStationId",
				"regphone", "sitType", "datatype", "key" };
		
		params.put("intercodode", "130102"); // 业务code
		params.put("system_type", "1"); // 系统id
		params.put("sitType", "0"); // 座位类型
		params.put("inSure", "0"); // 保险
		params.put("ip", NetWorkHelper.getIPAddress(mActivity)); // IP地址
		return get(Urls.SERVER, ResponseTickerOrderInfo.class, false,
				Constants.DBContentType.Content_list, params, order); 
	}
	
	/**
	 * 3.19.	获取银行卡支付数据
	 * 
	 * @param params
	 * @return
	 */
	public ResponseBase getUmsPayInfo(HashMap<String, String> params) {
		// 签名顺序
		String[] order = { "intercodode", "terminalid", "agentid", "datatype","channel", "orderno", "key" };
		
		params.put("intercodode", "130103"); // 业务code
		params.put("channel", "UMSPAY"); // 渠道号
		return get(Urls.SERVER, ResponseBase.class, false,
				Constants.DBContentType.Content_list, params, order); 
	}
	
	/*
	 * http://192.168.2.250:8291/TermServ/ChargeServlet?
	 * intercodode=130106&terminalid=5710050001&agentid=57100500&
	 * regphone=13634179935&password=123456&datatype=json&sign=ac8c81a2540056caa147870c147c34b4
	 *  3.12 用户登录
	 * @param params 传入参数
	 */
	public ResponseBase getLogininfo(HashMap<String, String> params){
		String[] order = {"intercodode", "terminalid", "agentid", "regphone","password","datatype","key"};
		params.put("intercodode", "130106"); // 业务code
		return get(Urls.SERVER, ResponseLogin.class, false,Constants.DBContentType.Content_list, params, order);
	}
	/*
	 * 3.10  用户注册 
	 * @param params 传入参数
	 */
	public ResponseBase getRegister(HashMap<String, String> params){
		String[] order = {"intercodode", "terminalid", "agentid","regphone","password","datatype","key"};
		params.put("intercodode", "130104"); // 业务code
		return get(Urls.SERVER, ResponseBase.class, false,Constants.DBContentType.Content_list, params, order);
	}
	/*
	 * 3.11 获得手机验证码
	 * @param params 传入参数 
	 * 18867503642
	 */
	public ResponseBase getSmsvode(HashMap<String, String> params){
		String[] order = {"intercodode", "terminalid", "agentid","datatype","regphone","key"};
		params.put("intercodode", "130107"); // 业务code
		return get(Urls.SERVER, ResponseBase.class, false,Constants.DBContentType.Content_list, params, order);
	}
	/*
	 * 3.14	修改用户手机号码
	 */
	public ResponseBase getBindNewPhone(HashMap<String, String> params){
		String[] order = {"intercodode", "terminalid", "agentid","regphone","datatype","type","key"};
		params.put("intercodode", "130108"); // 业务code
		params.put("type", "phone");
		return get(Urls.SERVER, ResponseBase.class, false,Constants.DBContentType.Content_list, params, order);
	}
	/*
	 * 3.15	修改用户密码
	 */
	public ResponseBase getAlterPwd(HashMap<String, String> params){
		String[] order = {"intercodode", "terminalid", "agentid","regphone","datatype","type","key"};
		params.put("intercodode", "130108"); // 业务code
		params.put("type", "password");
		return get(Urls.SERVER, ResponseBase.class, false,Constants.DBContentType.Content_list, params, order);
	}
	/*
	 * 3.16 个人资料更新
	 */
	
	public ResponseBase getPerson(HashMap<String, String> params){
		String[] order = {"intercodode", "terminalid", "agentid","regphone","datatype","type","key"};
		params.put("intercodode", "130108"); // 业务code
		params.put("type", "normal");
		return get(Urls.SERVER, ResponseLogin.class, false,Constants.DBContentType.Content_list, params, order);
	}
	
	/*
	 * 3.17  忘记密码
	 */
	public ResponseBase getForgotPwd(HashMap<String, String> params){
		String[] order = {"intercodode", "terminalid", "agentid","regphone","datatype","type","key"};
		params.put("intercodode", "130108"); // 业务code
		params.put("type", "forgot");
		return get(Urls.SERVER, ResponseBase.class, false,Constants.DBContentType.Content_list, params, order);
	}
	
	/*用户车票订单查询
	 * http://192.168.2.250:8291/TermServ/ChargeServlet?
	 * intercodode=130105&terminalid=5710050001&agentid=57100500&
	 * currentpage=1&pagesize=2&regphone=13989826414&password=000000
	 * &datatype=json&sign=09c6856930d22c61c24522c098c95f98
	 */
	public ResponseBase getTitckerOrder(HashMap<String, String> params){
		String[] order = {"intercodode", "terminalid", "agentid","currentpage","pagesize","datatype","key"};
		params.put("intercodode", "130105"); // 业务code
		return get(Urls.SERVER, ResponseTickerOrder.class, false,Constants.DBContentType.Content_list, params, order);
	}
	
	/*
	 * http://192.168.2.250:8291/TermServ/ChargeServlet?
	 * intercodode=130109&terminalid=5710050001&agentid=57100500&
	 * regphone=18867503642&password=111111&orderno=20140812660000002483&
	 * datatype=json&sign=34d4c0265a98fb4a6f7b3452b1406a96
	 */
	
	public ResponseBase getTitckerOrderDetial(HashMap<String, String> params){
		String[] order = {"intercodode", "terminalid", "agentid","regphone","orderno","datatype","key"};
		params.put("intercodode", "130109"); // 业务code
		return get(Urls.SERVER, ResponseTickerOrderInfo.class, false,Constants.DBContentType.Content_list, params, order);
	}
}
