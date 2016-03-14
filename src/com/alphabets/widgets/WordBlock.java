package com.alphabets.widgets;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.alphabets.MainActivity;
import com.alphabets.R;
import com.alphabets.view.CustomTextView;
import com.alphabets.widgets.raw.NewWord;
import com.alphabets.widgets.raw.OrgWord;

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
import android.widget.LinearLayout;

@SuppressLint("ClickableViewAccessibility")
public class WordBlock extends LinearLayout {
	
	// visual elements
	private EditText writer;
	private CustomTextView mainWord;
	private CustomTextView translationView;
	
	// raw data
	private OrgWord orgWord;
	private NewWord newWord;
	
	// properties
	private boolean locked;
	
	public WordBlock(Context context, String orgWord, String newWord, String translation, boolean locked) {
		
		super(context);
		View.inflate(context, R.layout.word, this);
		
		this.locked = locked;
		
		this.orgWord = new OrgWord(orgWord);
		this.newWord = new NewWord(newWord);
		
		mainWord = (CustomTextView) this.findViewById(R.id.main_word);
		mainWord.setText(this.orgWord.getWord());
		
		translationView = (CustomTextView) this.findViewById(R.id.translation);
		translationView.setText(translation);
		
		writer = (EditText) this.findViewById(R.id.write);
		writer.addTextChangedListener(watcher);		
		
		this.setOnTouchListener(doubleClickListener);
		
	}
	
	/*
	 * 
	 * Retrieve EditText object.
	 * 
	 */
	
	public EditText getWriter() {
		return writer;
	}
	
	/*
	 * 
	 * Text setting methods.
	 * 
	 */
	
	public void setMainWord(Spanned word) {
		mainWord.setText(word);
	}
	
	public void setWriterText(Spanned word) {
		writer.setText(word);
		writer.setSelection(word.length());
	}
	
	/*
	 * 
	 * Deals with text input.
	 * 
	 */
	
	private TextWatcher watcher = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable arg0) {
						
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

		@Override
		public void onTextChanged(CharSequence newTotalText, int start, int oldLetterCount, int newLetterCount) {
			
			handleTextInput(newTotalText.toString(), oldLetterCount, newLetterCount);
			
		}
		
	};
	
	private void handleTextInput(String newTotalText, int oldLetterCount, int newLetterCount) {
		
		if(oldLetterCount!=newLetterCount) {

			newWord.compareInputToWord(newTotalText);
			List<Boolean[]> newWordCompletionData = newWord.getData();
			orgWord.updateData(newWordCompletionData);
			
			updateCurrentBlock();
			
			if(newWord.isCompleted())
				((MainActivity) this.getContext()).finishBlock();							
		
		}
		
	}
	
	private void updateCurrentBlock() {
		
		setMainWord(orgWord.getWord());
		setWriterText(newWord.getUserInput());
		
	}
	
	/*
	 * 
	 * Called out when this object becomes active.
	 * Moves the original word up.
	 * 
	 */
		
	public void setActive() {
		
		writer.requestFocus();
		
		final TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, -1.0f);
		animation.setDuration(1000);
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
			
		}, 500);
		
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
		
		String newInput = newWord.getWithNextBlock();
		writer.setText(newInput);
		
	}
	
	/*
	 * 
	 * Deals with the locked property.
	 * 
	 */
	
	public void setLocked(boolean locked) {
		this.locked = locked;
	}
	
	public boolean isLocked() {
		return locked;
	}

}
