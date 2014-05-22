package com.qiniu.auth.token;

import com.qiniu.auth.token.AuthException;

import com.qiniu.conf.Conf;

public class DigestAuth {

	public static String sign(Mac mac, byte[] data) throws AuthException {
		if (mac == null) {
			mac = new Mac(Conf.ACCESS_KEY, Conf.SECRET_KEY);
		}
		return mac.sign(data);
	}
	
	
	public static String signWithData(Mac mac, byte[] data) throws AuthException {
		if (mac == null) {
			mac = new Mac(Conf.ACCESS_KEY, Conf.SECRET_KEY);
		}
		return mac.signWithData(data);
	}
	
}
