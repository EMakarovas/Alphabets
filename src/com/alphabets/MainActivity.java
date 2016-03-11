package com.alphabets;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.alphabets.game.Data;
import com.alphabets.game.DataReader;
import com.alphabets.game.NewWord;
import com.alphabets.game.OrgWord;
import com.alphabets.widgets.FocusedMessage;
import com.alphabets.widgets.FocusedWordBlock;
import com.alphabets.widgets.UnfocusedMessage;
import com.alphabets.widgets.UnfocusedWordBlock;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

public class MainActivity extends Activity {
	
	// layouts
	private LinearLayout main;
	private RelativeLayout bottom;
	
	// raw data
	private OrgWord[] orgWord;
	private NewWord[] newWord;
	private String[] translation;
	private String[] msg;
	private Map<Integer, String[]> positionMap;
	
	// unfocused blocks, shown all the time
	private UnfocusedWordBlock[] wordBlock;
	private UnfocusedMessage[] message;
	
	// focused item
	private FocusedWordBlock focusWord;
	private FocusedMessage focusMessage;
	
	// current item data
	private boolean currentIsWord;
	private int currentItem;
	private int currentBlock;
	private int blocks;
	private int amountScrolled;
	
	// other
	private int blockHeight;
	private MainActivity act;
	private ScrollView scroll;
	float coord = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		// instantiate layout
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		scroll = (ScrollView) findViewById(R.id.scroll);
		main = (LinearLayout) findViewById(R.id.main);
		bottom = (RelativeLayout) findViewById(R.id.bottom_of_screen);
		main.removeView(bottom);
		act = this;
		
		// retrieve completion data
		currentBlock = 0;
		currentItem = 0;
		while(Data.isBlockCompleted(currentBlock))
			currentBlock++;
		
		// retrieve game data
		DataReader gameData = null;
		try {
			gameData = new DataReader(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
				
		String[] orgWords = gameData.getOrgWords();
		String[] newWords = gameData.getNewWords();
		translation = gameData.getTranslations();
		msg = gameData.getMessages();
		positionMap = gameData.getPositionMap();
		
		orgWord = new OrgWord[orgWords.length];
		newWord = new NewWord[newWords.length];
		wordBlock = new UnfocusedWordBlock[orgWords.length];
		message = new UnfocusedMessage[msg.length];
				
		for(int i=0; i<orgWords.length; i++) {

			orgWord[i] = new OrgWord(orgWords[i]);
			newWord[i] = new NewWord(newWords[i]);
			wordBlock[i] = new UnfocusedWordBlock(this, orgWord[i].getWord());
			
		}
		
		for(int i=0; i<msg.length; i++)
			message[i] = new UnfocusedMessage(this, msg[i]);
		
		int counter = 0;
		
		for(int i=positionMap.size()-1; i>=0; i--) {
			
			String[] object = positionMap.get(i);
			String type = object[0];
			int number = Integer.parseInt(object[1]);
			
			if(type.equals("m"))
				main.addView(message[number], counter);
			else if(type.equals("w"))
				main.addView(wordBlock[number], counter);
			
			counter++;
			
		}
		
		main.addView(bottom);
		
		blocks = main.getChildCount();
		
		// scroll to current block
		scroll.post(new Runnable() {
		
			@Override
			public void run() {
				
				blockHeight = (currentBlock+1==wordBlock.length) ? wordBlock[0].getHeight() : wordBlock[wordBlock.length-1].getHeight();
				main.setOnTouchListener(disableActions);
				int amountToScroll = main.getHeight() - currentBlock*(-blockHeight);
				amountScrolled = 0;
				
				while(amountScrolled<amountToScroll) {

					scroll.scrollBy(0, 1);
					amountScrolled++;
					try {
						TimeUnit.MICROSECONDS.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					if(amountScrolled==amountToScroll) {
						
						main.setOnTouchListener(enableActions);
						String[] blockData = positionMap.get(currentBlock);
						currentIsWord = blockData[0].equals("w");
						if(currentIsWord)
							act.runOnUiThread(new Runnable() {
							
								@Override
								public void run() {
									
									focusWord.getWriter().requestFocus();
									act.showKeyboard();
									
								}
							
							});		
						
					}
					
				}
				
			}
		
		});
		
		this.setFocusedBlock();
		
	}
		
	private void setFocusedBlock() {
		
		// create new focused block
		String[] blockData = positionMap.get(currentBlock);
		currentIsWord = blockData[0].equals("w");
		currentItem = Integer.parseInt(blockData[1]);
		if(currentIsWord)
			focusWord = new FocusedWordBlock(this, orgWord[currentItem].getWord(), translation[currentItem]);
		else focusMessage = new FocusedMessage(this, msg[currentItem]);
		
		// replace next word set with the focused block
		main.removeViewAt(this.getBlockNumber());
		if(currentIsWord)
			main.addView(focusWord, this.getBlockNumber());
		else main.addView(focusMessage, this.getBlockNumber());
		
		// scroll down to the new focused block
		final int amountToScroll = (currentBlock==0) ? 0 : -blockHeight;
		amountScrolled = 0;
		
		main.setOnTouchListener(disableActions);
		
		Thread th = new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				while(amountScrolled>amountToScroll) {

					scroll.scrollBy(0, -1);
					amountScrolled--;
					try {
						TimeUnit.MILLISECONDS.sleep(3);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					if(amountScrolled==amountToScroll) {
						
						main.setOnTouchListener(enableActions);
						if(currentIsWord)
							act.runOnUiThread(new Runnable() {
							
								@Override
								public void run() {
									
									focusWord.getWriter().requestFocus();
									act.showKeyboard();
									
								}
							
							});					
						
					}
					
				}
				
			}
			
		});
		th.start();
		
	}
	
	public void handleTextInput(String newTotalText, int startOfChange, int oldLetterCount, int newLetterCount) {

		if(oldLetterCount!=newLetterCount) {
			
			newWord[currentItem].compareInputToWord(newTotalText);
			List<Boolean[]> newWordCompletionData = newWord[currentItem].getData();
			orgWord[currentItem].updateData(newWordCompletionData);
			
			if(newWord[currentItem].isCompleted())
				finishBlock();
			else updateCurrentBlock();
							
		
		}
		
	}
	
	public void handleDoubleClick() {
		
		String newInput = newWord[currentItem].getWithNextBlock();
		handleTextInput(newInput, 0, 0, 1);
		
	}
		
	public void finishBlock() {
		
		main.removeViewAt(this.getBlockNumber());
		
		if(currentIsWord) {
		
			hideKeyboard();
			
			main.addView(wordBlock[currentItem], this.getBlockNumber());
			wordBlock[currentItem].setNewHidden(false);
			wordBlock[currentItem].setOrgText(orgWord[currentItem].getWord());
			wordBlock[currentItem].setNewText(newWord[currentItem].getUserInput());
		
		} else {
			
			main.addView(message[currentItem], this.getBlockNumber());
			
		}
				
		currentBlock++;
		this.setFocusedBlock();
		
	}
	
	private void updateCurrentBlock() {
		
		focusWord.setMainWord(orgWord[currentItem].getWord());
		focusWord.setWriterText(newWord[currentItem].getUserInput());
		
	}
	
	private int getBlockNumber() {		
		return blocks - 2 - currentBlock;
	}
	
	private void hideKeyboard() {
		
	    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
	    View view = getCurrentFocus();
	    if (view == null) {
	        view = new View(this);
	    }
	    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	    
	}
	
	private void showKeyboard() {
		
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
	    if (inputMethodManager != null)
	        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
		
	}
	
	@Override
	public void onBackPressed() {
		
		QuitDialog.showQuitDialog(this);
		
	}
	
	@SuppressLint("ClickableViewAccessibility")
	private OnTouchListener disableActions = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			return true; // consume all events
		}
		
	};
	
	@SuppressLint("ClickableViewAccessibility")
	private OnTouchListener enableActions = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			return v.onTouchEvent(event);
		}
		
	};
	
}