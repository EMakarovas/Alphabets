package com.alphabetlift.view;

import java.util.ArrayList;
import java.util.List;

import com.alphabetlift.MainActivity;
import com.alphabetlift.widgets.HelpListener;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class HelpButton extends Button implements OnClickListener {
	
	private List<HelpListener> helpListenerList = new ArrayList<HelpListener>();
	
	public HelpButton(Context context) {
		super(context);
		this.setOnClickListener(this);
	}
	
	public HelpButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setOnClickListener(this);
	}
	
	public HelpButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.setOnClickListener(this);
	}
	
	public void addHelpListener(HelpListener listener) {
		helpListenerList.add(listener);
	}
	
	@Override
	public void onClick(View arg0) {

		if(((MainActivity) this.getContext()).isAnimationActive())
			return;
		
		for(int i=0; i<helpListenerList.size(); i++)
			helpListenerList.get(i).helpUsed();
		
	}
	
}
