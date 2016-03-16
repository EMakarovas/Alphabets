package com.alphabets.functional;

import com.alphabets.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class ErrorDialog {
	
	public static void show(Context context) {
		
		new AlertDialog.Builder(context)
	    .setTitle(R.string.error_title)
	    .setMessage(R.string.error_msg)
	    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            System.exit(0);
	        }
	     })
	    .setIcon(android.R.drawable.ic_dialog_alert)
	    .show();
		
	}

}
