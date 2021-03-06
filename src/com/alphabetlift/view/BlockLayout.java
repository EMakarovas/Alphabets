package com.alphabetlift.view;

import com.alphabetlift.widgets.raw.VisualBlock;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class BlockLayout extends LinearLayout {
	
	public BlockLayout(Context context) {
		super(context);
	}
	
	public BlockLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public BlockLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	/*
	 * 
	 * items are added to UI through UI thread
	 * therefore, synchronized doesn't work
	 * to make sure children are placed where they should be
	 * this method was overridden
	 * 
	 */
	
	@Override
	public void addView(View child, int index) {
		
		super.addView(child, index);
		
		if(!(child instanceof VisualBlock))
			return;
		else {
			
			VisualBlock block = (VisualBlock) child;
			
			int position = block.getPosition();
			
			boolean isInPlace = false;
			boolean upperCorrect = false, lowerCorrect = false;

			int currentIndex = index;
			
			while(!isInPlace) {
				
				if(!upperCorrect) {
									
					boolean canGoHigher = currentIndex!=0;
					
					if(canGoHigher) {
						
						int upperPos = ((VisualBlock) this.getChildAt(currentIndex-1)).getPosition();
						if(upperPos>position)
							upperCorrect = true;
						else {
							
							this.removeViewAt(currentIndex);
							super.addView(child, --currentIndex);
						
						}
						
					} else upperCorrect = true;
				
				}
				
				if(!lowerCorrect) {
					
					View view = this.getChildAt(currentIndex+1);
					
					boolean canGoLower = view instanceof VisualBlock;
					
					if(canGoLower) {
						
						int lowerPos = ((VisualBlock) view).getPosition();
						if(lowerPos<position)
							lowerCorrect = true;
						else {
							
							this.removeViewAt(currentIndex);
							super.addView(child, ++currentIndex);
						
						}
						
					} else lowerCorrect = true;
					
				}
				
				if(upperCorrect && lowerCorrect)
					isInPlace = true;
				
			}
						
		}			
		
	}

}
