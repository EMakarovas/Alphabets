package com.alphabetlift.progress;

import java.util.ArrayList;
import java.util.List;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import android.annotation.SuppressLint;

public class FirebaseData extends Firebase {
	
	private static final String FIREBASE = "https://alphabet-ladder.firebaseio.com/";
	private final String MISTAKES_TOTAL = "mistakes";
	private final String HELP_TOTAL = "help";
	
	private boolean gotMistakes;
	private boolean gotHelp;
	private boolean mistakesRequested;
	private boolean helpRequested;
	
	private List<Long> mistakes;
	private List<Long> help;
	
	public FirebaseData() {
		super(FIREBASE);
		mistakes = new ArrayList<Long>();
		help = new ArrayList<Long>();
	}
	
	public void getMistakes() {

		if(mistakesRequested)
			return;
		mistakesRequested = true;
		
		this.child(MISTAKES_TOTAL).addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onCancelled(FirebaseError arg0) {}

			@Override
			public void onDataChange(DataSnapshot data) {

				if(!data.exists())
					return;
				for(DataSnapshot snapshot : data.getChildren()) {
					mistakes.add((Long) snapshot.getValue());
				}
				gotMistakes = true;				
			}			
		});
			
	}
	
	public void getHelp() {
		
		if(helpRequested)
			return;
		helpRequested = true;
		
		this.child(HELP_TOTAL).addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onCancelled(FirebaseError arg0) {}

			@Override
			public void onDataChange(DataSnapshot data) {
				if(!data.exists())
					return;
				for(DataSnapshot snapshot : data.getChildren())
					help.add((Long) snapshot.getValue());
				gotHelp = true;				
			}			
		});
			
	}
	
	public boolean isMistakesReady() {
		return gotMistakes;
	}
	
	public boolean isHelpReady() {
		return gotHelp;
	}
	
	private boolean mistakesSent;
	private boolean helpSent;
	
	public void setMistakes(int mistakes) {
		if(mistakesSent)
			return;
		mistakesSent = true;
		this.child(MISTAKES_TOTAL).push().setValue(mistakes);
	}
	
	public void setHelp(int help) {
		if(helpSent)
			return;
		helpSent = true;
		this.child(HELP_TOTAL).push().setValue(help);
	}
	
	public String compareMistakes(int userMistakes) {
		
		double betterThan = 0;
		
		for(Long mistake : mistakes)
			if(userMistakes<=mistake)
				betterThan++;
		
		return format(round(betterThan*100/mistakes.size()));	
		
	}
	
	public String compareHelp(int userHelp) {
		
		double betterThan = 0;
		
		for(Long helpInstance : help)
			if(userHelp<=helpInstance)
				betterThan++;
		
		return format(round(betterThan*100/help.size()));	
		
	}
	
	@SuppressLint("DefaultLocale")
	private String format(double d) {
	    if(d == (long) d)
	        return String.format("%d",(long)d);
	    else
	        return String.format("%s",d);
	}
	
	private double round(double value) {
	    long factor = (long) Math.pow(10, 2);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}

}
