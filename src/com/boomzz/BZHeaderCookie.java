package com.boomzz;

import java.util.HashMap;
import java.util.Map.Entry;

public class BZHeaderCookie {

	private HashMap<String, Object> cookieStore = new HashMap<String, Object>();
	
	public void put(String key,Object value) {
		cookieStore.put(key, value);
	}
	
	public void remove(String key) {
		cookieStore.remove(key);
	}
	public Object get(String key) {
		
		return cookieStore.get(key);
	}
	
	public String getCookies() {
		StringBuilder builder = new StringBuilder();  
		for(Entry<String, Object> entry : cookieStore.entrySet()) {
			String key=entry.getKey();
			Object value=entry.getValue();
			builder.append(key);
			builder.append("=");
			builder.append(value);
			builder.append(";");
		}
		return builder.toString();
	}
}
