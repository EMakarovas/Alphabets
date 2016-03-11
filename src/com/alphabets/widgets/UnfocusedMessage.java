package com.alphabets.widgets;

import com.alphabets.R;
import com.alphabets.view.CustomTextView;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

public class UnfocusedMessage extends RelativeLayout {
	
	private String msg;
	
	public UnfocusedMessage(Context context, String message) {
		
		super(context);
		View.inflate(context, R.layout.unfocused_message, this);
		this.msg = message;
		
		CustomTextView tv = (CustomTextView) this.findViewById(R.id.message);
		tv.setText(message);
		
	}
	
	public String getMessage() {
		return msg;
	}

}
