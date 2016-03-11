package com.alphabets;

import com.alphabets.game.Data;
import com.alphabets.view.CustomTextView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.RelativeLayout;

public class StartActivity extends Activity {
	
	private static final int FIRST = 0;
	private static final int SECOND = 1;
	private static final int THIRD = 2;
	private static final int FINISH = 3;
	
	private CustomTextView tv;
	private RelativeLayout main;
	private StartActivity act;
	private int currentMsg = 0;
	
	@SuppressWarnings("unused")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		Data.initialiseData(this);
		
		if(true) {
			
			this.goToMainActivity();
			
		} else {
			
			act = this;
			setContentView(R.layout.start_activity);
			main = (RelativeLayout) findViewById(R.id.start_main);
			tv = (CustomTextView) findViewById(R.id.start_center);
			this.fadeIn(FIRST);
			
			
		}

		
	}
	
	private void fadeIn(int count) {
		
		AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
		anim.setDuration(1500);
		anim.setStartOffset(0);
		anim.setFillAfter(true);
		anim.setAnimationListener(animSecondListener);
		if(currentMsg!=3)
			tv.startAnimation(anim);
		
		switch (count) {
		
			case FIRST: 
				tv.setText(R.string.intro_first);
				break;
			case SECOND:
				tv.setText(R.string.intro_second);
				break;
			case THIRD:
				tv.setText(R.string.intro_third);
				break;
			case FINISH:
				this.goToMainActivity();
				break;
			default: this.goToMainActivity();
		
		}
		
	}
	
	private void fadeOut() {
		
		currentMsg++;
		AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
		anim.setDuration(1000);
		anim.setStartOffset(100);
		anim.setFillAfter(true);
		tv.startAnimation(anim);
		anim.setAnimationListener(animFirstListener);
		
	}
	
	private void goToMainActivity() {
		
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		
	}
	
	@SuppressLint("ClickableViewAccessibility")
	private OnTouchListener disableActions = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			return true; // consume all events
		}
		
	};
	
	@SuppressLint("ClickableViewAccessibility")
	private OnTouchListener enableActions = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			act.fadeOut();
			return v.onTouchEvent(event);
		}
		
	};
	
	private AnimationListener animFirstListener = new AnimationListener() {

		@Override
		public void onAnimationEnd(Animation animation) {
			act.fadeIn(act.currentMsg);
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			
		}

		@Override
		public void onAnimationStart(Animation animation) {
			main.setOnTouchListener(disableActions);
		}
		
	};
	
	private AnimationListener animSecondListener = new AnimationListener() {

		@Override
		public void onAnimationEnd(Animation animation) {
			main.setOnTouchListener(enableActions);
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			
		}

		@Override
		public void onAnimationStart(Animation animation) {
		}	
		
	};
	
	@Override
	public void onBackPressed() {
		
		QuitDialog.showQuitDialog(this);
		
	}

}
