package com.qiniu.auth.token;


import org.apache.commons.codec.EncoderException;





public class URLUtils {

	// to do url.escape();
	public static String makeBaseUrl(String domain, String key) throws EncoderException {
		return "http://" + domain + "/" + URLEscape.escape(key);
	}
}
