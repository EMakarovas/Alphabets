package com.alphabets.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.JsonReader;

public class DataReader {
	
	private String[] messages;
	private String[] orgWords;
	private String[] newWords;
	private String[] translations;
	private Map<Integer, String[]> positions;
	
	@SuppressLint("UseSparseArrays")
	public DataReader(Context context) throws IOException {
		
		String fileName = "data.json";
		InputStream fileData = context.getAssets().open(fileName);
		JsonReader reader = new JsonReader(new InputStreamReader(fileData, "UTF-8"));
		
		positions = new HashMap<Integer, String[]>();
		Map<String, String> map = new HashMap<String, String>();
		
		int wordCounter = 0;
		int messageCounter = 0;
		int absolutePositionCounter = 0;
		reader.beginObject();
		
		while(reader.hasNext()) {
			
			String name = reader.nextName();
				
			if(name.equals("words")) {
					
				reader.beginArray();
				while(reader.hasNext()) {
						
					reader.beginObject();
					reader.nextName();
					String orgWord = reader.nextString();
					reader.nextName();
					String newWord = reader.nextString();
					reader.nextName();
					String translation = reader.nextString();
					reader.endObject();
					String orgKey = "org" + wordCounter;
					String newKey = "new" + wordCounter;
					String tranKey = "tran" + wordCounter;
					map.put(orgKey, orgWord);
					map.put(newKey, newWord);
					map.put(tranKey, translation);
					String[] posValue = { "w", Integer.toString(wordCounter) };
					positions.put(absolutePositionCounter, posValue);
					wordCounter++;
					absolutePositionCounter++;
						
				}
				reader.endArray();
					
			} else if(name.equals("message")) {
					
				String message = reader.nextString();
				String key = "message" + messageCounter;;
				map.put(key, message);
				String[] posValue = { "m", Integer.toString(messageCounter) };
				positions.put(absolutePositionCounter, posValue);
				absolutePositionCounter++;
				messageCounter++;
					
			}
						
		}
				
		reader.close();
		
		orgWords = new String[wordCounter];
		newWords = new String[wordCounter];
		translations = new String[wordCounter];
		
		for(int i=0; i<wordCounter; i++) {
			
			orgWords[i] = map.get("org" + i);
			newWords[i] = map.get("new" + i);
			translations[i] = map.get("tran" + i);
			
		}
		
		messages = new String[messageCounter];
		
		for(int i=0; i<messageCounter; i++)
			messages[i] = map.get("message" + i);
		
	}
	
	public String[] getMessages() {
		return this.messages;
	}
	
	public String[] getOrgWords() {
		return this.orgWords;
	}
	
	public String[] getNewWords() {
		return this.newWords;
	}
	
	public String[] getTranslations() {
		return this.translations;
	}
	
	public Map<Integer, String[]> getPositionMap() {
		return positions;
	}

}
