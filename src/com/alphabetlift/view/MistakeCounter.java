package com.alphabetlift.view;

import com.alphabetlift.progress.ProgressData;
import com.alphabetlift.progress.ProgressDataFactory;
import com.alphabetlift.widgets.MistakeListener;

import android.content.Context;
import android.util.AttributeSet;

public class MistakeCounter extends CustomTextView implements MistakeListener {
	
	private ProgressData progressData;
		
	public MistakeCounter(Context context) {
		
		super(context);
		progressData = ProgressDataFactory.getProgressData();
		this.setText("" + progressData.getMistakesCommitted());
		
	}
	
	public MistakeCounter(Context context, AttributeSet attrs) {
		
		super(context, attrs);
		progressData = ProgressDataFactory.getProgressData();
		this.setText("" + progressData.getMistakesCommitted());
		
	}
	
	public MistakeCounter(Context context, AttributeSet attrs, int defStyle) {
		
		super(context, attrs, defStyle);
		progressData = ProgressDataFactory.getProgressData();
		this.setText("" + progressData.getMistakesCommitted());
		
	}

	@Override
	public void mistakeCommitted() {
		this.setText("" + progressData.getMistakesCommitted());	
	}

}
