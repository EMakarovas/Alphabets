package com.alphabetlift.widgets.raw;

import com.alphabetlift.constants.Action;
import com.alphabetlift.progress.ProgressData;
import com.alphabetlift.progress.ProgressDataFactory;

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
		
		ProgressData data = ProgressDataFactory.getProgressData();
		return data.isBlockCompleted(position);
		
	}
	
	public void setCompleted(boolean mode) {
		
		ProgressData data = ProgressDataFactory.getProgressData();
		data.setBlockCompleted(position);
		
	}
	
	public abstract void setActive(Action action);
	
	public abstract void completeBlock();

}
