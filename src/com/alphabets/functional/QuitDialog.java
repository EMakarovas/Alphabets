package com.alphabets.functional;

import com.alphabets.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class QuitDialog {
	
	public static void showQuitDialog(final Activity activity) {
		
		new AlertDialog.Builder(activity)
	    .setTitle(R.string.quit_title)
	    .setMessage(R.string.quit_msg)
	    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	     //       activity.finish();
	            System.exit(0);
	        }
	     })
	    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // do nothing
	        }
	     })
	    .setIcon(android.R.drawable.ic_dialog_alert)
	     .show();
		
	}

}
