package com.alphabets.progress;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Data {
	
	public static final String BLOCK_COMPLETED = "Block_Completed_";
	
	private static SharedPreferences preferences;
	private static SharedPreferences.Editor editor;
	
	public static void initialiseData(Context context) {
		
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		editor = preferences.edit();
		
	}
	
	public static boolean isBlockCompleted(int blockNumber) {
		return preferences.getBoolean(BLOCK_COMPLETED + blockNumber, false);
	}
	
	public static void setBlockCompleted(int blockNumber) {
		
		editor.putBoolean(BLOCK_COMPLETED + blockNumber, true);
		editor.commit();
		
	}

}
