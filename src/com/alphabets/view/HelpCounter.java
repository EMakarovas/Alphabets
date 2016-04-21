package com.alphabets.view;

import com.alphabets.progress.ProgressData;
import com.alphabets.widgets.HelpListener;

import android.content.Context;
import android.util.AttributeSet;

public class HelpCounter extends CustomTextView implements HelpListener {
	
	public HelpCounter(Context context) {
		
		super(context);
		this.setText("" + (new ProgressData(context)).getHelpCount());
		
	}
	
	public HelpCounter(Context context, AttributeSet attrs) {
		
		super(context, attrs);
		this.setText("" + (new ProgressData(context)).getHelpCount());
		
	}
	
	public HelpCounter(Context context, AttributeSet attrs, int defStyle) {
		
		super(context, attrs, defStyle);
		this.setText("" + (new ProgressData(context)).getHelpCount());
		
	}

	@Override
	public void helpUsed() {
		this.setText("" + (new ProgressData(this.getContext())).getHelpCount());		
	}
	
	@Override
	public void helpRefreshed() {
		this.setText("" + (new ProgressData(this.getContext())).getHelpCount());
	}

}
