package com.alphabets.view;

import com.alphabets.progress.ProgressData;
import com.alphabets.widgets.BlockCompletedListener;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class CustomProgressBar extends ProgressBar implements BlockCompletedListener {
	
	public CustomProgressBar(Context context) {
		
		super(context);
		onBlockCompleted();
		
	}
	
	public CustomProgressBar(Context context, AttributeSet attrs) {
		
		super(context, attrs);
		onBlockCompleted();
		
	}
	
	public CustomProgressBar(Context context, AttributeSet attrs, int defStyle) {
		
		super(context, attrs, defStyle);
		onBlockCompleted();
		
	}

	@Override
	public void onBlockCompleted() {
		
		ProgressData data = new ProgressData(this.getContext());
		this.setMax(data.getTotalNumberOfBlocks());
		this.setProgress(data.getProgress());
		
	}
	
}
