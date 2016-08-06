package com.alphabetlift.functional;

import com.alphabetlift.R;
import com.alphabetlift.constants.ErrorType;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class ErrorDialog {
	
	public static void show(Context context, final ErrorType errorType) {
		
		 String message;
		 switch(errorType) {
		 case PURCHASE:
			 message = context.getResources().getString(R.string.purchase_error);
			 break;
		 case LOAD:
			 message = context.getResources().getString(R.string.error_msg);
			 break;
		 default: message = "Something went wrong!";
		 }
		 
		new AlertDialog.Builder(context)
	    .setTitle(R.string.error_title)
	    .setMessage(message)
	    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	        	if(errorType==ErrorType.LOAD)
	        		System.exit(0);
	        	else if(errorType==ErrorType.PURCHASE)
	        		dialog.dismiss();
	        }
	     })
	    .setIcon(android.R.drawable.ic_dialog_alert)
	    .show();
		
	}

}
