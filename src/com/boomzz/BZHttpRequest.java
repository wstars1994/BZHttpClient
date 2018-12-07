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
}
