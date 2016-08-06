package com.alphabetlift.progress;

import android.content.Context;

public class ProgressDataFactory {
	
	private static ProgressData progressData;
	
	public static ProgressData getProgressData() {
		return progressData;
	}
	
	public static void initializeProgressData(Context context) {
		progressData = new ProgressData(context);
	}

}
