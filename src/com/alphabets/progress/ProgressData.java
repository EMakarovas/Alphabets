package com.alphabets.progress;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ProgressData {
		
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	
	@SuppressLint("CommitPrefEdits") // ignore
	public ProgressData(Context context) {
		
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		editor = preferences.edit();
		
	}
	
	public void clear() {
		editor.clear();
		editor.commit();
	}
	
	public static final String BLOCK_COMPLETED = "Block_Completed_";
	public static final String BLOCK_UNLOCKED = "Block_Unlocked_";
	public static final String BLOCK_COMPLETED_DATA = "Block_Completion_Data_";
	
	public boolean isBlockCompleted(int blockNumber) {
		
		return preferences.getBoolean(BLOCK_COMPLETED + blockNumber, false);
		
	}
	
	public void setBlockCompleted(int blockNumber) {
		
		editor.putBoolean(BLOCK_COMPLETED + blockNumber, true);
		editor.commit();
		
	}
	
	public boolean isBlockUnlocked(int blockNumber) {
		
		if(blockNumber==0) return true;
		return preferences.getBoolean(BLOCK_UNLOCKED + blockNumber, false);
		
	}
	
	public void setBlockUnlocked(int blockNumber) {
		
		editor.putBoolean(BLOCK_UNLOCKED + blockNumber, true);
		editor.commit();
		
	}
	
	public void setCompletionData(int blockNumber, String data) {
		
		editor.putString(BLOCK_COMPLETED_DATA + blockNumber, data);
		editor.commit();
		
	}
	
	public String getCompletionData(int blockNumber) {

		return preferences.getString(BLOCK_COMPLETED_DATA + blockNumber, "");
		
	}

}
