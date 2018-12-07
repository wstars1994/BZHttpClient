package com.boomzz;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

public class BZHttpRequest {

	private BZHeader header = null;
	
	public BZHttpRequest() {
		this.header = new BZHeader();
	}
	
	public BZHttpRequest(BZHeader header) {
		this.header = header;
	}
	
	public BZHttpResponse get(String url) {
		try {
			HttpURLConnection connection = getConnection(url,"GET");
			connection.connect();
        	return new BZHttpResponse(connection);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public BZHttpResponse post(String url,HashMap<String, String> params) {
		try {
			HttpURLConnection connection = getConnection(url,"POST");
			connection.connect();
			PrintWriter printWriter = new PrintWriter(connection.getOutputStream());
			// 发送请求参数
			printWriter.write(urlParamsStr(params));//post的参数 xx=xx&yy=yy
			// flush输出流的缓冲
			printWriter.flush();
        	return new BZHttpResponse(connection);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static String urlParamsStr(HashMap<String, String> params) throws UnsupportedEncodingException{
		String url = "";
		for(String key:params.keySet()){
			url+=key+"="+URLEncoder.encode(params.get(key), "utf-8")+"&";
		}
		if(!url.equals(""))
			url = url.substring(0,url.length()-1);
		return url;
	}
	
	private HttpURLConnection getConnection(String urlStr,String method) throws Exception {
		if(header==null) throw new NullPointerException("Header must not null !");
		URL url = new URL(urlStr);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setRequestMethod(method);
		header.setDefaultRequestProperty(connection);
		return connection;
	}
	
	public static void main(String[] args) {
		BZHeader header = new BZHeader();
		BZHttpRequest request = new BZHttpRequest(header);
		HashMap<String, String> params = new HashMap<>();
		params.put("loginname", "sz1803100118");
		params.put("password", "wxy123456");
		BZHttpResponse bzHttpResponse = request.post("https://www.ulearning.cn/ulearning_web/login!checkUserForLogin.do",params);
		BZHeader responseHeader = bzHttpResponse.getResponseHeader();
		
		System.out.println("1----"+bzHttpResponse.getString());
		params.clear();
		bzHttpResponse = request.post("https://www.ulearning.cn/umooc/user/login.do", params);
		responseHeader = bzHttpResponse.getResponseHeader();
		
		System.out.println("2----"+bzHttpResponse.getString());
		System.out.println(responseHeader.getResponseHeader());
	}
}
