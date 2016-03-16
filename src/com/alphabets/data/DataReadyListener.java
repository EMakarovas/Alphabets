package com.alphabets.data;

import com.alphabets.widgets.raw.VisualBlock;

public interface DataReadyListener {
	
	void blockIsReady(VisualBlock block);
	
	void loadComplete();
	
}
