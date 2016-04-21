package com.alphabets.view;

import com.alphabets.progress.ProgressData;
import com.alphabets.widgets.MistakeListener;

import android.content.Context;
import android.util.AttributeSet;

public class MistakeCounter extends CustomTextView implements MistakeListener {
		
	public MistakeCounter(Context context) {
		
		super(context);
		this.setText("" + new ProgressData(context).getMistakesCommitted());
		
	}
	
	public MistakeCounter(Context context, AttributeSet attrs) {
		
		super(context, attrs);
		this.setText("" + new ProgressData(context).getMistakesCommitted());
		
	}
	
	public MistakeCounter(Context context, AttributeSet attrs, int defStyle) {
		
		super(context, attrs, defStyle);
		this.setText("" + new ProgressData(context).getMistakesCommitted());
		
	}

	@Override
	public void mistakeCommitted() {
		this.setText("" + new ProgressData(this.getContext()).getMistakesCommitted());	
	}

}
