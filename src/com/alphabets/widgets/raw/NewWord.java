package com.alphabets.widgets.raw;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Build;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;

public class NewWord {
	
	private String[][] blocks;
	private boolean[] correct;
	private boolean[] blockCompleted;
	private List<Boolean[]> dataList;
	private String[] userInput;
	private boolean wordCompleted;
	private boolean disableMoreText;
	
	public NewWord(String unformattedWord) {
						
		int blockAmount = 0;
		int scanner = 0;
		
		while (scanner<unformattedWord.length()) {
			
			if(unformattedWord.charAt(scanner++)=='.')
				blockAmount++;
			
		}
		
		blocks = new String[blockAmount][];
		correct = new boolean[blockAmount]; // initialized to false
		blockCompleted = new boolean[blockAmount];
		userInput = new String[blockAmount];
		
		int blockCounter = 0;
		int start = 0;
		
		while(blockCounter<blockAmount) {
			
			int end = unformattedWord.indexOf(".", start);
			String unformattedBlock = unformattedWord.substring(start, end);
			boolean hasMultipleCorrect = unformattedBlock.contains("/");
			List<String> possibleBlocks = new ArrayList<String>();
			
			if(hasMultipleCorrect) {
				
				while(unformattedBlock.length()>0) {
					
					int slashPos = unformattedBlock.indexOf("/");
					String smallBlock = unformattedBlock.substring(0, slashPos);
					unformattedBlock = unformattedBlock.substring(slashPos+1);
					possibleBlocks.add(smallBlock);
					
				}
				
			} else possibleBlocks.add(unformattedBlock);
			
			blocks[blockCounter] = new String[possibleBlocks.size()];
			
			for(int i=0; i<possibleBlocks.size(); i++) {
				
				Object obj = possibleBlocks.get(i);
				
				blocks[blockCounter][i] = obj.toString();
				
			}
			
			start = end+1;
			blockCounter++;
			
		}
		
	}
	
	public boolean isCompleted() {
		return wordCompleted;
	}
	
	public boolean isMoreTextDisabled() {
		return disableMoreText;
	}
	
	public void compareInputToWord(String input) {
						
		int inputLength = input.length();
		int posCounter = 0;
		int blockCounter = 0;
		String currentBlock = "";
		
		if(inputLength==0)
			userInput[0] = "";
		
		wordLoop: while(posCounter<inputLength) {
			
			currentBlock += input.charAt(posCounter);
						
			for(int i=0; i<blocks[blockCounter].length; i++) {
				
				String correctBlock = blocks[blockCounter][i];
				
				// check if any correct blocks are equal to current
				if(currentBlock.equalsIgnoreCase(correctBlock)) {
					
					blockCompleted[blockCounter] = true;
					correct[blockCounter] = true;
					userInput[blockCounter] = currentBlock;
					currentBlock = "";
					blockCounter++;
					posCounter++;
					continue wordLoop;
					
				} 
					
			}
				
			/*
			 * reaches this point if currentBlock did not
			 * find a match.
			 * check if there could be potential matches later
			 * (because blocks[][] has a longer String)
			 */
			
			// gets the longest length of potential blocks				
			int longestLength = 0;
			for(int j=0; j<blocks[blockCounter].length; j++)
				if(blocks[blockCounter][j].length()>longestLength)
					longestLength = blocks[blockCounter][j].length();
							
			// no more potentially correct blocks - the block is incorrect
			if(currentBlock.length()==longestLength) {
												
				blockCompleted[blockCounter] = true;
				correct[blockCounter] = false;
				userInput[blockCounter] = currentBlock;
				currentBlock = "";
				blockCounter++;
				posCounter++;
				continue wordLoop;
											
			}
			
			/*
			 * Reaches this if currentBlock did not find a match
			 * AND there are potential blocks later
			 */
						
			// unfinished multiple letter block
			if(posCounter==inputLength-1) {
				
				userInput[blockCounter] = currentBlock;
				correct[blockCounter] = false;
				blockCompleted[blockCounter] = false;
				blockCounter++;
				break wordLoop;
				
			}
			
			/*
			 * Reaches this if currentBlock did not find a match
			 * AND there are potential blocks later
			 * AND this isn't the last letter in input
			 */
			
			userInput[blockCounter] = currentBlock;
			posCounter++;
			
		}
		
		for(int i=blockCounter; i<correct.length; i++) { // set arrays to default when deleting text
			
			correct[i] = false;
			blockCompleted[i] = false;
			userInput[i] = "";
			
		}	
		
		// Set word as completed
		boolean completed = true;
		for(int i=0; i<blockCompleted.length; i++)
			if(!blockCompleted[i] || !correct[i]) {
				
				completed = false;
				break;
				
			}
		
		// Disable writing more text
		// (Word is full but incorrect - only deleting allowed)
		boolean disableText = true;
		for(int i=0; i<blockCompleted.length; i++)
			if(!blockCompleted[i]) {
				
				disableText = false;
				break;
				
			}
			
		wordCompleted = completed;
		disableMoreText = disableText;
		
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
	
	public Spanned getWithNextBlock() {
		
		Spanned toReturn = new SpannableString("");
		
		int blockCounter = 0;

		while(blockCounter<blocks.length) {
									
			if(blockCompleted[blockCounter]) {
				
				Spanned toAdd = (correct[blockCounter]) ? colorCorrect(userInput[blockCounter]) : colorWrong(userInput[blockCounter]);				
				toReturn = (Spanned) TextUtils.concat(toReturn, toAdd);
				
			} else {
				
				Spanned toAdd = colorCorrect(blocks[blockCounter][0]);
				toReturn = (Spanned) TextUtils.concat(toReturn, toAdd);
				break;
				
			}
			
			blockCounter++;
			
		}
		
		return toReturn;
				
	}
	
	public Spanned getUserInput() {
		
		Spanned toReturn = new SpannableString("");
		
		int blockCounter = 0;

		while(blockCounter<blocks.length) {
									
			if(blockCompleted[blockCounter]) {
				
				Spanned toAdd = (correct[blockCounter]) ? colorCorrect(userInput[blockCounter]) : colorWrong(userInput[blockCounter]);			
				toReturn = (Spanned) TextUtils.concat(toReturn, toAdd);
				
			} else {
				
				if(userInput[blockCounter]==null && userInput[blockCounter].equals(""))
					break;
				
				Spanned toAdd = colorNotCompleted(userInput[blockCounter]);
				
				toReturn = (Spanned) TextUtils.concat(toReturn, toAdd);
				
				break;
				
			}
			
			blockCounter++;
			
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
	
	/*
	 * 
	 * Mistakes
	 * 
	 */

	private int previousCompletedBlocks;
	private int previousCorrectBlocks;
	
	public boolean mistakeWasCommitted() {
		
		int completedBlocks = blockCompleted.length;
		for(int i=0; i<blockCompleted.length; i++)
			if(!blockCompleted[i]) {
				completedBlocks = i;
				break;
			}
		
		int correctBlocks = 0;
		for(int i=0; i<correct.length; i++)
			if(correct[i]) 
				correctBlocks++;
				
		if(previousCompletedBlocks==completedBlocks) {
			
			previousCompletedBlocks = completedBlocks;
			previousCorrectBlocks = correctBlocks;
			return false;
			
		}

		
		if(completedBlocks==previousCompletedBlocks+1) {
			
			if(correctBlocks==previousCorrectBlocks+1) {
				
				previousCompletedBlocks = completedBlocks;
				previousCorrectBlocks = correctBlocks;
				return false;
				
			}
			
			previousCompletedBlocks = completedBlocks;
			previousCorrectBlocks = correctBlocks;
			return true;
			
		}
		
		if(completedBlocks==previousCompletedBlocks-1) {
			
			previousCompletedBlocks = completedBlocks;
			previousCorrectBlocks = correctBlocks;
			return false;
			
		}
		
		// will never reach this
		return false;
		
	}

}
