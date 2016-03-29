package com.alphabets.widgets;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.alphabets.MainActivity;
import com.alphabets.R;
import com.alphabets.functional.Keyboard;
import com.alphabets.progress.ProgressData;
import com.alphabets.view.CustomTextView;
import com.alphabets.widgets.raw.WordGroup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;

@SuppressLint("ClickableViewAccessibility")
public class WordBlock extends WordGroup {
	
	// visual elements
	private EditText writer;
	private CustomTextView mainWord;
	private CustomTextView translationView;
	private List<BlockCompletedListener> completionListeners;
	
	private int wordBlockNumber;
	
	public WordBlock(Context context, int position, String orgWord, String newWord, String translation, int wordBlockNumber) {
		
		super(context, position, orgWord, newWord, translation);
		View.inflate(context, R.layout.word_block, this);
		
		completionListeners = new ArrayList<BlockCompletedListener>();
		completionListeners.add((MainActivity) context);
		completionListeners.add(((MainActivity) context).getProgressBar());
				
		mainWord = (CustomTextView) this.findViewById(R.id.main_word);
		mainWord.setText(getOrgWord().getWord());
		
		translationView = (CustomTextView) this.findViewById(R.id.translation);
		translationView.setText(getTranslation());
		
		writer = (EditText) this.findViewById(R.id.write);
		writer.addTextChangedListener(watcher);	
		
		this.wordBlockNumber = wordBlockNumber;
		
		this.setOnTouchListener(doubleClickListener);
		
		writer.setFocusable(false);
		
		if(isCompleted()) {
			insertWriterText(new ProgressData(this.getContext()).getCompletionData(getPosition()));
			performAnimation(0, 0);
		}
		
	}
	
	/*
	 * 
	 * Get word block number - used for
	 * progress bar.
	 * 
	 */
	
	public int getBlockNumber() {
		return wordBlockNumber;
	}
	
	/*
	 * 
	 * Text setting methods.
	 * 
	 */
	
	private void setMainWord(Spanned word) {
		mainWord.setText(word);
	}
	
	private void setWriterText(Spanned word) {
		writer.setText(word);
		writer.setSelection(word.length());
	}
	
	private void insertWriterText(String word) {
		writer.setText(word);
	}
	
	/*
	 * 
	 * Deals with text input.
	 * 
	 */
	
	private TextWatcher watcher = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable arg0) {}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

		@Override
		public void onTextChanged(CharSequence newTotalText, int start, int oldLetterCount, int newLetterCount) {
			
			handleTextInput(newTotalText.toString(), oldLetterCount, newLetterCount);
			
		}
		
	};
	
	private void handleTextInput(String newTotalText, int oldLetterCount, int newLetterCount) {
		
		if(oldLetterCount!=newLetterCount) {

			getNewWord().compareInputToWord(newTotalText);
			List<Boolean[]> newWordCompletionData = getNewWord().getData();
			getOrgWord().updateData(newWordCompletionData);
			
			updateCurrentBlock();
			
			if(getNewWord().isCompleted() && getIsCurrent())
				completeBlock();
		
		}
		
	}
	
	@Override
	public void completeBlock() {
		
		Keyboard.hide();
		
		ProgressData data = new ProgressData(this.getContext());
		
		data.setCompletionData(getPosition(), getNewWord().getUserInput().toString());
		data.setBlockCompleted();

		setCompleted(true);
		setIsCurrent(false);
		writer.setFocusable(false);
		writer.setFocusableInTouchMode(false);
		
		for(BlockCompletedListener listener : completionListeners)
			listener.onBlockCompleted();		
		
	}
	
	private void updateCurrentBlock() {
		
		setMainWord(getOrgWord().getWord());
		setWriterText(getNewWord().getUserInput());
		
	}
	
	/*
	 * 
	 * Called out when this object becomes active.
	 * Moves the original word up.
	 * 
	 */
		
	@Override
	public void setActive() {
		
		setIsCurrent(true);
		
		writer.setFocusableInTouchMode(true);
		writer.requestFocus();
		Keyboard.show();
		performAnimation(1000, 500);		
		
	}
	
	private void performAnimation(final int duration, final int offset) {
		
		((MainActivity) this.getContext()).runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				
				final TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
						Animation.RELATIVE_TO_SELF, 0.0f,
						Animation.RELATIVE_TO_PARENT, 0.0f,
						Animation.RELATIVE_TO_PARENT, -1.0f);
				animation.setDuration(duration);
				animation.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationEnd(Animation animation) {
						
						// for some reason calling setTranslationY()
						// straightaway messes up the animation
						// added 1 sec delay to fix this issue
						final Handler handler = new Handler();
						handler.postDelayed(new Runnable() {
							public void run() {
								mainWord.setTranslationY(-writer.getHeight());

							}
						}, 1);
						
					}

					@Override
					public void onAnimationRepeat(Animation animation) {				
					}

					@Override
					public void onAnimationStart(Animation animation) {}
			
				});
				
				final Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						mainWord.startAnimation(animation);
					}
					
				}, offset);
				
			}
			
		});
		
	}
	
	/*
	 * 
	 * Deals with double click actions.
	 * 
	 */
	
	private long lastClickTime = 0;
	
	private OnTouchListener doubleClickListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
						
			int action = event.getAction();
			
			if(action==MotionEvent.ACTION_DOWN) {
							
				v.performClick();
				
				final long currentClickTime = System.currentTimeMillis();
				
				// is double click
				if(currentClickTime - lastClickTime<250) {
					
					// handle double click action
					if(getIsCurrent())
						handleDoubleClick();

					// reset millis counter
					lastClickTime = 0;
					
				// single click
				} else {
					
					lastClickTime = currentClickTime;
					
					Thread th = new Thread(new Runnable() {
						
						@Override
						public void run() {
						
							long timeAtStart = currentClickTime;
							
							try {
								TimeUnit.MILLISECONDS.sleep(249);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							
							if(timeAtStart==lastClickTime) {
								
								// single click, because lastClickTime has not changed
								
							}
														
						}
							
					});
					
					th.start();
					
				}
											
			}
			
			return false;
			
		}
				
	};
	
	private void handleDoubleClick() {
		
		Spanned newInput = getNewWord().getWithNextBlock();
		setWriterText(newInput);
		
	}

}
