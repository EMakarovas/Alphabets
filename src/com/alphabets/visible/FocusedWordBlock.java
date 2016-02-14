package com.alphabets.visible;

import java.util.concurrent.TimeUnit;

import com.alphabets.MainActivity;
import com.alphabets.R;
import com.alphabets.view.CustomTextView;
import android.annotation.SuppressLint;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
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
	
	private long lastClickTime = 0;
	
	private OnTouchListener touchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			int action = event.getAction();
			
			if(action==MotionEvent.ACTION_DOWN) {
							
				v.performClick();
				
				final long currentClickTime = System.currentTimeMillis();
				
				Log.e("STUFF", "last: " + lastClickTime + ", current: " + currentClickTime + ", diff: " + 
				(currentClickTime-lastClickTime));
				
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
								
								Log.e("STUFF", "single click");
								
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
