package com.alphabetlift.view;

import java.util.concurrent.TimeUnit;

import com.alphabetlift.MainActivity;
import com.alphabetlift.R;
import com.alphabetlift.constants.Scroll;
import com.alphabetlift.progress.ProgressData;
import com.alphabetlift.progress.ProgressDataFactory;
import com.alphabetlift.widgets.BlockCompletedListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class ProgressArrow extends ImageView implements BlockCompletedListener {
	
	private float pivotX;
	private float pivotY;
	private int height;
	private ProgressData progressData;
	
	public ProgressArrow(Context context) {
		
		super(context);
		progressData = ProgressDataFactory.getProgressData();
		setPivot();
		
	}
	
	public ProgressArrow(Context context, AttributeSet attrs) {
		
		super(context, attrs);
		progressData = ProgressDataFactory.getProgressData();
		setPivot();
		
	}
	
	public ProgressArrow(Context context, AttributeSet attrs, int defStyle) {
		
		super(context, attrs, defStyle);
		progressData = ProgressDataFactory.getProgressData();
		setPivot();
		
	}
	
	private void setPivot() {
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					TimeUnit.MILLISECONDS.sleep(20);
				} catch (InterruptedException e) {
					setPivot();
				}
				
				View parent = (View) getParent();
				if(parent==null) {
					setPivot();
					return;
				}
				
				height = getHeight();
				int width = getWidth();
				int parentHeight = parent.getHeight();
				int parentWidth = parent.getWidth();
				if(height==0 || width==0 || parentHeight==0 || parentWidth==0) {
					setPivot();
					return;
				}
				pivotX = parentWidth/2;
				pivotY = parentHeight-height*3/20; // *3/20 is the ratio of the arrow bubble
				onBlockCompleted(Scroll.NORMAL);
				
			}
		}).start();
	
	}

	@Override
	public void onBlockCompleted(Scroll scroll) {
		
		if(pivotX==0 || pivotY==0)
			return;
		
		final float degrees = ((float)progressData.getProgress()/(float)progressData.getTotalNumberOfBlocks())*180-90;
		final Matrix matrix = new Matrix();
		matrix.postRotate(degrees, pivotX, pivotY);
		Bitmap bitmap = BitmapFactory.decodeResource(this.getContext().getResources(), R.drawable.arrow);
		final Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		((MainActivity) this.getContext()).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				setImageBitmap(rotatedBitmap);
				int newWidth = rotatedBitmap.getWidth();
				int newHeight = rotatedBitmap.getHeight();
				double radians = Math.toRadians(Math.abs(degrees));
				float opposite = (float) Math.sin(radians)*7*(float)height/20;
				float adjacent = (float) Math.cos(radians)*7*(float)height/20;
				if(degrees<0)
					opposite = -opposite;
				setX((float)pivotX+opposite-(float)newWidth/2);				
				setY((float)pivotY-adjacent-(float)newHeight/2);
			}
		});
		
	}
	
}
