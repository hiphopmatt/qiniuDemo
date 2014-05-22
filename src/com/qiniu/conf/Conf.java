package com.qiniu.conf;

import java.util.Date;

import org.json.JSONException;

import com.qiniu.auth.token.AuthException;
import com.qiniu.auth.token.Mac;
import com.qiniu.auth.token.PutPolicy;

import android.text.format.DateFormat;
import android.util.Log;




public class Conf {
	
	public static String ACCESS_KEY = "iXlbDFIYsBoavkReOn9lnKmNsvKk6U-MQty0ZJWS";
	public static String SECRET_KEY = "t_WBqY1JujmU9z-ICdMXfZ72AksXle5rg3IXBigA";
	// 指定上传所用的七牛空间名称
	public static String BUCKET_NAME = "testappdemo";

	public static final String USER_AGENT = "qiniu android-sdk v6.0.0";
	public static final String UP_HOST = "http://up.qiniu.com";

	// get qiniu upload token
	public static String getToken()  {
		String uptoken;
		Mac mac = new Mac(Conf.ACCESS_KEY, Conf.SECRET_KEY);
		PutPolicy putPolicy = new PutPolicy(Conf.BUCKET_NAME);
		try {
			uptoken = putPolicy.token(mac);
			Log.e("qiniu uptoken:", uptoken);
			return uptoken;
		} catch (AuthException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getUploadFileName() {
		DateFormat dateformat = new DateFormat();
		String today = dateformat.format("yyyy-MM-dd hh:mm:ss", new Date()).toString();
		return today;
	}
	
}
