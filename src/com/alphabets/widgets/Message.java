package com.alphabets.widgets;

import com.alphabets.MainActivity;
import com.alphabets.R;
import com.alphabets.view.CustomTextView;
import com.alphabets.widgets.raw.VisualBlock;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

public class Message extends VisualBlock {

	private CustomTextView tv;
	private BlockCompletedListener listener;
	
	@SuppressLint("ClickableViewAccessibility")
	public Message(Context context, int position, String message) {
		
		super(context, position);
		View.inflate(context, R.layout.message, this);
		
		listener = (MainActivity) context;
				
		tv = (CustomTextView) this.findViewById(R.id.message);
		tv.setText(message);
		this.setOnTouchListener(touchListener);
		
	}
	
	private OnTouchListener touchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
						
			v.performClick();
			
			int action = event.getAction();
						
			if(action==MotionEvent.ACTION_DOWN && getIsCurrent())
				completeBlock();
			
			return false;
			
		}

	};
	
	@Override
	public void setActive() {
		
		setUnlocked(true);
		setIsCurrent(true);
		
	}
	
	@Override
	public void completeBlock() {
		
		setCompleted(true);
		setIsCurrent(false);
		listener.onBlockCompleted();
		
	}
		
}
