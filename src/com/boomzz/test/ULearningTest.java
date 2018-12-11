package com.boomzz.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.boomzz.BZHeader;
import com.boomzz.BZHttpRequest;
import com.boomzz.BZHttpResponse;

public class ULearningTest {

	private BlockingQueue<JSONObject> wholepageQueue = null;
	String AUTHORIZATION = "";
	private void start() {
		try {
			InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(new File("D://courseDirectory.json")), "UTF-8");
			BufferedReader reader = new BufferedReader(inputStreamReader); 
			StringBuffer stringBuffer = new StringBuffer();
			String tempString = null;  
			while ((tempString = reader.readLine()) != null) {
				stringBuffer.append(tempString);
			}  
			reader.close();
			JSONObject jsonObject = new JSONObject(stringBuffer.toString());
			JSONArray chapters = jsonObject.getJSONArray("chapters");
			for(int i=0;i<chapters.length();i++) {
				JSONObject node = chapters.getJSONObject(i);
				int nodeId = node.getInt("nodeid");
				wholepageQueue = new ArrayBlockingQueue<>(chapters.length());
				new Thread(()->{
					getPageInfo(nodeId);
				}).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getPageInfo(int nodeId) {
		try {
			BZHeader header = new BZHeader();
			header.getRequestHeader().put("UA-AUTHORIZATION",AUTHORIZATION);
			BZHttpRequest bzHttpRequest = new BZHttpRequest(header);
			BZHttpResponse bzHttpResponse = bzHttpRequest.get("https://api.ulearning.cn/wholepage/chapter/stu/"+nodeId);
			String string = bzHttpResponse.getString();
			JSONObject wholepage = new JSONObject(string);
			wholepageQueue.add(wholepage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void takeStart() {
		try {
			while (true) {
				JSONObject take = wholepageQueue.take();
				System.out.println("获取到一个处理项,Time:"+System.currentTimeMillis());
				JSONArray wholepageItemDTOList = take.getJSONArray("wholepageItemDTOList");
				for(int i=0;i<wholepageItemDTOList.length();i++) {
					JSONObject jsonObject = wholepageItemDTOList.getJSONObject(i);
					int itemid = jsonObject.getInt("itemid");
					JSONObject item = new JSONObject();
					item.put("itemid", itemid);
					item.put("autoSave", 0);
					item.accumulate("version", null);
					item.put("complete", 1);
					item.put("userName", "王");
					item.put("score", 100);
					JSONArray pageStudyRecordDTOList = studyRecordDTOList(jsonObject.getJSONArray("wholepageDTOList"));
					item.put("pageStudyRecordDTOList", pageStudyRecordDTOList);
					submit(item.toString());
//					break;
				}
//				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void submit(String string) throws Exception {
//		System.out.println(string);
		String encrypt = new DESUtil().encrypt(string);
//		System.out.println(encrypt);
		new Thread(new Runnable() {
			@Override
			public void run() {
				BZHeader header = new BZHeader();
				header.getRequestHeader().put("UA-AUTHORIZATION",AUTHORIZATION);
				BZHttpRequest bzHttpRequest = new BZHttpRequest(header);
				HashMap<String, String> hashMap = new HashMap<>();
				hashMap.put(null, encrypt);
				header.addRequestHearderProerty("Content-Type", "text/plain");
				BZHttpResponse bzHttpResponse = bzHttpRequest.post("https://api.ulearning.cn/yws/api/personal/sync?courseType=4&platform=PC",hashMap);
				System.out.println(bzHttpResponse.getString());
			}
		}).start();
	}

	private JSONArray studyRecordDTOList(JSONArray jsonArray) throws JSONException {
		JSONArray pageStudyRecordDTOList = new JSONArray();
		for(int i=0;i<jsonArray.length();i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			JSONObject studyRecordItem = new JSONObject();
			studyRecordItem.put("pageid", jsonObject.getInt("relationid"));
			studyRecordItem.put("complete", 1);
			Random random = new Random();
			int t=random.nextInt(50)+1;
			studyRecordItem.put("studyTime", t);
			studyRecordItem.put("score",100);
			studyRecordItem.put("answerTime",1);
			studyRecordItem.put("questions", new JSONArray());
			studyRecordItem.put("videos", new JSONArray());
			studyRecordItem.put("speaks", new JSONArray());
			pageStudyRecordDTOList.put(studyRecordItem);
			JSONArray coursepageDTOList = jsonObject.getJSONArray("coursepageDTOList");
			int contentType = jsonObject.getInt("contentType");
			if(contentType==6||contentType==7) {
				for(int j=0;j<coursepageDTOList.length();j++) {
					JSONObject course = coursepageDTOList.getJSONObject(j);
					int type = course.getInt("type");
					if(type==4) {
						int resourceid = course.getInt("resourceid");
						JSONObject video = new JSONObject();
						video.put("videoid", resourceid);
						video.put("current", 120);
						video.put("status", 1);
						video.put("recordTime", 100);
						video.put("time", 1);
						studyRecordItem.put("videos",new JSONArray().put(video));
					}
					if(type==6) {
						JSONArray questionDTOList = course.getJSONArray("questionDTOList");
						JSONArray questions = new JSONArray();
						for(int n=0;n<questionDTOList.length();n++) {
							JSONObject qDto = questionDTOList.getJSONObject(n);
							int questionid = qDto.getInt("questionid");
							JSONObject qJsonObject = new JSONObject();
							qJsonObject.put("questionid", questionid);
							String anString[] = {"好好学习"};
							qJsonObject.put("answerList",anString);
							qJsonObject.put("score", 4);
							questions.put(qJsonObject);
						}
						studyRecordItem.put("questions",questions);
					}
				}
			}
		}
		return pageStudyRecordDTOList;
	}

	public static void main(String[] args) {
		ULearningTest uLearningTest = new ULearningTest();
		uLearningTest.start();
		uLearningTest.takeStart();
	}
}
