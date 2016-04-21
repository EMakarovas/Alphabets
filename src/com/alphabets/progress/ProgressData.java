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
	
	/*
	 * 
	 * Mistakes
	 * 
	 */
	
	private static final String MISTAKES_COMMITTED = "Mistakes_committed";
	
	public void setMistakeCommitted() {
		
		int currentMistakes = preferences.getInt(MISTAKES_COMMITTED, 0);
		editor.putInt(MISTAKES_COMMITTED, ++currentMistakes);
		editor.commit();
		
	}
	
	public int getMistakesCommitted() {
		return preferences.getInt(MISTAKES_COMMITTED, 0);
	}
	
	/*
	 * 
	 * Help
	 * 
	 */
	
	private static final int BLOCKS_TO_REFRESH = 2;
	private static final int MAX_HELP_COUNT = 10;
	
	private static final String HELP_COUNTER = "Help_counter";
	private static final String HELP_REFRESH = "Help_refreshes_at";
	
	public void setHelpUsed(int blockNumber) {
		
		int helpCount = preferences.getInt(HELP_COUNTER, MAX_HELP_COUNT);
		editor.putInt(HELP_COUNTER, --helpCount);
		
		String refreshKey = HELP_REFRESH + (blockNumber+BLOCKS_TO_REFRESH);
		int currentRefresh = preferences.getInt(refreshKey, 0);
		editor.putInt(refreshKey, ++currentRefresh);
		
		editor.commit();
		
	}
	
	public boolean refreshHelpCount(int blockNumber) {
		
		String refreshKey = HELP_REFRESH + blockNumber;
		int refreshCount = preferences.getInt(refreshKey, 0);
		if(refreshCount==0)
			return false;
		
		editor.putInt(refreshKey, 0);
		
		int currentHelpCount = preferences.getInt(HELP_COUNTER, MAX_HELP_COUNT);
		int newHelpCount = currentHelpCount + refreshCount;
		
		editor.putInt(HELP_COUNTER, newHelpCount);
		editor.commit();
		
		return true;
		
	}
	
	public int getHelpCount() {
		return preferences.getInt(HELP_COUNTER, MAX_HELP_COUNT);
	}
	
}
