package com.alphabetlift.functional;

import com.alphabetlift.MainActivity;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

public class Keyboard {
	
	private static MainActivity actRef;
	private static boolean keyboardShowing;
	private static InputMethodManager imm;
	
	public static void initialize(Context context) {
		actRef = (MainActivity) context;
		imm = (InputMethodManager) actRef.getSystemService(Activity.INPUT_METHOD_SERVICE);
	}
	
	public static void show() {		
		if(!keyboardShowing)
			imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);		
	}
	
	public static void hide() {
		if(keyboardShowing)
			imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);	    
	}
	
	public static void setKeyboardShowing(boolean showing) {
		keyboardShowing = showing;
	}

}
