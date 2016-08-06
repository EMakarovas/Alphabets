package com.alphabetlift.widgets;

import java.util.ArrayList;
import java.util.List;

import com.alphabetlift.MainActivity;
import com.alphabetlift.R;
import com.alphabetlift.constants.Action;
import com.alphabetlift.constants.DoorEvent;
import com.alphabetlift.constants.Scroll;
import com.alphabetlift.progress.ProgressData;
import com.alphabetlift.progress.ProgressDataFactory;
import com.alphabetlift.view.CustomTextView;
import com.alphabetlift.widgets.raw.WordGroup;

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
public class WordBlock extends WordGroup implements HelpListener {
	
	// visual elements
	private EditText writer;
	private CustomTextView mainWord;
	private CustomTextView translationView;
	private List<BlockCompletedListener> completionListeners;
	private MistakeListener mistakeListener;
	private ProgressData data;
	
	private int wordBlockNumber;
	
	public WordBlock(Context context, int position, String orgWord, String newWord, String translation, int wordBlockNumber) {
		
		super(context, position, orgWord, newWord, translation);
		View.inflate(context, R.layout.word_block, this);
		
		completionListeners = new ArrayList<BlockCompletedListener>();
		completionListeners.add((MainActivity) context);
		completionListeners.add(((MainActivity) context).getProgressArrow());
		
		mistakeListener = ((MainActivity) context).getMistakeCounter();
				
		mainWord = (CustomTextView) this.findViewById(R.id.main_word);
		mainWord.setText(getOrgWord().getWord());
		
		translationView = (CustomTextView) this.findViewById(R.id.translation);
		translationView.setText(getTranslation());
		
		writer = (EditText) this.findViewById(R.id.write);
		writer.addTextChangedListener(watcher);	
		
		data = ProgressDataFactory.getProgressData();
		
		this.wordBlockNumber = wordBlockNumber;
				
		writer.setFocusable(false);
		writer.setOnTouchListener(writerTouchListener);
		
		if(isCompleted()) {
			insertWriterText(data.getCompletionData(getPosition()));
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
		
		if(oldLetterCount<newLetterCount && getNewWord().isMoreTextDisabled())
			updateCurrentBlock(); // ignores what was typed in
		else if(oldLetterCount>newLetterCount || (oldLetterCount<newLetterCount && !getNewWord().isMoreTextDisabled())) {

			getNewWord().compareInputToWord(newTotalText);
			List<Boolean[]> newWordCompletionData = getNewWord().getData();
			getOrgWord().updateData(newWordCompletionData);
			
			updateCurrentBlock();
			
			if(getNewWord().mistakeWasCommitted() && getIsCurrent()) {
								
				data.setMistakeCommitted();
				mistakeListener.mistakeCommitted();
				
			}
			
			if(getNewWord().isCompleted() && getIsCurrent())
				completeBlock();
		
		}
		
	}
	
	@Override
	public void completeBlock() {
					
		data.setCompletionData(getPosition(), getNewWord().getUserInput().toString());
		data.setBlockCompleted();

		setCompleted(true);
		setIsCurrent(false);
		writer.setFocusable(false);
		writer.setFocusableInTouchMode(false);
		
		for(BlockCompletedListener listener : completionListeners)
			listener.onBlockCompleted(Scroll.NORMAL);		
		
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
	public void setActive(Action action) {
		
		data.refreshHelpCount(wordBlockNumber);
	
		setIsCurrent(true);
		writer.setFocusableInTouchMode(true);
		writer.requestFocus();
		performAnimation(1000, 500);		
		
	}
	
	private void performAnimation(final int duration, final int offset) {
		
		((MainActivity) this.getContext()).runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				
				final TranslateAnimation animation = new TranslateAnimation(
						Animation.RELATIVE_TO_SELF, 0.0f,
						Animation.RELATIVE_TO_SELF, 0.0f,
						Animation.RELATIVE_TO_PARENT, 0.0f,
						Animation.RELATIVE_TO_PARENT, -1.0f);
				animation.setDuration(duration);
				animation.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationEnd(Animation animation) {
						
						final Handler handler = new Handler();
						handler.postDelayed(new Runnable() {
							
							@Override
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
	 * EditText's listener. Disables clicking between
	 * current input letters.
	 * 
	 */
	
	private OnTouchListener writerTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View view, MotionEvent event) {
			
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					
					writer.setSelection(writer.getText().length());
					
				}
				
			}, 50);
			
			return false;
		}	
		
	};

	@Override
	public void helpUsed() {
				
		if(getIsCurrent() && !getNewWord().isMoreTextDisabled()){
			
			if(data.getHelpCount()==0) {
				((MainActivity) this.getContext()).showDoorAnimation(this.getContext().getResources().getString(R.string.no_more_help), DoorEvent.NONE);
				return;
			}
			
			data.setHelpUsed(wordBlockNumber);
			Spanned newInput = getNewWord().getWithNextBlock();
			setWriterText(newInput);
		
		}
		
	}

}
