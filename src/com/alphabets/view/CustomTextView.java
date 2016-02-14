package com.alphabets.view;

import com.alphabets.R;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.TextView;

public class CustomTextView extends TextView {

	public CustomTextView(Context context) {
		
		super(context);
		this.setTextStyle();
		
	}
	
	public CustomTextView(Context context, AttributeSet attrs) {
		
		super(context, attrs);
		this.setTextStyle();
		
	}
	
	public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
		
		super(context, attrs, defStyle);
		this.setTextStyle();
		
	}
	
	@SuppressWarnings("deprecation")
	private void setTextStyle() {
		
		Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "OpenSans-Regular.ttf");
		setTypeface(tf);
		if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M)
			setTextColor(getResources().getColor(R.color.black));
		else ContextCompat.getColor(getContext(), R.color.black);
		
	}

}
