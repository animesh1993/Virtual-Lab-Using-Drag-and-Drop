package com.blahti.example.drag2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.Buffer;

import com.blahti.example.drag2.R;

import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.Keyboard.Row;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This activity presents a screen on which images can be added and moved around.
 * It also defines areas on the screen where the dragged views can be dropped. Feedback is
 * provided to the user as the objects are dragged over these drop zones.
 *
 * <p> Like the DragActivity in the previous version of the DragView example application, the
 * code here is derived from the Android Launcher code.
 * 
 */

public class DragActivityV2 extends Activity 
implements View.OnLongClickListener, View.OnClickListener, View.OnTouchListener
{


	/**
	 */
	// Constants

//	private static final int ENABLE_S2_MENU_ID = Menu.FIRST;
//	private static final int DISABLE_S2_MENU_ID = Menu.FIRST + 1;
	private static final int ADD_OBJECT_MENU_ID = Menu.FIRST + 1;
//	private static final int CHANGE_TOUCH_MODE_MENU_ID = Menu.FIRST + 3;
	private static final int PLAY_ANIM = Menu.FIRST + 2 ;
	private static final int RESET = Menu.FIRST + 3 ;
	private static final int PLAY_STEP = Menu.FIRST + 4 ;
	private static final int DELETE_FILE = Menu.FIRST + 5 ;

	/**
	 */
	// Variables

	private DragController mDragController;   // Object that sends out drag-drop events while a view is being moved.
	private DragLayer mDragLayer;             // The ViewGroup that supports drag-drop.
	private DropSpot mSpot2;                  // The DropSpot that can be turned on and off via the menu.
	private boolean mLongClickStartsDrag = false;    // If true, it takes a long click to start the drag operation.
	// Otherwise, only longTouch event starts a drag.


	public static TextView dragInfo ;
	//public static int imageNo = 2 ;
//	private static boolean animComplete = true ;
	private static int lineNo = 1 ;
	private static boolean stepMode = false ;

	public static final boolean Debugging = false;

	/**
	 */
	// Methods

	/**
	 * onCreate - called when the activity is first created.
	 * 
	 * Creates a drag controller and sets up three views so click and long click on the views are sent to this activity.
	 * The onLongClick method starts a drag sequence.
	 *
	 */

	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		mDragController = new DragController(this);

		setContentView(R.layout.main);
		setupViews ();

		dragInfo = (TextView) findViewById(R.id.textView1);
		dragInfo.setText("Data");
		dragInfo.setMovementMethod(new ScrollingMovementMethod());

	}
	/**
	 * Build a menu for the activity.
	 *
	 */    

	public boolean onCreateOptionsMenu (Menu menu) 
	{
		super.onCreateOptionsMenu(menu);

//		menu.add(0, ENABLE_S2_MENU_ID, 0, "Enable Spot2").setShortcut('1', 'c');
//		menu.add(0, DISABLE_S2_MENU_ID, 0, "Disable Spot2").setShortcut('2', 'c');
		menu.add(0, ADD_OBJECT_MENU_ID, 0, "Add View").setShortcut('9', 'z');
//		menu.add (0, CHANGE_TOUCH_MODE_MENU_ID, 0, "Change Touch Mode");
		menu.add(0, PLAY_ANIM, 0, "Play Animation") ;
		menu.add(0, RESET, 0, "Reset") ;
		menu.add(0,PLAY_STEP,0,"Step Mode") ;
		menu.add(0,DELETE_FILE,0,"Delete File") ;
		return true;
	}

	/**
	 * Handle a click on a view.
	 *
	 */    

	public void onClick(View v) 
	{
		if (mLongClickStartsDrag) {
			// Tell the user that it takes a long click to start dragging.
			toast ("Press and hold to drag an image.");
		}
	}

	/**
	 * Handle a long click.
	 *
	 * @param v View
	 * @return boolean - true indicates that the event was handled
	 */    

	public boolean onLongClick(View v) 
	{
		if (mLongClickStartsDrag) {

			//trace ("onLongClick in view: " + v + " touchMode: " + v.isInTouchMode ());

			// Make sure the drag was started by a long press as opposed to a long click.
			// (Note: I got this from the Workspace object in the Android Launcher code. 
			//  I think it is here to ensure that the device is still in touch mode as we start the drag operation.)
			if (!v.isInTouchMode()) {
				toast ("isInTouchMode returned false. Try touching the view again.");
				return false;
			}        
			return startDrag (v);
		}

		// If we get here, return false to indicate that we have not taken care of the event.
		return false;
	}

	/**
	 * Perform an action in response to a menu item being clicked.
	 *
	 */

	public boolean onOptionsItemSelected (MenuItem item) 
	{
		//mPaint.setXfermode(null);
		//mPaint.setAlpha(0xFF);

		switch (item.getItemId()) {
//		case ENABLE_S2_MENU_ID:
//			if (mSpot2 != null) mSpot2.setDragLayer (mDragLayer);
//			return true;
//		case DISABLE_S2_MENU_ID:
//			if (mSpot2 != null) mSpot2.setDragLayer (null);
//			return true;
		case ADD_OBJECT_MENU_ID:
			// Add a new object to the DragLayer and see if it can be dragged around.
			ImageView newView = new ImageView (this);
			newView.setImageResource (R.drawable.hello);
			newView.setId(IDGen.generateViewId());
			//            imageNo++ ;
			int w = 60;
			int h = 60;
			int left = 80;
			int top = 100;
			DragLayer.LayoutParams lp = new DragLayer.LayoutParams (w, h, left, top);
			mDragLayer.addView (newView, lp);
			newView.setOnClickListener(this);
			newView.setOnLongClickListener(this);
			newView.setOnTouchListener(this);
			return true;
//		case CHANGE_TOUCH_MODE_MENU_ID:
//			mLongClickStartsDrag = !mLongClickStartsDrag;
//			String message = mLongClickStartsDrag ? "Changed touch mode. Drag now starts on long touch (click)." 
//					: "Changed touch mode. Drag now starts on touch (click).";
//			Toast.makeText (getApplicationContext(), message, Toast.LENGTH_LONG).show ();
//			return true;
		case PLAY_ANIM:
			FileInputStream fis = null ;
			
			try {
				fis = openFileInput("media") ;
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				toast("Error: File Not Found") ;
				return true ;
			}

			BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

			nextMove(reader);
			return true ;
		case RESET:
			onCreate(null);
			lineNo = 1 ;
			return true ;
		case PLAY_STEP:
			stepMode = !stepMode ;
			return true ;
		case DELETE_FILE:
			File file = new File(getFilesDir().getAbsolutePath()+"/media") ;
//			toast((getFilesDir().getAbsolutePath()+"/media").toString()) ;
			file.delete() ;
			return true ;
			
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * This is the starting point for a drag operation if mLongClickStartsDrag is false.
	 * It looks for the down event that gets generated when a user touches the screen.
	 * Only that initiates the drag-drop sequence.
	 *
	 */    

	public boolean onTouch (View v, MotionEvent ev) 
	{
		// If we are configured to start only on a long click, we are not going to handle any events here.
		if (mLongClickStartsDrag) return false;

		boolean handledHere = false;

		final int action = ev.getAction();

		// In the situation where a long click is not needed to initiate a drag, simply start on the down event.
		if (action == MotionEvent.ACTION_DOWN) {
			handledHere = startDrag (v);
		}

		return handledHere;
	}

	/**
	 * Start dragging a view.
	 *
	 */    

	public boolean startDrag (View v)
	{
		// Let the DragController initiate a drag-drop sequence.
		// I use the dragInfo to pass along the object being dragged.
		// I'm not sure how the Launcher designers do this.
		Object dragInfo = v;
		mDragController.startDrag (v, mDragLayer, dragInfo, DragController.DRAG_ACTION_MOVE);
		return true;
	}

	/**
	 * Finds all the views we need and configure them to send click events to the activity.
	 *
	 */
	private void setupViews() 
	{
		DragController dragController = mDragController;

		mDragLayer = (DragLayer) findViewById(R.id.drag_layer);
		mDragLayer.setDragController(dragController);
		dragController.addDropTarget (mDragLayer);

		ImageView i1 = (ImageView) findViewById (R.id.Image1);
		ImageView i2 = (ImageView) findViewById (R.id.Image2);

		//    i1.setId(IDGen.generateViewId());
		//    i2.setId(IDGen.generateViewId());


		i1.setId(0);
		i2.setId(1);

		i1.setOnClickListener(this);
		i1.setOnLongClickListener(this);
		i1.setOnTouchListener(this);

		i2.setOnClickListener(this);
		i2.setOnLongClickListener(this);
		i2.setOnTouchListener(this);

		//    TextView tv = (TextView) findViewById (R.id.Text1);
		//    tv.setOnLongClickListener(this);
		//    tv.setOnTouchListener(this);

		// Set up some drop targets and enable them by connecting them to the drag layer
		// and the drag controller.
		// Note: If the dragLayer is not set, the drop spot will not accept drops.
		// That is the initial state of the second drop spot.
		//    DropSpot drop1 = (DropSpot) mDragLayer.findViewById (R.id.drop_spot1);
		//    drop1.setup (mDragLayer, dragController, R.color.drop_target_color1);
		//
		//    DropSpot drop2 = (DropSpot) mDragLayer.findViewById (R.id.drop_spot2);
		//    drop2.setup (null, dragController, R.color.drop_target_color2);
		//
		//    DropSpot drop3 = (DropSpot) mDragLayer.findViewById (R.id.drop_spot3);
		//    drop3.setup (mDragLayer, dragController, R.color.drop_target_color1);
		//
		//    // Save the second area so we can enable and disable it via the menu.
		//    mSpot2 = drop2;

		// Note: It might be interesting to allow the drop spots to be movable too.
		// Unfortunately, in the current implementation, that does not work
		// because the parent view of the DropTarget objects is not the drag layer.
		// The current DragLayer.onDrop method makes assumptions about how to reposition a dropped view.

		// Give the user a little guidance.
		String message = mLongClickStartsDrag ? "Press and hold to start dragging." 
				: "Touch a view to start dragging.";
		Toast.makeText (getApplicationContext(), message, Toast.LENGTH_LONG).show ();

	}

	/**
	 * Show a string on the screen via Toast.
	 * 
	 * @param msg String
	 * @return void
	 */

	public void toast (String msg)
	{
		Toast.makeText (getApplicationContext(), msg, Toast.LENGTH_SHORT).show ();
	} // end toast

	/**
	 * Send a message to the debug log and display it using Toast.
	 */

	public void trace (String msg) 
	{
		if (!Debugging) return;
		Log.d ("DragActivity", msg);
		toast (msg);
	}

	public void anim(int imageId,float xInitial, float yInitial, final float xFinal, final float yFinal){

		final ImageView logoFocus = (ImageView)findViewById(imageId) ;

//		final float amountToMoveDown = yFinal - yInitial ;
//		final float amountToMoveRight = xFinal - xInitial ;

//		Animation anim= new TranslateAnimation(0, amountToMoveRight, 0, amountToMoveDown); 
				Animation anim = new TranslateAnimation(0,(xFinal-xInitial),0,(yFinal-yInitial)) ;
		anim.setDuration(1000); 
		//	anim.setFillAfter(true); 
		//	anim.setFillEnabled(true); 
		anim.setAnimationListener(new AnimationListener() {

			public void onAnimationStart(Animation arg0) {
			}

			public void onAnimationRepeat(Animation arg0) {}

			public void onAnimationEnd(Animation arg0) {

				MyAbsoluteLayout.LayoutParams lp = (MyAbsoluteLayout.LayoutParams) logoFocus.getLayoutParams();
				lp.x = (int) xFinal ;
				lp.y = (int) yFinal ;
				logoFocus.setLayoutParams(lp);
				
//				nextMove(reader);
				if(!stepMode)
				{
				FileInputStream fis = null ;
				
				try {
					fis = openFileInput("media") ;
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					toast("Error: File not found") ;
					return ;
				}

				BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

				nextMove(reader);
				}
				
			}
		});
		logoFocus.startAnimation(anim);
	}

	public void nextMove(BufferedReader reader)
	{
		int imageId ;
		float initX, finX, initY, finY ;
		String[] RowData = null;
		int i = 1 ;
		try {
			String line;
			while(i < lineNo)
			{
				line = reader.readLine() ;
				i++ ;
			}
			if ((line = reader.readLine()) != null) 
			{

				RowData = line.split(",");
				imageId = Integer.parseInt(RowData[0]);
				initX = Float.parseFloat(RowData[1]);
				initY = Float.parseFloat(RowData[2]) ;
				finX = Float.parseFloat(RowData[3]) ;
				finY = Float.parseFloat(RowData[4]) ;
				
				anim(imageId,initX,initY,finX,finY);
			}
		}
		catch (IOException ex) {
		}
		finally 
		{
			lineNo++ ;
		}
	}

} // end class
