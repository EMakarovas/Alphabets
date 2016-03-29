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
	public static final String BLOCK_COMPLETED_DATA = "Block_Completion_Data_";
	
	public boolean isBlockCompleted(int blockNumber) {
		
		return preferences.getBoolean(BLOCK_COMPLETED + blockNumber, false);
		
	}
	
	public void setBlockCompleted(int blockNumber) {
		
		editor.putBoolean(BLOCK_COMPLETED + blockNumber, true);
		editor.commit();
		
	}
	
	public void setCompletionData(int blockNumber, String data) {
		
		editor.putString(BLOCK_COMPLETED_DATA + blockNumber, data);
		editor.commit();
		
	}
	
	public String getCompletionData(int blockNumber) {

		return preferences.getString(BLOCK_COMPLETED_DATA + blockNumber, "");
		
	}
	
	/*
	 * 
	 * Progress bar stuff
	 * 
	 */
	
	private static final String BLOCKS_SET = "Block_have_been_set";
	private static final String TOTAL_BLOCKS = "Total_blocks";
	private static final String BLOCKS_COMPLETED = "Blocks_completed";
	
	public boolean numberOfBlocksWasSet() {
		
		return preferences.getBoolean(BLOCKS_SET, false);
		
	}
	
	// will only be called once - on first init
	public void setTotalNumberOfBlocks(int number) {
		
		editor.putInt(TOTAL_BLOCKS, number);
		editor.commit();
		editor.putBoolean(BLOCKS_SET, true);
		editor.commit();
		
	}
	
	public int getTotalNumberOfBlocks() {
		return preferences.getInt(TOTAL_BLOCKS, 100);
	}
	
	// called every time a new block is completed
	public void setBlockCompleted() {
		
		int currentCompleted = preferences.getInt(BLOCKS_COMPLETED, 0);
		editor.putInt(BLOCKS_COMPLETED, ++currentCompleted);
		editor.commit();
		
	}
	
	public int getProgress() {		
		return preferences.getInt(BLOCKS_COMPLETED, 0);

	}
	
}
