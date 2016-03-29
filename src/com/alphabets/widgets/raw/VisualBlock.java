package com.alphabets.widgets.raw;

import com.alphabets.progress.ProgressData;

import android.content.Context;
import android.widget.LinearLayout;

public abstract class VisualBlock extends LinearLayout {
	
	private int position;
	private boolean isCurrent;
	
	public VisualBlock(Context context, int position) {
		
		super(context);
		
		this.position = position;
		
	}
	
	public int getPosition() {
		return position;
	}
	
	public void setIsCurrent(boolean mode) {
		isCurrent = mode;
	}
	
	public boolean getIsCurrent() {
		return isCurrent;
	}
	
	public boolean isCompleted() {
		
		ProgressData data = new ProgressData(this.getContext());
		return data.isBlockCompleted(position);
		
	}
	
	public void setCompleted(boolean mode) {
		
		ProgressData data = new ProgressData(this.getContext());
		data.setBlockCompleted(position);
		
	}
	
	public abstract void setActive();
	
	public abstract void completeBlock();

}
