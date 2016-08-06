package com.alphabetlift.view;

import android.R;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.TextView;

public class CustomTextView extends TextView {

	public CustomTextView(Context context) {
		
		super(context);
		this.setTextStyle(R.color.white);
		
	}
	
	public CustomTextView(Context context, AttributeSet attrs) {
		
		super(context, attrs);
		this.setTextStyle(R.color.white);
		
	}
	
	public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
		
		super(context, attrs, defStyle);
		this.setTextStyle(R.color.white);
		
	}
	
	@SuppressWarnings("deprecation")
	public void setTextStyle(int resId) {
		
		Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "OpenSans-Regular.ttf");
		setTypeface(tf);
		if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M)
			setTextColor(getResources().getColor(resId));
		else ContextCompat.getColor(getContext(), resId);
		
	}

}
