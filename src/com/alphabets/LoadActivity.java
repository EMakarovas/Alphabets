package com.alphabets;

import com.alphabets.data.Loader;
import com.alphabets.data.ReadyListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class LoadActivity extends Activity implements ReadyListener {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.load_activity);
		Loader.setListener(this);
		
	}

	@Override
	public void dataIsReady() {
		
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		
	}

}
