package com.alphabets.widgets;

import com.alphabets.MainActivity;
import com.alphabets.R;
import com.alphabets.view.CustomTextView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

public class Message extends LinearLayout {

	private CustomTextView tv;
	
	@SuppressLint("ClickableViewAccessibility")
	public Message(Context context, String message) {
		
		super(context);
		View.inflate(context, R.layout.message, this);
				
		tv = (CustomTextView) this.findViewById(R.id.message);
		tv.setText(message);
		this.setOnTouchListener(touchListener);
		
	}
	
	private OnTouchListener touchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			v.performClick();
			
			int action = event.getAction();
			
			if(action==MotionEvent.ACTION_DOWN)
				((MainActivity) v.getContext()).finishBlock();
			
			return false;
			
		}

	};
	
}
