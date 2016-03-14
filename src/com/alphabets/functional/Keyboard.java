package com.alphabets.functional;

import com.alphabets.MainActivity;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class Keyboard {
	
	private static MainActivity actRef;
	
	public static void initialize(Context context) {
		actRef = (MainActivity) context;
	}
	
	public static void show() {
		
		InputMethodManager inputMethodManager = (InputMethodManager) actRef.getSystemService(Activity.INPUT_METHOD_SERVICE);
	    if (inputMethodManager != null)
	        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
		
	}
	
	public static void hide() {
		
	    InputMethodManager imm = (InputMethodManager) actRef.getSystemService(Activity.INPUT_METHOD_SERVICE);
	    View view = actRef.getCurrentFocus();
	    if (view == null) {
	        view = new View(actRef);
	    }
	    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	    
	}

}
