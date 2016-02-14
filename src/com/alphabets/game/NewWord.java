package com.alphabets.game;

import java.util.ArrayList;
import java.util.List;

import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;

public class NewWord {
	
	private String[] blocks;
	private boolean[] correct;
	private boolean[] blockCompleted;
	private List<Boolean[]> dataList;
	private String userInput = "";
	private boolean wordCompleted;
	
	public NewWord(String unformattedWord) {
				
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
	
	public boolean isCompleted() {
		return wordCompleted;
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
	
	public void compareInputToWord(String input) {

		userInput = input;
		int lettersCompleted = userInput.length();
		int arrayCounter = 0;
		int letterCounter = 0;
		
		while(true) {
			
			if(arrayCounter==blocks.length) {
				wordCompleted = true;
				break;
			} else wordCompleted = false;
			
			String wordBlock = blocks[arrayCounter];
			int blockLength = wordBlock.length();
			int inputBlockEnd = letterCounter + blockLength;
			
			if(inputBlockEnd<=lettersCompleted) {
				
				String blockFromInput = input.substring(letterCounter, inputBlockEnd);
				
				blockCompleted[arrayCounter] = true;				
				correct[arrayCounter] = wordBlock.equalsIgnoreCase(blockFromInput);
				
				letterCounter = inputBlockEnd;
				arrayCounter++;
				
			} else break;
			
		}
		
		for(int i=arrayCounter; i<correct.length; i++) { // set arrays to default when deleting text
			
			correct[i] = false;
			blockCompleted[i] = false;
			
		}
		
	}
	
	public List<Boolean[]> getData() {
		
		dataList = new ArrayList<Boolean[]>();
		
		int length = correct.length;
		
		Boolean[] correctToReturn = new Boolean[length];
		Boolean[] completedToReturn = new Boolean[length];
		
		for(int i=0; i<length; i++) {
			
			correctToReturn[i] = Boolean.valueOf(correct[i]);
			completedToReturn[i] = Boolean.valueOf(blockCompleted[i]);
			
		}
		
		dataList.add(correctToReturn);
		dataList.add(completedToReturn);
		
		return dataList;
		
	}
	
	public String getWithNextBlock() {
		
		String toReturn = "";
				
		int alreadyDone = userInput.length();
		
		String checker = "";
		
		for(int i=0; i<blocks.length; i++) {
			
			checker += blocks[i];
			int checkerLength = checker.length();
			
			if(checkerLength>alreadyDone) {
				
				String block = blocks[i];
				
				int untouchableWordLetters = checkerLength - block.length();
				
				String untouchableWordPart = userInput.substring(0, untouchableWordLetters);
				
				toReturn = untouchableWordPart + block;

				break;
				
			}
			
		}
		
		return toReturn;
				
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
	
	public Spanned getUserInput() {
		
		Spanned toReturn = new SpannableString("");
		
		int arrayCounter = 0;
		int letterCounter = 0;
		int lettersCompleted = userInput.length();
		
		while(true) {
			
			if(arrayCounter==blocks.length)
				break;

			String wordBlock = blocks[arrayCounter];
			int blockLength = wordBlock.length();
			int inputBlockEnd = letterCounter + blockLength;
			
			if(inputBlockEnd<=lettersCompleted) {

				String blockFromInput = userInput.substring(letterCounter, inputBlockEnd);
				
				Spanned toAdd = (correct[arrayCounter]) ? this.colorCorrect(blockFromInput) : this.colorWrong(blockFromInput);
									
				toReturn = (Spanned) TextUtils.concat(toReturn, toAdd);
				
				letterCounter = inputBlockEnd;
				arrayCounter++;
				
			} else if(letterCounter<lettersCompleted) {
				
				Spanned toAdd = this.colorNotCompleted(userInput.substring(letterCounter, lettersCompleted)); 
				
				toReturn = (Spanned) TextUtils.concat(toReturn, toAdd);
				
				break;
				
			} else break;
			
		}
		
		return toReturn;
		
	}
		
	private Spanned colorNotCompleted(String block) {
		return (Spanned) Html.fromHtml("<font color=\"#A4A4A4\">" + block + "</font>");
	}
	
	private Spanned colorCorrect(String block) {
		return (Spanned) Html.fromHtml("<font color=\"#00FF00\">" + block + "</font>");
	}
	
	private Spanned colorWrong(String block) {
		return (Spanned) Html.fromHtml("<font color=\"#FA5858\">" + block + "</font>");
	}

}
