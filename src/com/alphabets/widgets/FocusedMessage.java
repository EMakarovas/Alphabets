package com.alphabets.widgets;

import com.alphabets.MainActivity;
import com.alphabets.R;
import com.alphabets.view.CustomTextView;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

public class FocusedMessage extends LinearLayout {

	private String msg;
	private CustomTextView tv;
	private MainActivity act;
	
	@SuppressLint("ClickableViewAccessibility")
	public FocusedMessage(MainActivity activity, String message) {
		
		super(activity);
		View.inflate(activity, R.layout.focused_message, this);
		act = activity;
		
		msg = message;
		
		tv = (CustomTextView) this.findViewById(R.id.message);
		tv.setText(msg);
		this.setOnTouchListener(touchListener);
		
	}
	
	public String getMessage() {
		return msg;
	}
	
	private OnTouchListener touchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			v.performClick();
			
			int action = event.getAction();
			
			if(action==MotionEvent.ACTION_DOWN)
				act.finishBlock();
			
			return false;
			
		}

	};
	
}
