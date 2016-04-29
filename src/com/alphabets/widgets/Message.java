package com.alphabets.widgets;

import com.alphabets.MainActivity;
import com.alphabets.R;
import com.alphabets.view.CustomTextView;
import com.alphabets.widgets.raw.VisualBlock;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;

public class Message extends VisualBlock {

	private CustomTextView tv;
	private BlockCompletedListener listener;
	
	@SuppressLint("ClickableViewAccessibility")
	public Message(Context context, int position, String message) {
		
		super(context, position);
		View.inflate(context, R.layout.message, this);
		
		listener = (MainActivity) context;
				
		tv = (CustomTextView) this.findViewById(R.id.message);
		tv.setText(message);
		this.setOnTouchListener(touchListener);
		
		if(!isCompleted())
			performAnimation(0, 0, ScaleType.SHRINK);
		
	}
	
	private OnTouchListener touchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
						
			v.performClick();
			
			int action = event.getAction();
						
			if(action==MotionEvent.ACTION_DOWN && getIsCurrent())
				completeBlock();
			
			return false;
			
		}

	};
	
	@Override
	public void setActive() {
		
		setIsCurrent(true);
		performAnimation(1000, 500, ScaleType.GROW);
		
	}
	
	@Override
	public void completeBlock() {
		
		setCompleted(true);
		setIsCurrent(false);
		listener.onBlockCompleted();
		
	}
	
	/*
	 * 
	 * Animations
	 * 
	 */
	
	private void performAnimation(final int duration, final int offset, ScaleType scaleType) {
		
		final float scale = scaleType.equals(ScaleType.GROW) ? 2 : 0.5f;
		final float endScale = scaleType.equals(ScaleType.GROW) ? 1 : 0.5f;
		
		((MainActivity) this.getContext()).runOnUiThread(new Runnable() {
		
			@Override
			public void run() {
				
				final ScaleAnimation animation = new ScaleAnimation(1, scale,
															1, scale,
										Animation.RELATIVE_TO_SELF, 0.5f,
										Animation.RELATIVE_TO_SELF, 0.5f);
				
				animation.setDuration(duration);
				
				animation.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationEnd(Animation animation) {

						final Handler handler = new Handler();
						handler.postDelayed(new Runnable() {
						
							@Override
							public void run() {
								
								tv.setScaleX(endScale);
								tv.setScaleY(endScale);
								
							}
							
						}, 1);
						
					}

					@Override
					public void onAnimationRepeat(Animation animation) {

					}

					@Override
					public void onAnimationStart(Animation animation) {						
					}
					
				});
				
				final Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						
						tv.startAnimation(animation);
						
					}
					
				}, offset);
				
			}
		
		});
		
	}
	
	private enum ScaleType {
		GROW, SHRINK;
	}
		
}