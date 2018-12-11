package com.boomzz;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
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
		try {
			BufferedInputStream bis = new BufferedInputStream(inputStream);
			ByteArrayOutputStream buf = new ByteArrayOutputStream();
			int result = bis.read();
			while(result != -1) {
			    buf.write((byte) result);
			    result = bis.read();
			}
			return buf.toString("utf-8");
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			close();
		}
		return null;
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