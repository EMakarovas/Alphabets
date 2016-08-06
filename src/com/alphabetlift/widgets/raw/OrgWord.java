package com.alphabetlift.widgets.raw;

import java.util.List;
import android.annotation.SuppressLint;
import android.os.Build;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;

public class OrgWord {
	
	private String[] blocks;
	private boolean[] correct;
	private boolean[] blockCompleted;
	
	public OrgWord(String unformattedWord) {
				
		int blockAmount = 0;
		int scanner = 0;
		
		while (scanner<unformattedWord.length()) {
			
			if(unformattedWord.charAt(scanner++)=='.')
				blockAmount++;
			
		}
		
		blocks = new String[blockAmount];
		correct = new boolean[blockAmount]; // initialized to false
		blockCompleted = new boolean[blockAmount];
		
		int blockCounter = 0;
		int start = 0;
		int end;
		
		while(blockCounter<blockAmount) {
			
			end = unformattedWord.indexOf(".", start);
			blocks[blockCounter] = unformattedWord.substring(start, end);
			start = end+1;
			blockCounter++;
			
		}
		
	}
	
	public String getBlock(int blockNumber) {
		return blocks[blockNumber];
	}
	
	public boolean blockIsCorrect(int blockNumber) {
		return correct[blockNumber];
	}
	
	public boolean blockIsCompleted(int blockNumber) {
		return blockCompleted[blockNumber];
	}
	
	public void setBlockCorrect(boolean correct, int blockNumber) {
		this.correct[blockNumber] = correct;
	}
	
	public void setBlockCompleted(boolean completed, int blockNumber) {
		this.blockCompleted[blockNumber] = completed;
	}
	
	public int getBlockNumber(int letterPos) {
		
		String toAdd = "";
		int counter = 0;
		
		while(true) {
			
			toAdd += blocks[counter];
			if(toAdd.length()>letterPos)
				return counter;
			counter++;
			
		}
		
	}
	
	public void updateData(List<Boolean[]> dataList) {
		
		Boolean correctData[] = dataList.get(0);
		Boolean completedData[] = dataList.get(1);
		
		int length = correctData.length;
		
		for(int i=0; i<length; i++) {
			
			correct[i] = correctData[i].booleanValue();
			blockCompleted[i] = completedData[i].booleanValue();
			
		}
		
	}
	
	public Spanned getWord() {
		
		Spanned toReturn = new SpannableString("");
		
		for(int i=0; i<blocks.length; i++) {
			
			Spanned toAdd = null;
			
			if(blockCompleted[i]) {
				
				toAdd = (correct[i]) ? this.colorCorrect(blocks[i]) : this.colorWrong(blocks[i]);
				
			} else toAdd = this.colorNotCompleted(blocks[i]); 
			
			toReturn = (Spanned) TextUtils.concat(toReturn, toAdd);
			
		}
		
		return toReturn;
		
	}
	
	@SuppressLint({ "InlinedApi", "NewApi" })
	@SuppressWarnings("deprecation")
	private Spanned colorNotCompleted(String block) {
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N)
			return (Spanned) Html.fromHtml("<font color=\"#FFFFFF\">" + block + "</font>", Html.FROM_HTML_MODE_LEGACY, null, null);
		return (Spanned) Html.fromHtml("<font color=\"#FFFFFF\">" + block + "</font>");
	}
	
	@SuppressLint({ "InlinedApi", "NewApi" })
	@SuppressWarnings("deprecation")
	private Spanned colorCorrect(String block) {
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N)
			return (Spanned) Html.fromHtml("<font color=\"#22B573\">" + block + "</font>", Html.FROM_HTML_MODE_LEGACY, null, null);
		return (Spanned) Html.fromHtml("<font color=\"#22B573\">" + block + "</font>");
	}
	
	@SuppressLint({ "InlinedApi", "NewApi" })
	@SuppressWarnings("deprecation")
	private Spanned colorWrong(String block) {
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N)
			return (Spanned) Html.fromHtml("<font color=\"#C1272D\">" + block + "</font>", Html.FROM_HTML_MODE_LEGACY, null, null);
		return (Spanned) Html.fromHtml("<font color=\"#C1272D\">" + block + "</font>");
	}

}
