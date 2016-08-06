package com.alphabetlift.widgets.raw;

import android.content.Context;

public abstract class WordGroup extends VisualBlock {
	
	private OrgWord orgWord;
	private NewWord newWord;
	private String translation;
	
	public WordGroup(Context context, int position, String orgWord, String newWord, String translation) {
		
		super(context, position);
		
		this.orgWord = new OrgWord(orgWord);
		this.newWord = new NewWord(newWord);
		this.translation = translation;
		
	}
	
	public OrgWord getOrgWord() {
		return orgWord;
	}
	
	public NewWord getNewWord() {
		return newWord;
	}
	
	public String getTranslation() {
		return translation;
	}

}
