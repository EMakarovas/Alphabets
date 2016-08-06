package com.alphabetlift.widgets;

import com.alphabetlift.MainActivity;
import com.alphabetlift.R;
import com.alphabetlift.constants.Action;
import com.alphabetlift.constants.DoorEvent;
import com.alphabetlift.constants.ScaleType;
import com.alphabetlift.constants.Scroll;
import com.alphabetlift.data.DataLoader;
import com.alphabetlift.functional.Keyboard;
import com.alphabetlift.view.CustomTextView;
import com.alphabetlift.widgets.raw.VisualBlock;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;

@SuppressLint("NewApi")
public class Message extends VisualBlock implements DudeButtonClickListener {

	CustomTextView tv;
	private BlockCompletedListener listener;
	
	@SuppressWarnings("deprecation")
	@SuppressLint({ "ClickableViewAccessibility", "ResourceAsColor" })
	public Message(Context context, int position, String message) {
		
		super(context, position);
		View.inflate(context, R.layout.message, this);
		
		listener = (MainActivity) context;
				
		tv = (CustomTextView) this.findViewById(R.id.message);
		tv.setText(message);
		this.setOnTouchListener(touchListener);
		
		if(!isCompleted()) {
			performAnimation(0, 0, ScaleType.SHRINK);
			if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
				tv.setTextColor(getContext().getResources().getColor(R.color.comment_not_finished_text, null));
			else tv.setTextColor(getContext().getResources().getColor(R.color.comment_not_finished_text));
			
		}
		
	}
	
	private OnTouchListener touchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
						
			v.performClick();
			
			//int action = event.getAction();
						
			/* Legacy method, not needed anymore */
//			if(action==MotionEvent.ACTION_DOWN && getIsCurrent())
//				completeBlock();
			
			return false;
			
		}

	};
	
	@Override
	public void setActive(Action action) {
		
		setIsCurrent(true);
		performAnimation(500, 2000, ScaleType.GROW);
		Keyboard.hide();

		if(action!=Action.SKIP_ANIMATION) {
			DoorEvent doorEvent = getPosition()+1==DataLoader.getTotalBlocks() ? DoorEvent.END_GAME : DoorEvent.NONE;
			((MainActivity) this.getContext()).registerDudeButtonClickListener(this);
			((MainActivity) this.getContext()).showDoorAnimation(tv.getText().toString(), doorEvent);
		}	
	}
	
	@Override
	public void completeBlock() {

		if(!getIsCurrent())
			return;
		
		setCompleted(true);
		setIsCurrent(false);
		listener.onBlockCompleted(Scroll.INSTANT);
		
	}
	
	/*
	 * 
	 * Animations
	 * 
	 */
	
	protected void performAnimation(final int duration, final int offset, ScaleType scaleType) {
		
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
						
							@SuppressWarnings("deprecation")
							@Override
							public void run() {
								
								tv.setScaleX(endScale);
								tv.setScaleY(endScale);
								if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
									tv.setTextColor(getContext().getResources().getColor(R.color.white, null));
								else tv.setTextColor(getContext().getResources().getColor(R.color.white));
								
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

	@Override
	public void onDudeButtonClick() {
		completeBlock();		
	}
	
	public String getText() {
		return tv.getText().toString();
	}
		
}