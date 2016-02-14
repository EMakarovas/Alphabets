package com.alphabets.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;

@SuppressLint("NewApi")
public class Screen {
	
	@SuppressWarnings("deprecation")
	public static int getHeight(Context context) {
		
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB_MR2) {
		
			Point size = new Point();
			display.getSize(size);
			return size.y;
		
		} else return display.getHeight();
		
	}
	
	@SuppressWarnings("deprecation")
	public static int getWidth(Context context) {
		
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();

		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB_MR2) {
			
			Point size = new Point();
			display.getSize(size);
			return size.x;
		
		} else return display.getWidth();
		
	}
	
	public static boolean allowScrollDown(Context context, int startY) {
		
		int upperBound = getHeight(context);
		int lowerBound = (int) (upperBound*0.75);
		
		int relativeY = startY%1000;
		
		if(relativeY<upperBound && relativeY>lowerBound)
			return true;
		else return false;
		
	}
	
	public static boolean allowScrollUp(Context context, int startY) {
		
		int lowerBound = 0;
		int upperBound = (int) (getHeight(context)*0.25);

		int relativeY = startY%1000;
		
		if(relativeY<upperBound && relativeY>lowerBound)
			return true;
		else return false;
		
	}
	
	public static boolean isSwipingForNextLetter(Context context, int x) {
		
		int lowerBound = (int) (getWidth(context)*0.65);
		int upperBound = (int) getWidth(context);
		
		if(x<upperBound && x>lowerBound)
			return true;
		else return false;
		
	}
	
	public static boolean isSwipingForLevelMenu(Context context, int x) {
		
		int lowerBound = 0;
		int upperBound = (int) (getWidth(context)*0.35);
		
		if(x<upperBound && x>lowerBound)
			return true;
		else return false;
		
	}

}
