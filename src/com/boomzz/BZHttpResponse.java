package com.boomzz;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

public class BZHttpResponse {

	private InputStream inputStream;
	private HttpURLConnection connection;

	protected BZHttpResponse(HttpURLConnection connection) {
		this.connection = connection;
		try {
			this.inputStream = connection.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public InputStream getStream() {
		return this.inputStream;
	}
	
	public String getString() {
		StringBuilder builder = new StringBuilder(); 
		try {
			int len=0;
			byte bytes[] = new byte[1024];
			while ((len=inputStream.read(bytes))!=-1) {
				builder.append(new String(bytes));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			close();
		}
		return builder.toString();
	}
	
	public BZHeader getResponseHeader() {
		return new BZHeader().convert(connection.getHeaderFields());
	}
	
	private void close() {
		try {
			if(inputStream!=null)
				inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}