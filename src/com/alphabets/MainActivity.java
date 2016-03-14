package com.alphabets;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.alphabets.data.Loader;
import com.alphabets.functional.Keyboard;
import com.alphabets.functional.QuitDialog;
import com.alphabets.progress.Data;
import com.alphabets.widgets.Message;
import com.alphabets.widgets.WordBlock;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

public class MainActivity extends Activity {
	
	// layouts
	private LinearLayout main;
	private RelativeLayout bottom;
	
	// raw data
	private Map<Integer, String[]> positionMap;
	
	// widgets
	private WordBlock[] wordBlock;
	private Message[] message;
	
	// current item data
	private boolean currentIsWord;
	private int currentItem; // number of current type of item, e.g. 3rd WordBlock, or 5th Message
	private int currentBlock; // number of current item disregarding the type
	private int blocks;
	private int amountScrolled;
	
	// other
	private int blockHeight;
	private ScrollView scroll;
	float coord = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		// instantiate layout
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		scroll = (ScrollView) findViewById(R.id.scroll);
		main = (LinearLayout) findViewById(R.id.main);
		
		// initialize keyboard functionality
		Keyboard.initialize(this);
		
		/*
		 * TODO sort out getting progress data and
		 * 		injecting in here
		 */
		
		// retrieve completion data
		currentBlock = 0;
		currentItem = 0;
		while(Data.isBlockCompleted(currentBlock))
			currentBlock++;
		
		wordBlock = Loader.getWordBlocks();
		message = Loader.getMessages();
		positionMap = Loader.getPositionMap();
		
		int counter = 0;
		long time = System.currentTimeMillis();
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
		
		Log.e("STUFF", "end placing at " + (System.currentTimeMillis()-time));
		
		/*
		 * 
		 *  required to lift the screen up at the beginning
		 *  otherwise widgets stick to the bottom
		 *  
		 */
		bottom = (RelativeLayout) findViewById(R.id.bottom_of_screen);
		main.removeView(bottom);
		main.addView(bottom);
		
		blocks = main.getChildCount();
		
		// scroll to current block
		scroll.post(new Runnable() {
		
			@Override
			public void run() {
				
				blockHeight = wordBlock[0].getHeight();
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
							runOnUiThread(new Runnable() {
							
								@Override
								public void run() {
									
									wordBlock[currentItem].getWriter().requestFocus();
									Keyboard.show();
									
								}
							
							});		
						
					}
					
				}
				
			}
		
		});
		
		setFocusedBlock();
		
	}
	
	/*
	 * 
	 *  Updates current item data
	 *  Scrolls to the next block
	 *  Unlocks the block if needed
	 * 
	 */
		
	private void setFocusedBlock() {
		
		// update current item data
		String[] blockData = positionMap.get(currentBlock);
		currentIsWord = blockData[0].equals("w");
		currentItem = Integer.parseInt(blockData[1]);
		
		// scroll down to the new focused block
		final int amountToScroll = (currentBlock==0) ? 0 : -blockHeight;
		amountScrolled = 0;
		
		// disable actions, so the user doesn't mess up
		// the auto scroll
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
							runOnUiThread(new Runnable() {
							
								@Override
								public void run() {
									
									wordBlock[currentItem].setActive();
									Keyboard.show();
									
								}
							
							});					
						
					}
					
				}
				
			}
			
		});
		th.start();
		
	}
	
	/*
	 * 
	 * Called upon completion of a word.
	 * 
	 */
		
	/*
	 * 
	 * TODO need to sort this to update progress,
	 * 		set stuff as unlocked
	 * 
	 */
	public void finishBlock() {
				
		if(currentIsWord) {
		
			Keyboard.hide();
		
		} else {
			
//			main.addView(message[currentItem], this.getBlockNumber());
			
		}
		
		currentBlock++;
		this.setFocusedBlock();
		
	}
	
	/*
	 * 
	 * Using this method as the layout is from bottom to top
	 * 
	 */
	
	/*
	 * 
	 * TODO not removing yet as might be useful later.
	 * 
	 */
	@SuppressWarnings("unused")
	private int getBlockNumber() {		
		return blocks - 2 - currentBlock;
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