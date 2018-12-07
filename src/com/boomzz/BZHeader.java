package com.boomzz;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BZHeader {
	
	private BZHeaderCookie requestCookie = new BZHeaderCookie();
	private BZHeaderCookie responseCookie = new BZHeaderCookie();
	
	private HashMap<String, String> request = new HashMap();
	private HashMap<String, String> response = new HashMap();
	
	public BZHeader() {
		request.put("Accept", "application/json, text/javascript, */*; q=0.01");
		request.put("Accept-Encoding", "application/json, text/javascript, */*; q=0.01");
		request.put("Accept-Language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7");
		request.put("Connection", "keep-alive");
		request.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36");
		request.put("Cookie", requestCookie.getCookies());
		request.put("Content-Type", "application/x-www-form-urlencoded");
	}
	
	protected void setDefaultRequestProperty(HttpURLConnection conn) {
		for(Map.Entry<String, String> entry:request.entrySet()) {
			conn.addRequestProperty(entry.getKey(),entry.getValue());
		}
	}
	
	public void addRequestHearderProerty(String key,String value) {
		request.put(key, value);
	}

	public BZHeaderCookie getRequestCookie() {
		return requestCookie;
	}

	public void setRequestCookie(BZHeaderCookie requestCookie) {
		this.requestCookie = requestCookie;
	}

	public BZHeaderCookie getResponseCookie() {
		return responseCookie;
	}

	public HashMap<String, String> getRequestHeader() {
		return request;
	}

	public HashMap<String, String> getResponseHeader() {
		return response;
	}

	protected BZHeader convert(Map<String, List<String>> headerFields) {
		for(String h: headerFields.keySet()){
        	if("Set-Cookie".equals(h)){
        		List<String> list = headerFields.get(h);
        		for(String s:list){
        			String cookiesArry[]=s.split(";");
            		for(String strCookie:cookiesArry){
            			String c[]=strCookie.split("=");
            			if(c.length==2){
            				//除去不需要的
            				if(!c[0].contains("EXPIRES")&&!c[0].contains("PATH")&&!c[0].contains("DOMAIN"))
            					responseCookie.put(c[0], c[1]);
            			}
            		}
        		}
        	}else {
        		response.put(h, headerFields.get(h).get(0));
        	}
        }
		response.put("Set-Cookie", responseCookie.getCookies());
		
		return this;
	}
}
