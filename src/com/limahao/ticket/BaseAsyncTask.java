package com.limahao.ticket;



import java.util.HashMap;

import com.limahao.ticket.config.Constants;
import com.limahao.ticket.entity.ResponseBase;
//import com.limahao.ticket.ui.Login;

import android.os.AsyncTask;

/** 
 * @Title: BaseAsyncTask.java 
 * 
 * @author email: <a href="cj@12pai.cn">陈杰</a> 
 * @date 2014-1-17 下午3:52:28 
 * @version :
 * @Description: 
 */
public class BaseAsyncTask extends AsyncTask<Void, Void, ResponseBase> {

	protected BaseActivity mActivity;
	
	public BaseAsyncTask(BaseActivity activity){
		mActivity = activity;
	}
	
	@Override
	protected void onPreExecute() {
	}
	
	@Override
	protected ResponseBase doInBackground(Void... params) {
		return null;
	}
	
	 @Override
     protected void onPostExecute(ResponseBase result) {
		 
		 if(result != null){
			 if(!Constants.CD_SUCCESS.equals(result.getERR_INFO().getERROR_CODE())){
				 mActivity.showLongToast(result.getERR_INFO().getERROR_MSG());
				 return;
			 } 
		 } else {
			 mActivity.showLongToast(mActivity.getResources().getString(R.string.httpError));
			 return;
		 }
	 }

}
