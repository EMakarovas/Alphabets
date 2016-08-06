package com.alphabetlift.data;

import com.alphabetlift.widgets.raw.VisualBlock;

public interface DataReadyListener {
	
	void blockIsReady(VisualBlock block);
	
	void loadComplete();
		
}
