package com.alphabets;

import java.util.concurrent.TimeUnit;

import com.alphabets.data.DataLoader;
import com.alphabets.data.DataReadyListener;
import com.alphabets.functional.Keyboard;
import com.alphabets.functional.QuitDialog;
import com.alphabets.progress.ProgressData;
import com.alphabets.view.BlockLayout;
import com.alphabets.view.CustomProgressBar;
import com.alphabets.widgets.BlockCompletedListener;
import com.alphabets.widgets.raw.VisualBlock;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

@SuppressLint("ClickableViewAccessibility")
public class MainActivity extends Activity implements DataReadyListener, BlockCompletedListener {
	
	/*
	 * TODO if switched on for first time sometimes sets block1 as active
	 */
	
	// layouts
	private BlockLayout main;
	private RelativeLayout bottom;
	
	// current item data
	private int currentBlock; // number of current item
	private int amountScrolled;
	private boolean loading;
	
	// items
	private ScrollView scroll;
	private CustomProgressBar progBar;
	
	// progress
	private ProgressData data;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		// instantiate layout
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		scroll = (ScrollView) findViewById(R.id.scroll);
		main = (BlockLayout) findViewById(R.id.main);
		progBar = (CustomProgressBar) findViewById(R.id.progress_bar);
		
		// initialize keyboard functionality
		Keyboard.initialize(this);
		
		// retrieve completion data
		currentBlock = 0;
		loading = true;
		data = new ProgressData(this);
	//	data.clear(); // for testing
		while(data.isBlockCompleted(currentBlock))
			currentBlock++;
				
		DataLoader loader = new DataLoader(this);
		loader.execute(currentBlock);
				
		/*
		 * 
		 *  required to lift the screen up at the beginning
		 *  otherwise widgets stick to the bottom
		 *  
		 */
		bottom = (RelativeLayout) findViewById(R.id.bottom_of_screen);
		main.removeView(bottom);
		main.addView(bottom);
		
	}
	
	/*
	 * 
	 *  Updates current item data
	 *  Scrolls to the next block
	 *  Unlocks the block if needed
	 * 
	 */
		
	private void setFocusedBlock() {
				
		// scroll down to the new focused block
		final int amountToScroll = -getBlockHeight();
		amountScrolled = 0;
		
		// disable actions, so the user doesn't mess up
		// the auto scroll
		main.setOnTouchListener(disableActions);
				
		Thread th = new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				int delay = loading? 0 : 3;
				
				while(amountScrolled>amountToScroll) {

					scroll.scrollBy(0, -1);
					amountScrolled--;
					try {
						TimeUnit.MILLISECONDS.sleep(delay);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					if(amountScrolled==amountToScroll) {
						
						main.setOnTouchListener(enableActions);
						runOnUiThread(new Runnable() {
							@Override
							public void run() {	
								((VisualBlock) main.getChildAt(getCurrentBlock())).setActive();
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
		
	@Override
	public void onBlockCompleted() {
		
		currentBlock++;
		setFocusedBlock();
		
	}
	
	@Override
	public void onBackPressed() {
		
		QuitDialog.show(this);
		
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
	
	/*
	 * 
	 * Utility methods
	 * 
	 */
	
	private int getBlockHeight() {
		return main.getChildAt(1).getHeight();
	}
	
	private int getCurrentBlock() {
		return main.getChildCount() - currentBlock - 2;
	}
	
	/*
	 * 
	 * called whenever a block is loaded up in DataLoader
	 * 
	 */
	
	private boolean firstWasSet;
	
	@Override
	public void blockIsReady(final VisualBlock block) {
						
		// for first addition
		if(!firstWasSet) {
			
			firstWasSet = true;
			
			this.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {

					main.addView(block, 0);
					block.setActive();
					lowestBlockPosition = block.getPosition();
					scrollToCurrentBlock();
					
				}
				
			});
			
			return;
			
		}
		
		final int index = block.getPosition();
		int indexToSet = main.getChildCount()-1;
		int highestPreviousPos = 0;
		
		for(int i=0; i<main.getChildCount(); i++) {
			
			View object = main.getChildAt(i);
			
			if(object instanceof VisualBlock) {
				
				int childPos = ((VisualBlock) object).getPosition();
				if(childPos<index && highestPreviousPos<=childPos) {
					
					indexToSet = i;
					highestPreviousPos = childPos;
				
				}
				
			} else continue;			
			
		}
		
		final int toSet = indexToSet;
		
		this.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				
				main.addView(block, toSet);
				if(index<lowestBlockPosition)
					lowestBlockPosition = index;
				scrollToCurrentBlock();
				
			}
			
		});
		
	}
	
	@Override
	public void loadComplete() {
		loading = false;
	}
	
	/*
	 * 
	 * Called when a new block is loaded up
	 * 
	 */
	
	private int lowestBlockPosition;
	
	private void scrollToCurrentBlock() {
		int addition = currentBlock!=0 ? 1 : 0;
		scroll.scrollTo(0, main.getHeight() - ((currentBlock-lowestBlockPosition+2+addition))*getBlockHeight());		
	}
	
	/*
	 * 
	 * Getter method for progress bar
	 * 
	 */
	
	public CustomProgressBar getProgressBar() {
		return progBar;
	}
	
}