package com.alphabets.widgets;

import com.alphabets.R;
import com.alphabets.view.CustomTextView;

import android.content.Context;
import android.text.Spanned;
import android.view.View;
import android.widget.LinearLayout;

public class UnfocusedWordBlock extends LinearLayout {
	
	private CustomTextView orgW, newW;
	private boolean newHidden;

	public UnfocusedWordBlock(Context context, Spanned originalWord) {
		
		super(context);
		View.inflate(context, R.layout.unfocused_word, this);
				
		orgW = (CustomTextView) this.findViewById(R.id.org_word);
		orgW.setText(originalWord);
		
		newW = (CustomTextView) this.findViewById(R.id.new_word);
		
		newHidden = true;
		
	}
	
	public void setNewHidden(boolean hide) {
		newHidden = hide;
	}
	
	public boolean getNewHidden() {
		return newHidden;
	}
	
	public void setOrgText(Spanned text) {
		orgW.setText(text);
	}
	
	public void setNewText(Spanned text) {
		if(!newHidden)
			newW.setText(text);
	}

}
