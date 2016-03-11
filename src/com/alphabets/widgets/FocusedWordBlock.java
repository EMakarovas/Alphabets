package com.alphabets.widgets;

import java.util.concurrent.TimeUnit;

import com.alphabets.MainActivity;
import com.alphabets.R;
import com.alphabets.view.CustomTextView;

import android.annotation.SuppressLint;
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
public class FocusedWordBlock extends LinearLayout {
	
	private EditText writer;
	private CustomTextView mainWord;
	private CustomTextView translationView;
	private MainActivity act;
	
	public FocusedWordBlock(MainActivity activity, Spanned word, String translation) {
		
		super(activity);
		View.inflate(activity, R.layout.focused_word, this);
		
		act = activity;
						
		mainWord = (CustomTextView) this.findViewById(R.id.main_word);
		mainWord.setText(word);
		
		translationView = (CustomTextView) this.findViewById(R.id.translation);
		translationView.setText(translation);
		
		writer = (EditText) this.findViewById(R.id.write);
		writer.addTextChangedListener(watcher);		
		
		this.setOnTouchListener(touchListener);
		
		this.setActive();
		
	}
	
	public EditText getWriter() {
		return writer;
	}
	
	public void setMainWord(Spanned word) {
		mainWord.setText(word);
	}
	
	public void setWriterText(Spanned word) {
		writer.setText(word);
		writer.setSelection(word.length());
	}
	
	private String wordBeingReplaced;
	private int startOfChange, oldLetterCount, newLetterCount;
	
	private TextWatcher watcher = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable arg0) {

			act.handleTextInput(wordBeingReplaced, startOfChange, oldLetterCount, newLetterCount);
			
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			
			wordBeingReplaced = s.toString();
			startOfChange = start;
			oldLetterCount = before;
			newLetterCount = count;
			
		}
		
	};
		
	public void setActive() {
		
		final TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, -1.0f);
		animation.setDuration(1500);
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
	
	private long lastClickTime = 0;
	
	private OnTouchListener touchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			int action = event.getAction();
			
			if(action==MotionEvent.ACTION_DOWN) {
							
				v.performClick();
				
				final long currentClickTime = System.currentTimeMillis();
				
				if(currentClickTime - lastClickTime<250) {
					
					act.handleDoubleClick();
					lastClickTime = currentClickTime;
					
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
								
								// single click
								
							}
														
						}
							
					});
					
					th.start();
					
				}
											
			}
			
			return false;
			
		}
				
	};

}
