package com.alphabetlift.widgets;

import com.alphabetlift.MainActivity;
import com.alphabetlift.constants.Action;
import com.alphabetlift.constants.DoorEvent;
import com.alphabetlift.constants.ScaleType;
import com.alphabetlift.functional.Keyboard;
import com.alphabetlift.progress.ProgressDataFactory;

import android.content.Context;

public class BuyMessage extends Message {

	public BuyMessage(Context context, int position, String message) {
		super(context, position, message);
	}
	
	@Override
	public void setActive(Action action) {
		
		setIsCurrent(true);
		super.performAnimation(500, 2000, ScaleType.GROW);
		Keyboard.hide();
		if(!ProgressDataFactory.getProgressData().hasLicense()) {
			((MainActivity) this.getContext()).registerDudeButtonClickListener(this);
			((MainActivity) this.getContext()).showDoorAnimation(tv.getText().toString(), DoorEvent.BUY_GAME);
		} else super.setActive(action);		
		
	}

}
