package com.alphabets.data;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.alphabets.MainActivity;
import com.alphabets.functional.ErrorDialog;
import com.alphabets.progress.ProgressData;
import com.alphabets.widgets.Message;
import com.alphabets.widgets.WordBlock;

import android.content.Context;
import android.os.AsyncTask;

public class DataLoader extends AsyncTask<Integer, Object, Object> {
	
	private Message[] message;
	private WordBlock[] wordBlock;
	private DataReader dataReader;
	private Context context;
	private DataReadyListener readyListener;
	
	public DataLoader(Context context) {
		
		this.context = context;
		readyListener = (MainActivity) context;
		
	}
	
	public Message getMessage(int index) {
		return message[index];
	}
	
	public WordBlock getWordBlock(int index) {
		return wordBlock[index];
	}
	
	private String[] orgWords, newWords, translation, msg;
	private Map<Integer, String[]> positionMap;

	@Override
	protected Object doInBackground(Integer... params) {
				
		// retrieve game data
		try {
			dataReader = new DataReader(context);
		} catch(IOException e) {
			e.printStackTrace();
			ErrorDialog.show(context);
		}
				
		// retrieve raw data
		orgWords = dataReader.getOrgWords();
		newWords = dataReader.getNewWords();
		translation = dataReader.getTranslations();
		msg = dataReader.getMessages();
		positionMap = dataReader.getPositionMap();
		int currentBlock = params[0];
		
		// init progress data
		ProgressData data = new ProgressData(this.context);
		if(!data.numberOfBlocksWasSet())
			data.setTotalNumberOfBlocks(orgWords.length);
				
		// initialize arrays
		wordBlock = new WordBlock[orgWords.length];
		message = new Message[msg.length];

		int goingDown = currentBlock-1;
		int goingUp = currentBlock;
		
		while(goingUp<positionMap.size() || goingDown>=0) {
			
			try {
				TimeUnit.MILLISECONDS.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
				//ignore
			}
						
			if(goingUp<positionMap.size())
				loadBlock(goingUp++);
						
			if(goingDown>=0) 
				loadBlock(goingDown--);
			
		}
		
		readyListener.loadComplete();
		
		return null;
		
	}
	
	private void loadBlock(final int index) {
					
		String[] object = positionMap.get(index);
		String type = object[0];
		final int number = Integer.parseInt(object[1]);
			
		// if is message
		if(type.equals("m")) {
				
			message[number] = new Message(context, index, msg[number]);
			readyListener.blockIsReady(message[number]);
								
		} 
		// else if is wordblock
		else if(type.equals("w")) {
				
			wordBlock[number] = new WordBlock(context, index, orgWords[number],
					newWords[number], translation[number], number);
			readyListener.blockIsReady(wordBlock[number]);
			
		}
		
	}

}
