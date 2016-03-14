package com.alphabets.data;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.alphabets.widgets.Message;
import com.alphabets.widgets.WordBlock;

import android.content.Context;
import android.util.Log;

public class Loader {
	
	private static boolean ready;
	
	private static WordBlock[] wordBlocks;
	private static Message[] messages;
	private static Map<Integer, String[]> positionMap;
		
	public static void startLoading(final Context context) {
		
		
		Thread th = new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				long time = System.currentTimeMillis();
				
				// retrieve game data
				DataReader gameData = null;
				try {
					gameData = new DataReader(context);
				} catch (IOException e) {
					Log.e("STUFF", "ERROR");
					e.printStackTrace();
				}
						
				String[] orgWords = gameData.getOrgWords();
				String[] newWords = gameData.getNewWords();
				String[] translation = gameData.getTranslations();
				String[] msg = gameData.getMessages();
				positionMap = gameData.getPositionMap();
				
				wordBlocks = new WordBlock[orgWords.length];
				messages = new Message[msg.length];
				
				Log.e("STUFF", "1");
						
				/*
				 * 
				 * TODO instead of locked = true, get progress data
				 * and plug it in.
				 * 
				 */
						
				for(int i=0; i<orgWords.length; i++)
					wordBlocks[i] = new WordBlock(context, orgWords[i], newWords[i], translation[i], true);

				Log.e("STUFF", "" + 2);
				
				for(int i=0; i<msg.length; i++)
					messages[i] = new Message(context, msg[i]);
				
				Log.e("STUFF", "" + 3);
				Log.e("STUFF", "end load at " + (System.currentTimeMillis()-time));
				Loader.handleDataReady();
				
			}
			
		});
		th.start();
		
	}
	
	public static WordBlock[] getWordBlocks() {
		return wordBlocks;
	}
	
	public static Message[] getMessages() {
		return messages;
	}
	
	public static Map<Integer, String[]> getPositionMap() {
		return positionMap;
	}
	
	public static boolean isReady() {
		return ready;
	}
	
	/*
	 * 
	 * Used to communicate between the data loader and load activity
	 * 
	 */
	
	private static boolean showingStartActivity;
	
	public static void setShowingStartActivity(boolean mode) {
		showingStartActivity = mode;
	}
	
	private static ReadyListener listener;
	
	public static void setListener(ReadyListener listener) {	
		Loader.listener = listener;		
	}
	
	public static void handleDataReady() {
		
		Log.e("STUFF", "handling " + listener);
		
		// load activity is being shown and can proceed as normal
		if(listener!=null) 
			listener.dataIsReady();
		// still in start activity -> will jump straight into main without loadAct
		else if(showingStartActivity)
			ready = true;
		// not showing start activity anymore,
		// but listener still not plugged in.
		// wait and re-try.
		else {
			
			try {
				TimeUnit.MILLISECONDS.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			handleDataReady();
			
		}
		
	}

}
