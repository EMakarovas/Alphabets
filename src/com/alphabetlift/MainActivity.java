package com.alphabetlift;

import java.util.concurrent.TimeUnit;

import com.alphabetlift.constants.Action;
import com.alphabetlift.constants.DoorEvent;
import com.alphabetlift.constants.Scroll;
import com.alphabetlift.data.DataLoader;
import com.alphabetlift.data.DataReadyListener;
import com.alphabetlift.functional.Keyboard;
import com.alphabetlift.functional.QuitDialog;
import com.alphabetlift.progress.FirebaseData;
import com.alphabetlift.progress.ProgressData;
import com.alphabetlift.progress.ProgressDataFactory;
import com.alphabetlift.progress.PurchaseManager;
import com.alphabetlift.view.BlockLayout;
import com.alphabetlift.view.CustomTextView;
import com.alphabetlift.view.HelpButton;
import com.alphabetlift.view.MistakeCounter;
import com.alphabetlift.view.ProgressArrow;
import com.alphabetlift.widgets.BlockCompletedListener;
import com.alphabetlift.widgets.DudeButtonClickListener;
import com.alphabetlift.widgets.Message;
import com.alphabetlift.widgets.raw.VisualBlock;
import com.firebase.client.Firebase;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

@SuppressLint("ClickableViewAccessibility")
public class MainActivity extends Activity implements DataReadyListener, BlockCompletedListener {
	
	// layouts
	private BlockLayout main;
	private RelativeLayout mainLayout;
	
	// doors
	private LinearLayout doorLeft;
	private LinearLayout doorRight;
	private ImageView dudeView;
	private CustomTextView bubbleView;
	private Button dudeButton;
	private HelpButton helpButton;
	
	// current item data
	private int currentBlock; // number of current item
	private int amountScrolled;
	private boolean loading;
	
	// items
	private ScrollView scroll;
	private ProgressArrow progArrow;
	private MistakeCounter mistakeCounter;
	
	// progress
	private ProgressData data;
	private FirebaseData firebaseData;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		// initialize keyboard and prog data functionality
		Keyboard.initialize(this);
		ProgressDataFactory.initializeProgressData(this);
		Firebase.setAndroidContext(this);
		firebaseData = new FirebaseData();
		
		// instantiate layout
		setContentView(R.layout.main_activity);
		mainLayout = (RelativeLayout) findViewById(R.id.main_layout);
		scroll = (ScrollView) findViewById(R.id.scroll);
		main = (BlockLayout) findViewById(R.id.main);
		doorLeft = (LinearLayout) findViewById(R.id.left_door);
		doorRight = (LinearLayout) findViewById(R.id.right_door);
		progArrow = (ProgressArrow) findViewById(R.id.progress_arrow);
		mistakeCounter = (MistakeCounter) findViewById(R.id.mistake_counter);
		dudeView = (ImageView) findViewById(R.id.dude_view);
		bubbleView = (CustomTextView) findViewById(R.id.bubble_view);
		dudeButton = (Button) findViewById(R.id.dude_button);
		helpButton = (HelpButton) findViewById(R.id.help_button);
		
		// for keyboard
		mainLayout.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
		    @Override
		    public void onGlobalLayout() {
		    	Rect r = new Rect();
		        mainLayout.getWindowVisibleDisplayFrame(r);
		        int screenHeight = mainLayout.getRootView().getHeight();
		        int keypadHeight = screenHeight - r.bottom;

		        if (keypadHeight > screenHeight * 0.15) {
		        	Keyboard.setKeyboardShowing(true);
		        }
		        else {
		            Keyboard.setKeyboardShowing(false);
		            if(doorWidth==0) {
		    			sortDudeView();
		    		}
		        }
		     }
		});
		
		// retrieve completion data
		currentBlock = 0;
		loading = true;
		data = ProgressDataFactory.getProgressData();
		data.setHasLicense();
		while(data.isBlockCompleted(currentBlock))
			currentBlock++;
				
		DataLoader loader = new DataLoader(this, helpButton);
		loader.execute(currentBlock);
				
		/*
		 * 
		 *  required to lift the screen up at the beginning
		 *  otherwise widgets stick to the bottom
		 *  
		 */
		RelativeLayout bottom = (RelativeLayout) findViewById(R.id.bottom_of_screen);
		main.removeView(bottom);
		main.addView(bottom);
		
		if(data.isGameFinished()) {
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {			
				@Override
				public void run() {
					startEndGameSequence();
				}
				
			}, 1000);
		}
		
	}
	
	/*
	 * 
	 *  Updates current item data
	 *  Scrolls to the next block
	 *  Unlocks the block if needed
	 * 
	 */
		
	private void setFocusedBlock(final Scroll scrollType) {
		
		if(currentBlock+15>DataLoader.getTotalBlocks()) {
			firebaseData.getMistakes();
			firebaseData.getHelp();
		}
					
		if(getCurrentBlock()<0) {
			startEndGameSequence();
			return;
		}
					
		// scroll down to the new focused block
		final int amountToScroll = -getBlockHeight();
		amountScrolled = 0;
				
		// disable actions, so the user doesn't mess up
		// the auto scroll
		main.setOnTouchListener(disableActions);
						
		Thread th = new Thread(new Runnable() {
					
			@Override
			public void run() {
						
				int delay = loading || scrollType==Scroll.INSTANT ? 0 : 3;
						
				while(amountScrolled>amountToScroll) {

					scroll.scrollBy(0, -1);
					amountScrolled--;
					try {
						TimeUnit.MILLISECONDS.sleep(delay);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
							
					if(amountScrolled==amountToScroll) {
								
						main.setOnTouchListener(enableActions);
						runOnUiThread(new Runnable() {
							@Override
							public void run() {	
								((VisualBlock) main.getChildAt(getCurrentBlock())).setActive(Action.NONE);
							}	
						});					
						
					}
							
				}
						
			}
					
		});
		th.start();
		
	}
	
	/*
	 * 
	 * Called upon completion of a word.
	 * 
	 */
		
	@Override
	public void onBlockCompleted(final Scroll scroll) {
		
		currentBlock++;
		
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				setFocusedBlock(scroll);
			}
			
		}, 300);
		
	}
	
	@Override
	public void onBackPressed() {
		
		QuitDialog.show(this);
		
	}
	
	@SuppressLint("ClickableViewAccessibility")
	private OnTouchListener disableActions = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			return true; // consume all events
		}
		
	};
	
	@SuppressLint("ClickableViewAccessibility")
	private OnTouchListener enableActions = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			return v.onTouchEvent(event);
		}
		
	};
	
	/*
	 * 
	 * Utility methods
	 * 
	 */
	
	private int getBlockHeight() {
		return main.getChildAt(1).getHeight();
	}
	
	private int getCurrentBlock() {		
		return main.getChildCount() - (currentBlock - lowestBlockPosition) - 2;	
	}
	
	/*
	 * 
	 * called whenever a block is loaded up in DataLoader
	 * 
	 */
	
	private boolean firstWasSet;
	
	@Override
	public void blockIsReady(final VisualBlock block) {
						
		// for first addition
		if(!firstWasSet) {
			
			firstWasSet = true;
			
			this.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {

					main.addView(block, 0);
					block.setActive(Action.NONE);
					lowestBlockPosition = block.getPosition();
					scrollToCurrentBlock();
					
				}
				
			});
			
			return;
			
		}
		
		final int index = block.getPosition();
		int indexToSet = main.getChildCount()-1;
		int highestPreviousPos = 0;
		
		for(int i=0; i<main.getChildCount(); i++) {
			
			View object = main.getChildAt(i);
			
			if(object instanceof VisualBlock) {
				
				int childPos = ((VisualBlock) object).getPosition();
				if(childPos<index && highestPreviousPos<=childPos) {
					
					indexToSet = i;
					highestPreviousPos = childPos;
				
				}
				
			} else continue;			
			
		}
		
		final int toSet = indexToSet;
		
		this.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				
				main.addView(block, toSet);
				if(index<lowestBlockPosition)
					lowestBlockPosition = index;
				scrollToCurrentBlock();
				
			}
			
		});
		
	}
	
	@Override
	public void loadComplete() {
		loading = false;
	}
	
	/*
	 * 
	 * Called when a new block is loaded up
	 * 
	 */
	
	private int lowestBlockPosition;
	
	private void scrollToCurrentBlock() {
		int addition = currentBlock!=0 ? 1 : 0;
		scroll.scrollTo(0, main.getHeight() - ((currentBlock-lowestBlockPosition+2+addition))*getBlockHeight());		
	}
	
	/*
	 * 
	 * Getter methods for items
	 * 
	 */
	
	public ProgressArrow getProgressArrow() {
		return progArrow;
	}
	
	public MistakeCounter getMistakeCounter() {
		return mistakeCounter;
	}
	
	/*
	 * Door animation
	 */
	
	private int doorWidth;
	private int screenWidth;
	private float scaleFactor;
	private boolean animationActive;
	private PurchaseManager purchase;
	private DudeButtonClickListener dudeButtonClickListener;
	
	public void showDoorAnimation(String message, DoorEvent doorEvent) {
		closeDoor(new SpannableString(message), doorEvent);
	}
	
	public void registerDudeButtonClickListener(DudeButtonClickListener dudeButtonClickListener) {
		this.dudeButtonClickListener = dudeButtonClickListener;
	}
	
	private void closeDoor(final Spanned message, DoorEvent doorEvent) {
		
		animationActive = true;
		
		if(doorWidth==0) { // called on first time if main view hasn't called it yet
			sortDudeView();
		}
		
		if(doorEvent==DoorEvent.END_GAME) {
			startEndGameSequence();
		} else if(doorEvent==DoorEvent.BUY_GAME) {
			
			dudeButton.setText("Sure!");
			dudeButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					purchase = new PurchaseManager(MainActivity.this);
					
				}
			
			});
		}
		
		final Animation dudeAnimation = new TranslateAnimation(
				0, -screenWidth/2, 0, 0);				
		final Animation rightAnimation = new ScaleAnimation(
				1, scaleFactor, 1, 1, doorWidth, 0);
		final Animation leftAnimation = new ScaleAnimation(
				1, scaleFactor, 1, 1, 0, 0);
		
		rightAnimation.setDuration(1500);
		leftAnimation.setDuration(1500);
		dudeAnimation.setDuration(1500);
		
		rightAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationEnd(Animation animation) {
				
				final Handler handler = new Handler();
				handler.postDelayed(new Runnable() {					
					@Override
					public void run() {		
						doorLeft.setScaleX(scaleFactor);
						doorRight.setScaleX(scaleFactor);
						dudeView.setX(screenWidth/2);
						showSpeechBubble(message);
					}				
				}, 1);			
			}

			@Override
			public void onAnimationRepeat(Animation animation) {				
			}

			@Override
			public void onAnimationStart(Animation animation) {
				doorLeft.setBackgroundResource(R.drawable.door_left_closed);
				doorRight.setBackgroundResource(R.drawable.door_right_closed);
				dudeView.setVisibility(View.VISIBLE);
			}
	
		});
		
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				doorLeft.startAnimation(leftAnimation);
				doorRight.startAnimation(rightAnimation);
				dudeView.startAnimation(dudeAnimation);
			}
			
		}, 200);
		
	}
	
	private void openDoor() {
				
		final Animation dudeAnimation = new TranslateAnimation(
				0, screenWidth/2, 0, 0);
		final Animation rightAnimation = new ScaleAnimation(
				1, 1/scaleFactor, 1, 1, doorWidth, 0);
		final Animation leftAnimation = new ScaleAnimation(
				1, 1/scaleFactor, 1, 1, 0, 0);
		
		rightAnimation.setDuration(1500);
		leftAnimation.setDuration(1500);
		dudeAnimation.setDuration(1500);
		
		rightAnimation.setAnimationListener(new AnimationListener() {
	
			@Override
			public void onAnimationEnd(Animation animation) {
				
				final Handler handler = new Handler();
				handler.postDelayed(new Runnable() {					
					@Override
					public void run() {		
						doorLeft.setBackgroundResource(R.drawable.door_opened);
						doorRight.setBackgroundResource(R.drawable.door_opened);
						doorLeft.setScaleX(1);
						doorRight.setScaleX(1);
						dudeView.setX(screenWidth);
						animationActive = false;
					}				
				}, 1);			
			}

			@Override
			public void onAnimationRepeat(Animation animation) {				
			}

			@Override
			public void onAnimationStart(Animation animation) {
				doorLeft.setBackgroundResource(R.drawable.door_left_closed);
				doorRight.setBackgroundResource(R.drawable.door_right_closed);
			}
	
		});
		
		doorLeft.startAnimation(leftAnimation);
		doorRight.startAnimation(rightAnimation);
		dudeView.startAnimation(dudeAnimation);
		
	}
	
	private void showSpeechBubble(final Spanned message) {
	
		Animation bubbleAnimation = new AlphaAnimation(0f, 1f);
		Animation buttonAnimation = new AlphaAnimation(0f, 1f);
		
		bubbleAnimation.setDuration(500);
		buttonAnimation.setDuration(500);
		
		bubbleAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationEnd(Animation animation) {}

			@Override
			public void onAnimationRepeat(Animation animation) {}

			@Override
			public void onAnimationStart(Animation animation) {
				bubbleView.setVisibility(View.VISIBLE);
				dudeButton.setVisibility(View.VISIBLE);
				if(!data.isGameFinished())
					bubbleView.setText(message);
			}
			
		});
		
		dudeButton.startAnimation(buttonAnimation);
		bubbleView.startAnimation(bubbleAnimation);
		
	}
	
	private void hideSpeechBubble() {
		
		if(dudeButtonClickListener!=null && (
				(main.getChildAt(getCurrentBlock()-1) instanceof Message) || getCurrentBlock()<0)) {
			goToNextMessage();
			return;
		}
		
		Animation bubbleAnimation = new AlphaAnimation(1.0f, 0.0f);
		Animation buttonAnimation = new AlphaAnimation(1.0f, 0.0f);
		
		bubbleAnimation.setDuration(300);
		buttonAnimation.setDuration(300);
		
		bubbleAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationEnd(Animation arg0) {
				bubbleView.setVisibility(View.INVISIBLE);
				dudeButton.setVisibility(View.INVISIBLE);
				openDoor();
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {}

			@Override
			public void onAnimationStart(Animation arg0) {
				
				if(dudeButtonClickListener!=null) {
					dudeButtonClickListener.onDudeButtonClick();
					setDudeButtonNormalBehavior();
					dudeButtonClickListener = null;
					
				}
			}
			
		});
		
		bubbleView.startAnimation(bubbleAnimation);
		dudeButton.startAnimation(buttonAnimation);
		
	}
	
	public boolean isAnimationActive() {
		return animationActive;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		
		
		if (!purchase.getHelper().handleActivityResult(requestCode, resultCode, data)) {
			dudeButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					hideSpeechBubble();				
				}
			});	
        } else {
            Log.d("ActivityHandler", "onActivityResult handled by IABUtil.");
        }
		
	}
	
	public void goToNextMessage() {
		((Message) main.getChildAt(getCurrentBlock())).setCompleted(true);
		((Message) main.getChildAt(getCurrentBlock())).setIsCurrent(false);
		currentBlock++;
		Message message = (Message) main.getChildAt(getCurrentBlock());
		message.setActive(Action.SKIP_ANIMATION);
		scroll.scrollBy(0, -getBlockHeight());
		dudeButtonClickListener = message;
		bubbleView.setText(message.getText());
	}
	
	private void setDudeButtonNormalBehavior() {
		
		dudeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				hideSpeechBubble();				
			}
		});	
		
	}
	
	public void finishPurchase() {
		data.setHasLicense();
		dudeButton.setText(getResources().getString(R.string.dude_button_text));
		setDudeButtonNormalBehavior();
		goToNextMessage();
	}
	
	private String mistakesCounter;
	private String helpCounter;
	private int totalMistakes;
	private int totalHelp;
	
	private void startEndGameSequence() {
		
		data.setGameFinished();
		firebaseData.setMistakes(data.getMistakesCommitted());
		firebaseData.setHelp(data.getTotalHelpUsed());
		Keyboard.hide();
		
		totalMistakes = data.getMistakesCommitted();		
		mistakesCounter = firebaseData.isMistakesReady() ? firebaseData.compareMistakes(totalMistakes) : null;
		
		String messageString = "Well done!\nYou've committed " + totalMistakes + " mistakes";
		String additionString = mistakesCounter!=null ? ", which is better than " + mistakesCounter + "% of all " +
		getResources().getString(R.string.app_name) + " users!\n" : ". Not bad!";
		messageString += additionString;
		
		bubbleView.setText(messageString);
		dudeButton.setText("Ok..");
		dudeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				followEndGameSequence();				
			}
			
		});
		
	}
	
	private void followEndGameSequence() {
		
		totalHelp = data.getTotalHelpUsed();
		helpCounter = firebaseData.isHelpReady() ? firebaseData.compareHelp(totalHelp) : null;
		
		String messageString = "On the other hand, you've asked me for help " + totalHelp + " times";
		String additionString = helpCounter!=null ? ", which is less than " + helpCounter + "% of all " +
				getResources().getString(R.string.app_name) + " users! " : ". ";
		String addition = totalHelp<5? "Unbelievable!" : "This could be improved!";
		messageString += additionString + addition;
		bubbleView.setText(messageString);
		dudeButton.setText("Ok....");
		dudeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finishEndGameSequence();
			}
			
		});
		
	}
	
	private void finishEndGameSequence() {
		bubbleView.setText("Would you like to try again and improve your score?");
		dudeButton.setText("Sure!");
		dudeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				data.clearData();					
				Intent startMain = new Intent(Intent.ACTION_MAIN);
				startMain.addCategory(Intent.CATEGORY_HOME);
				startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(startMain);
				finish();					
			}
			
		});
	}
	
	private void sortDudeView() {
		
		doorWidth = doorLeft.getWidth();
		screenWidth = findViewById(R.id.main_layout).getWidth();
		int screenHeight = findViewById(R.id.main_layout).getHeight();
		int topSectionHeight = findViewById(R.id.top_section).getHeight();
		scaleFactor = (float)screenWidth/(float)(doorWidth*2);
		
		doorLeft.setPivotX(0);
		doorLeft.setPivotY(0);
		doorRight.setPivotX(doorWidth);
		doorRight.setPivotY(0);
		
		dudeView.getLayoutParams().width = screenWidth/2;
		dudeView.getLayoutParams().height = screenHeight - topSectionHeight;
		dudeView.setX(screenWidth);
		dudeView.setY(findViewById(R.id.top_section).getHeight());
		
		bubbleView.setX(20);
		bubbleView.setY(topSectionHeight+15);
		bubbleView.setHeight((screenHeight-topSectionHeight)/2-15);
		bubbleView.setWidth(screenWidth/2-40);
		bubbleView.setTextStyle(R.color.black);
		
		int dudeButtonDimen = dudeButton.getHeight();
		dudeButton.setX((screenWidth/2-dudeButtonDimen)/2);
		int bubbleViewHeight = (screenHeight-topSectionHeight)/2;
		int bottomGap = (screenHeight - topSectionHeight - bubbleViewHeight - dudeButtonDimen)/2;
		dudeButton.setY(topSectionHeight+bubbleViewHeight+bottomGap);
		setDudeButtonNormalBehavior();	
		
	}
	
}