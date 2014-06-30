package com.virtualis.exp.simulation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.webkit.WebView.FindListener;
import android.widget.SeekBar;

import com.blahti.example.drag2.R;
import com.virtualis.exp.simulation.SeekArc.OnSeekArcChangeListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.display.DisplayManager;
import android.inputmethodservice.Keyboard.Row;
import android.media.Image;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView.ScaleType;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.util.*;
/**
 * This activity presents a screen on which images can be added and moved around.
 * It also defines areas on the screen where the dragged views can be dropped. Feedback is
 * provided to the user as the objects are dragged over these drop zones.
 *
 * <p> Like the DragActivity in the previous version of the DragView example application, the
 * code here is derived from the Android Launcher code.
 * 
 */

@SuppressLint("ResourceAsColor")
public class DragActivityV2 extends Activity 
implements View.OnLongClickListener, View.OnClickListener, View.OnTouchListener
{


	/**
	 */
	// Constants

	//	private static final int ENABLE_S2_MENU_ID = Menu.FIRST;
	//	private static final int DISABLE_S2_MENU_ID = Menu.FIRST + 1;
	//	private static final int ADD_OBJECT_MENU_ID = Menu.FIRST + 1;
	//	private static final int CHANGE_TOUCH_MODE_MENU_ID = Menu.FIRST + 3;
	//	private static final int PLAY_ANIM = Menu.FIRST + 2 ;
	//	private static final int RESET = Menu.FIRST + 3 ;
	//	private static final int PLAY_STEP = Menu.FIRST + 1 ;
//	private static final int DELETE_FILE = Menu.FIRST + 1 ;
	private static final int SHOW_BUTTONS = Menu.FIRST + 1 ;
//	private static final int ADMIN_MODE = Menu.FIRST + 3 ;
	//	private static final int SCALE = Menu.FIRST + 6 ;

	private String m_Text = "" ;

	private DisplayMetrics screenMetrics = new DisplayMetrics();

	/**
	 */
	// Variables

	private DragController mDragController;   // Object that sends out drag-drop events while a view is being moved.
	private DragLayer mDragLayer;             // The ViewGroup that supports drag-drop.
	//	private DropSpot mSpot2;                  // The DropSpot that can be turned on and off via the menu.
	private boolean mLongClickStartsDrag = false;    // If true, it takes a long click to start the drag operation.
	// Otherwise, only longTouch event starts a drag.


	public static TextView dragInfo ;
	//public static int imageNo = 2 ;
	//	private static boolean animComplete = true ;
	private static int lineNo = 1 ;
	private static boolean stepMode = false ;
	String targetUrl = "http://goo.gl/qfGTN3";
	private static boolean buttonsVisible = false ;

	public static final boolean Debugging = false;
	View objectSelectedForScaleRotate = null ;
	public static boolean ghostMode = false ;
	public static boolean studentMode = true ;
	public boolean firstTouchForLine = false ;
	public int lineInitialX = 0 ;
	public int lineInitialY = 0 ;
	BufferedReader reader = null ;
	public static boolean fileEndReached = false ;
	View objectSelectedForDelete = null ;
	public static boolean deleteMode = false ;
	private static int yOffSet = 40 ;
	//	private static boolean answeredCorrect = false;
	//	public boolean lineMode = false ;

	public enum TouchMode
	{
		MOVE,
		SCALE,
		ROTATE,
		LINE
	}

	public TouchMode currentTouchMode  = TouchMode.MOVE ;

	ListView list;
	public static String[] equipmentItems = {
			"Ammeter",
			"Battery",
			"Resistor",
			"Switch On",
			"Switch Off",
			"Voltmeter",
	} ;
	//			"Cancel"
	//	} ;
	public static Integer[] imageId = {
			R.drawable.exp_simulation_ammeter,
			R.drawable.exp_simulation_battery,
			R.drawable.exp_simulation_resistor,
			R.drawable.exp_simulation_glossy_green_button,
			R.drawable.exp_simulation_glossy_red_button,
			R.drawable.exp_simulation_voltmeter,
	};
	
	public static int[] imageIdInt = {
		R.drawable.exp_simulation_ammeter,
		R.drawable.exp_simulation_battery,
		R.drawable.exp_simulation_resistor,
		R.drawable.exp_simulation_glossy_green_button,
		R.drawable.exp_simulation_glossy_red_button,
		R.drawable.exp_simulation_voltmeter,
};
	
	
	//			R.drawable.cancel_icon
	//	};

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
		final Object objectDef = this ;

		setContentView(R.layout.exp_simulation_main);
		setupViews ();
		getWindowManager().getDefaultDisplay().getMetrics(screenMetrics);


		CustomList adapter = new CustomList(DragActivityV2.this, equipmentItems, imageId);
		list=(ListView)findViewById(R.id.list);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int which, long id) {

				list.bringToFront();
				if(which==5)
				{
					list.setVisibility(View.INVISIBLE);
					return ;
				}
				else if(which==3)
				{
					insertTextBox(objectDef);
					return ;
				}
				else if(which==4)
				{
					if(currentTouchMode == TouchMode.LINE)
						currentTouchMode = TouchMode.MOVE ;
					else
					{
						currentTouchMode = TouchMode.LINE ;
						//						ImageView lineImage = (ImageView) findViewById(R.id.blankBackground) ;
						//						createLine(lineImage, 0, 0, 1000, 1000, Color.GREEN);
						//						createLine(lineImage, 0, 0, 500, 0, Color.RED);
						//						findViewById(R.id.blankBackground).bringToFront();
					}
					trace("CurrentTouchMode set to " + currentTouchMode) ;
					return ;
				}
				Toast.makeText(DragActivityV2.this, "You Clicked at " +equipmentItems[+ which], Toast.LENGTH_SHORT).show();
				final ImageView newView = new ImageView (getApplicationContext());
				//				newView.setImageResource(R.drawable.beaker);
				
				newView.setImageResource(imageId[which]) ;
//				switch(which)
//				{
//				case 0:
//					newView.setImageResource(R.drawable.exp_simulation_ammeter);
//					break ;
//				case 1:
//					newView.setImageResource(R.drawable.exp_simulation_battery);
//					break ;
//				case 2:
//					newView.setImageResource(R.drawable.exp_simulation_resistor);
//					break ;
//				case 3:
//					//Text Box insertion
//					break ;
//				default:
//					break ;	
//				}

				if(!studentMode)
					newView.setId(IDGen.generateViewId());

				if(!studentMode)
				{
					FileOutputStream fos = null;
					try {
						fos = openFileOutput("media", MODE_APPEND);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					try {
						fos.write(("a" + "," + which + "," + newView.getId() + "\n").getBytes());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					try {
						fos.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else
				{
					if(ghostMode)
					{
						playBackForGhostMode(null) ;
						DragController.setMoveNo(DragController.getMoveNo() + 1 );
					}
				}

				int w = 60;
				int h = 60;
				int left = 80;
				int top = 100;
				DragLayer.LayoutParams lp = new DragLayer.LayoutParams (w, h, left, top);
				mDragLayer.addView (newView, lp);
				newView.setOnClickListener((OnClickListener) objectDef);
				newView.setOnLongClickListener((OnLongClickListener) objectDef);
				newView.setOnTouchListener((OnTouchListener) objectDef);

				MyAbsoluteLayout.LayoutParams lpMove = (MyAbsoluteLayout.LayoutParams) newView.getLayoutParams();
				lpMove.x = (int)(0.3 * screenMetrics.widthPixels) ;
				lpMove.y = (int) (0.4 * screenMetrics.heightPixels) - yOffSet ;
				newView.setLayoutParams(lpMove);
				//				newView.bringToFront();

				//				scaleAbsolute(newView, 50);
				//				newView.setAlpha(30);

				//				list.setVisibility(View.INVISIBLE);
			}
		});
		list.setVisibility(View.INVISIBLE);

		//		findViewById(R.id.addButton).setVisibility(View.INVISIBLE);
		//		findViewById(R.id.modeButton).setVisibility(View.INVISIBLE);
		//		findViewById(R.id.playButton).setVisibility(View.INVISIBLE);
		//		findViewById(R.id.resetButton).setVisibility(View.INVISIBLE);

		VerticalSeekBar scaleSeekBar=(VerticalSeekBar) findViewById(R.id.scaleBar);
		scaleSeekBar.setOnSeekBarChangeListener(new scaleListener());

		//		SeekBar rotateSeekBar = (SeekBar) findViewById(R.id.rotateBar) ;
		//		rotateSeekBar.setOnSeekBarChangeListener(new rotateListener());

		//		findViewById(R.id.rotateBar).setVisibility(View.INVISIBLE);
		findViewById(R.id.scaleBar).setVisibility(View.INVISIBLE);
		findViewById(R.id.plusSignImage).setVisibility(View.INVISIBLE);
		findViewById(R.id.minusSignImage).setVisibility(View.INVISIBLE);
		findViewById(R.id.rotateValueText).setVisibility(View.INVISIBLE) ;
		findViewById(R.id.seekArc).setVisibility(View.INVISIBLE);
		findViewById(R.id.modeRadioGroup).setVisibility(View.INVISIBLE); 
		buttonsVisible = true ;
		stepMode = false ;
		mLongClickStartsDrag = false ;

		SeekArc mSeekArc = (SeekArc) findViewById(R.id.seekArc) ;
		mSeekArc.setRotation(180);
		//		mSeekArc.setArcRotation(180);
		//		mSeekArc.setProgressWidth(360);

		mSeekArc.setOnSeekArcChangeListener(new OnSeekArcChangeListener() {

			float originalRotate ;

			public void onProgressChanged(SeekArc seekArc, int progress,
					boolean fromUser) {
				// Log the progress
				((TextView)findViewById(R.id.rotateValueText)).setText(progress+"");
				Log.d("DEBUG", "Progress is: "+progress);
				float rotate = progress ;
				if(objectSelectedForScaleRotate!=null)
				{
					//				scaleRelative(objectSelectedForScaleRotate,scale);
					objectSelectedForScaleRotate.setRotation(rotate);
				}
				//set textView's text
				//            yourTextView.setText(""+progress);
			}

			public void onStartTrackingTouch(SeekArc seekArc) {
				if(objectSelectedForScaleRotate!=null)
				{
					originalRotate = objectSelectedForScaleRotate.getRotation() ;
					trace("Inside onStart") ;
					trace("Inside originalRotate" + originalRotate) ;
				}
			}

			public void onStopTrackingTouch(SeekArc seekArc) {

				if(objectSelectedForScaleRotate != null)
				{	
					trace("Entered onStopTrackingTouch") ;
					float rotate = seekArc.getProgress() ;

					if(!studentMode)
					{
						trace("Entered non student mode of rotate") ;
						FileOutputStream fos = null;
						try {
							fos = openFileOutput("media", MODE_APPEND);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}

						try {
							fos.write(("r" + "," + objectSelectedForScaleRotate.getId() + "," + rotate + "\n").getBytes());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						try {
							fos.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					else
					{
						trace("Entered student mode of rotate") ;
						int imageId ;
						float rotateRead ;
						String[] RowData = null;
						int i = 1 ;

						FileInputStream fis = null ;

						try {
							fis = openFileInput("media") ;
						} catch (FileNotFoundException e) {
							e.printStackTrace();
							return ;
						}

						BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

						//						reader = DragController.reader ;
						try {
							String line = null;
							while(i < DragController.getMoveNo())
							{
								line = reader.readLine() ;
								//								trace("line read inside rotate = " + line) ;
								i++ ;
							}
							//							trace("Line after while in rotate" + line);
							if ((line = reader.readLine()) != null) 
							{
								trace("line compared in rotateObject "  + line) ;
								RowData = line.split(",");
								if(RowData[0].charAt(0) != 'r')
								{
									if(RowData[0].charAt(0) == 'a')
										DragController.setMoveNo(DragController.getMoveNo()+1);
									objectSelectedForScaleRotate.setRotation(originalRotate);
									return ;
								}

								imageId = Integer.parseInt(RowData[1]);
								rotateRead = Float.parseFloat(RowData[2]) ; 

								trace("Rotate Read " + rotateRead) ;
								trace("Rotation " + objectSelectedForScaleRotate.getRotation()) ;
								
//								float rotationDiff = Math.abs(objectSelectedForScaleRotate.getRotation() - rotateRead) ;
//								if(rotationDiff > 180)
//									rotationDiff -= 180 ;
//								if(rotationDiff < 20)
//								{
//									objectSelectedForScaleRotate.setRotation(originalRotate);
//									return ;
//								}

								objectSelectedForScaleRotate.setRotation(rotateRead);
								DragController.setMoveNo(DragController.getMoveNo()+1);
								if(ghostMode)
								{
									playBackForGhostMode(null) ;
									//									DragController.setMoveNo(DragController.getMoveNo() + 1 );
								}	
							}
							else
							{
								objectSelectedForScaleRotate.setRotation(originalRotate);
							}
						}
						catch (IOException ex)
						{
						}
					}
				}
			}

		});

		//		dragInfo = (TextView) findViewById(R.id.textView1);
		//		dragInfo.setText("Data");
		//		dragInfo.setMovementMethod(new ScrollingMovementMethod());

		if(studentMode)
			setTitle("Virtual Labs - Student Mode");
		else
			setTitle("Virtual Labs - Teacher/Admin Mode");

		//		findViewById(R.id.blankBackground).setAlpha(100);
		findViewById(R.id.blankBackground).setBackgroundColor(Color.BLACK) ;
		findViewById(R.id.deleteButton).setVisibility(View.INVISIBLE);
		//		((ImageView)findViewById(R.id.blankBackground)).setImageResource(R.drawable.blank_white_shape);
		//		findViewById(R.id.blankBackground).setAlpha(0);

		FileInputStream fis = null ;

		try {
			fis = openFileInput("media") ;
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			//			e1.printStackTrace();
			toast("Error: File Not Found") ;
			trace("File Error");
			return ;
		}

		reader = new BufferedReader(new InputStreamReader(fis));
		findViewById(R.id.seekArc).setRotation(0);
		findViewById(R.id.rotateValueText).setVisibility(View.INVISIBLE);
//		reset(null) ;
		//		DragController.resetReader(); 
		
		lineNo = 1 ;
		mLongClickStartsDrag = false ;
		stepMode = false ;
		objectSelectedForScaleRotate = null ;
		currentTouchMode = TouchMode.MOVE ;
		ghostMode = false ;
		studentMode = true ;
		DragController.setMoveNo(0);
		fileEndReached = false ;
		deleteMode = false ;
		objectSelectedForDelete = null ;
		//				DragController.resetReader();

		if(studentMode)
			setTitle("Virtual Labs - Student Mode");
		else
			setTitle("Virtual Labs - Teacher/Admin Mode");

		fis = null ;

		try {
			fis = openFileInput("media") ;
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			//			e1.printStackTrace();
			toast("Error: File Not Found") ;
			trace("File Error");
			return ;
		}

		reader = new BufferedReader(new InputStreamReader(fis));

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
		//		menu.add(0, ADD_OBJECT_MENU_ID, 0, "Add View").setShortcut('9', 'z');
		//		menu.add (0, CHANGE_TOUCH_MODE_MENU_ID, 0, "Change Touch Mode");
		//		menu.add(0, PLAY_ANIM, 0, "Play Animation") ;
		//		menu.add(0, RESET, 0, "Reset") ;
		//		menu.add(0,PLAY_STEP,0,"Step Mode") ;
//		menu.add(0,DELETE_FILE,0,"Delete File") ;
		menu.add(0,SHOW_BUTTONS,0,"Show/Hide Buttons") ;
//		menu.add(0,ADMIN_MODE,0,"Admin Mode Toggle") ;
		//		menu.add(0,SCALE,0,"Scale and Rotate Mode") ;

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

	public boolean onLongClick(final View v) 
	{
		//		//		if (mLongClickStartsDrag) {
		//		//
		//		//			//trace ("onLongClick in view: " + v + " touchMode: " + v.isInTouchMode ());
		//		//
		//		//			// Make sure the drag was started by a long press as opposed to a long click.
		//		//			// (Note: I got this from the Workspace object in the Android Launcher code. 
		//		//			//  I think it is here to ensure that the device is still in touch mode as we start the drag operation.)
		//		//			if (!v.isInTouchMode()) {
		//		//				toast ("isInTouchMode returned false. Try touching the view again.");
		//		//				return false;
		//		//			}        
		//		//			return startDrag (v);
		//		//		}
		//		if (mLongClickStartsDrag) 
		//		{
		//			AlertDialog.Builder alert = new AlertDialog.Builder(this);
		//			alert.setTitle("Scaling");
		//			alert.setMessage("Enter Scaling or Rotation Value (integer)");
		//
		//			// Set an EditText view to get user input 
		//			final EditText input = new EditText(this);
		//			input.setInputType(InputType.TYPE_CLASS_NUMBER);
		//			alert.setView(input);
		//			alert.setPositiveButton("Scale", new DialogInterface.OnClickListener() {
		//				public void onClick(DialogInterface dialog, int whichButton) {
		//					String value = input.getText().toString();
		//					// Do something with value!
		//					if(value.length() == 0)
		//					{
		//						toast("No text entered Error") ;
		//						return ;
		//					}
		//
		//					Float scale = Float.parseFloat(value) ;
		//					//			  scale = scale/100 ;
		//					trace("Scale =  " + scale);
		//					scaleRelative(v,scale);
		//
		//					FileOutputStream fos = null;
		//					try {
		//						fos = openFileOutput("media", MODE_APPEND);
		//					} catch (FileNotFoundException e) {
		//						// TODO Auto-generated catch block
		//						e.printStackTrace();
		//					}
		//
		//					try {
		//						fos.write(("s" + "," + v.getId() + "," + scale + "\n").getBytes());
		//					} catch (IOException e) {
		//						// TODO Auto-generated catch block
		//						e.printStackTrace();
		//					}
		//
		//					try {
		//						fos.close();
		//					} catch (IOException e) {
		//						// TODO Auto-generated catch block
		//						e.printStackTrace();
		//					}
		//
		//					//			  	scaleImageAbsolute((ImageView)v,Integer.parseInt(value));
		//					//					scaleImageRelative((ImageView)v, scale);
		//				}
		//			});
		//
		//			alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		//				public void onClick(DialogInterface dialog, int whichButton) {
		//					// Canceled.
		//					//				  scaleImageAbsolute((ImageView)v, 300);
		//					//					scaleRelative(v,200);
		//					//					Matrix matrix=new Matrix();
		//					//					((ImageView)v).setScaleType(ScaleType.MATRIX);   //required
		//					//					matrix.postRotate( 45f, ((ImageView)v).getDrawable().getBounds().width()/2, ((ImageView)v).getDrawable().getBounds().height()/2);
		//					//					((ImageView)v).setImageMatrix(matrix);
		//					//					v.setRotation(180);
		//				}
		//			});
		//
		//			alert.setNeutralButton("Rotate", new DialogInterface.OnClickListener() {
		//				public void onClick(DialogInterface dialog, int whichButton) {
		//					// Canceled.
		//					//				  scaleImageAbsolute((ImageView)v, 300);
		//					//					scaleRelative(v,200);
		//					//					Matrix matrix=new Matrix();
		//					//					((ImageView)v).setScaleType(ScaleType.MATRIX);   //required
		//					//					matrix.postRotate( 45f, ((ImageView)v).getDrawable().getBounds().width()/2, ((ImageView)v).getDrawable().getBounds().height()/2);
		//					//					((ImageView)v).setImageMatrix(matrix);
		//
		//
		//					String value = input.getText().toString() ;
		//
		//					if(value.length() == 0)
		//					{
		//						toast("No text entered Error") ;
		//						return ;
		//					}
		//
		//					Float rotate = Float.parseFloat(value) ;
		//					v.setRotation(rotate);
		//
		//					FileOutputStream fos = null;
		//					try {
		//						fos = openFileOutput("media", MODE_APPEND);
		//					} catch (FileNotFoundException e) {
		//						// TODO Auto-generated catch block
		//						e.printStackTrace();
		//					}
		//
		//					try {
		//						fos.write(("r" + "," + v.getId() + "," + rotate + "\n").getBytes());
		//					} catch (IOException e) {
		//						// TODO Auto-generated catch block
		//						e.printStackTrace();
		//					}
		//
		//					try {
		//						fos.close();
		//					} catch (IOException e) {
		//						// TODO Auto-generated catch block
		//						e.printStackTrace();
		//					}
		//				}
		//			}) ;
		//
		//			alert.show();
		//
		//			return true ;
		//		}
		//
		//

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
		//		case ADD_OBJECT_MENU_ID:
		//
		//			(findViewById(R.id.list)).setVisibility(View.VISIBLE);
		//			return true ;
		// Add a new object to the DragLayer and see if it can be dragged around.
		//			ImageView newView = new ImageView (this);
		//			newView.setImageResource (R.drawable.hello);
		//			newView.setId(IDGen.generateViewId());
		//			//            imageNo++ ;
		//			int w = 60;
		//			int h = 60;
		//			int left = 80;
		//			int top = 100;
		//			DragLayer.LayoutParams lp = new DragLayer.LayoutParams (w, h, left, top);
		//			mDragLayer.addView (newView, lp);
		//			newView.setOnClickListener(this);
		//			newView.setOnLongClickListener(this);
		//			newView.setOnTouchListener(this);
		//			return true;

		/* Option Menu for selecting Image */
		//			final ImageView newView = new ImageView (this);
		//			CharSequence equipment[] = new CharSequence[] {"burrete", "beaker", "testtube"};
		//
		//			/* For saving adding image into the csv data file*/
		//
		//			AlertDialog.Builder builder = new AlertDialog.Builder(this);
		//			builder.setTitle("Pick an equipment");
		//			builder.setItems(equipment, new DialogInterface.OnClickListener() {
		//				@Override
		//				public void onClick(DialogInterface dialog, int which) {
		//					// the user clicked on equipment[which]
		//					FileOutputStream fos = null;
		//					try {
		//						fos = openFileOutput("media", MODE_APPEND);
		//					} catch (FileNotFoundException e) {
		//						// 
		//						e.printStackTrace();
		//					}
		//
		//					try {
		//						fos.write(("a" + "," + which + "," + newView.getId() + "\n").getBytes());
		//					} catch (IOException e) {
		//						// TODO Auto-generated catch block
		//						e.printStackTrace();
		//					}
		//
		//					try {
		//						fos.close();
		//					} catch (IOException e) {
		//						// TODO Auto-generated catch block
		//						e.printStackTrace();
		//					}
		//
		//					switch(which)
		//					{
		//					case 0:
		//						newView.setImageResource(R.drawable.burrete);
		//						break ;
		//					case 1:
		//						newView.setImageResource(R.drawable.beaker);
		//						break ;
		//					case 2:
		//						newView.setImageResource(R.drawable.testtube);
		//						break ;
		//					default:
		//						break ;	
		//					}
		//				}
		//			});
		//			builder.show();
		//
		//			//			ImageView newView = new ImageView (this);
		//			//			new LoadImageTask(newView).execute(targetUrl);
		//			//			newView.setImageResource (R.drawable.hello);
		//			newView.setId(IDGen.generateViewId());
		//			//            imageNo++ ;
		//			int w = 60;
		//			int h = 60;
		//			int left = 60;
		//			int top = 60;
		//			DragLayer.LayoutParams lp = new DragLayer.LayoutParams (w, h, left, top);
		//			mDragLayer.addView (newView, lp);
		//			newView.setOnClickListener(this);
		//			newView.setOnLongClickListener(this);
		//			newView.setOnTouchListener(this);
		//
		//			//			lp.height = 100 ;
		//			//			lp.width = 100 ;
		//
		//			//			scaleRelative(newView,200);
		//
		//			//			scaleImageAbsolute(newView, 100);
		//
		//
		//			return true;

		//			image1 = (ImageView)findViewById(R.id.image1);			   
		//			new LoadImageTask(image1).execute(targetUrl);


		//		case CHANGE_TOUCH_MODE_MENU_ID:
		//			mLongClickStartsDrag = !mLongClickStartsDrag;
		//			String message = mLongClickStartsDrag ? "Changed touch mode. Drag now starts on long touch (click)." 
		//					: "Changed touch mode. Drag now starts on touch (click).";
		//			Toast.makeText (getApplicationContext(), message, Toast.LENGTH_LONG).show ();
		//			return true;
		//		case PLAY_ANIM:
		//			FileInputStream fis = null ;
		//
		//			try {
		//				fis = openFileInput("media") ;
		//			} catch (FileNotFoundException e1) {
		//				// TODO Auto-generated catch block
		//				e1.printStackTrace();
		//				toast("Error: File Not Found") ;
		//				return true ;
		//			}
		//
		//			BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
		//
		//			nextMove(reader);
		//			return true ;
		//		case RESET:
		//			onCreate(null);
		//			lineNo = 1 ;
		//			return true ;
		//		case PLAY_STEP:
		//			stepMode = !stepMode ;
		//			return true ;
//		case DELETE_FILE:
//			File file = new File(getFilesDir().getAbsolutePath()+"/media") ;
//			//			toast((getFilesDir().getAbsolutePath()+"/media").toString()) ;
//			file.delete() ;
//			return true ;
//			//		case SCALE:
//			//			mLongClickStartsDrag = !mLongClickStartsDrag ;
//			//			return true ;
		case SHOW_BUTTONS:
			if(buttonsVisible)
			{
//				findViewById(R.id.addButton).setVisibility(View.INVISIBLE);
				findViewById(R.id.modeRadioGroup).setVisibility(View.INVISIBLE);
				//				findViewById(R.id.playButton).setVisibility(View.INVISIBLE);
				findViewById(R.id.resetButton).setVisibility(View.INVISIBLE);
				findViewById(R.id.stepModeToggle).setVisibility(View.INVISIBLE);
				findViewById(R.id.ghostModeToggle).setVisibility(View.INVISIBLE);
				buttonsVisible = !buttonsVisible ;
			}
			else
			{
//				findViewById(R.id.addButton).setVisibility(View.VISIBLE);
				findViewById(R.id.modeRadioGroup).setVisibility(View.VISIBLE);
				//				findViewById(R.id.playButton).setVisibility(View.VISIBLE);
				findViewById(R.id.resetButton).setVisibility(View.VISIBLE);
				findViewById(R.id.stepModeToggle).setVisibility(View.VISIBLE);
				findViewById(R.id.ghostModeToggle).setVisibility(View.VISIBLE);
				buttonsVisible = !buttonsVisible ;
			}
			return true ;
//		case ADMIN_MODE:
//			studentMode = !studentMode ;
//			if(studentMode)
//				setTitle("Virtual Labs - Student Mode");
//			else
//				setTitle("Virtual Labs - Teacher/Admin Mode");
//			if(Debugging)
//				trace("STUDENT MODE = " + studentMode) ;
//			break ;
		}



		return super.onOptionsItemSelected(item);
	}

	/**
	 * This is the starting point for a drag operation if mLongClickStartsDrag is false.
	 * It looks for the down event that gets generated when a user touches the screen.
	 * Only that initiates the drag-drop sequence.
	 *
	 */    

	@SuppressLint("ClickableViewAccessibility")
	public boolean onTouch (final View v, MotionEvent ev) 
	{	
		if(v.getId() == R.id.blankBackground && findViewById(R.id.list).getVisibility() == View.VISIBLE)
			findViewById(R.id.list).setVisibility(View.INVISIBLE);
		if(deleteMode == true && (v.getId() != R.id.blankBackground))
		{
			objectSelectedForDelete =  v ;
			return true ;
		}
		else if(currentTouchMode == TouchMode.LINE && ev.getAction() == MotionEvent.ACTION_DOWN)
		{
			trace("Entered onTouch with touchmodeline") ;
			//			Canvas canvas = new Canvas();
			//			Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			//			paint.setColor(R.color.red);
			//			TextView line = new TextView(this);
			//			line.setBackgroundResource(android.R.color.holo_red_dark);
			//			line.setHeight((int)convertDpToPixel(1,this));

			if(!firstTouchForLine)
			{
				lineInitialX = (int) ev.getRawX() ;
				lineInitialY = (int) ev.getRawY() - yOffSet ;
				trace("lineInitialx set to " + lineInitialX) ;
				firstTouchForLine = true ;
			}
			else
			{
				trace("Came into createLIne") ;
				ImageView lineImage = (ImageView) findViewById(R.id.blankBackground) ;
				createLine(lineImage, lineInitialX, lineInitialY, ev.getRawX(), (ev.getRawY()-yOffSet), Color.GREEN);
				trace("Created line " + lineInitialX  + "," + lineInitialY + " " + ev.getRawX() + "," + (ev.getRawY()-yOffSet)) ;
				currentTouchMode = TouchMode.MOVE ;
				firstTouchForLine = false ;

				if(!studentMode)
				{
					FileOutputStream fos = null;
					try {
						fos = openFileOutput("media", MODE_APPEND);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}

					try {
						fos.write(("l" + "," + lineInitialX + "," + lineInitialY + "," + ev.getRawX() + "," + (ev.getRawY()-yOffSet) + "\n").getBytes());

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					try {
						fos.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				//				createLine(lineImage, 0, 0, 500, 0, Color.RED);
			}
			return true ;
		}
		else if (((currentTouchMode == TouchMode.SCALE) || (currentTouchMode == TouchMode.ROTATE)) && ev.getAction() == MotionEvent.ACTION_DOWN && (v != findViewById(R.id.blankBackground))) 
		{

			trace("Entered onTouch with if") ;

			objectSelectedForScaleRotate = v ;
			Log.d("DEBUG", "ObjectSelected "+objectSelectedForScaleRotate.getId());


			/*
						AlertDialog.Builder alert = new AlertDialog.Builder(this);
						alert.setTitle("Scaling");
						alert.setMessage("Enter Scaling or Rotation Value (integer)");

						// Set an EditText view to get user input 
						final EditText input = new EditText(this);
						input.setInputType(InputType.TYPE_CLASS_NUMBER);
						alert.setView(input);
						alert.setPositiveButton("Scale", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								String value = input.getText().toString();
								// Do something with value!
								if(value.length() == 0)
								{
									toast("No text entered Error") ;
									return ;
								}

								Float scale = Float.parseFloat(value) ;
								//			  scale = scale/100 ;
								trace("Scale =  " + scale);
								scaleRelative(v,scale);

								FileOutputStream fos = null;
								try {
									fos = openFileOutput("media", MODE_APPEND);
								} catch (FileNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

								try {
									fos.write(("s" + "," + v.getId() + "," + scale + "\n").getBytes());
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

								try {
									fos.close();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

								//			  	scaleImageAbsolute((ImageView)v,Integer.parseInt(value));
								//					scaleImageRelative((ImageView)v, scale);
							}
						});

						alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								// Canceled.
								//				  scaleImageAbsolute((ImageView)v, 300);
								//					scaleRelative(v,200);
								//					Matrix matrix=new Matrix();
								//					((ImageView)v).setScaleType(ScaleType.MATRIX);   //required
								//					matrix.postRotate( 45f, ((ImageView)v).getDrawable().getBounds().width()/2, ((ImageView)v).getDrawable().getBounds().height()/2);
								//					((ImageView)v).setImageMatrix(matrix);
								//					v.setRotation(180);
							}
						});

						alert.setNeutralButton("Rotate", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								// Canceled.
								//				  scaleImageAbsolute((ImageView)v, 300);
								//					scaleRelative(v,200);
								//					Matrix matrix=new Matrix();
								//					((ImageView)v).setScaleType(ScaleType.MATRIX);   //required
								//					matrix.postRotate( 45f, ((ImageView)v).getDrawable().getBounds().width()/2, ((ImageView)v).getDrawable().getBounds().height()/2);
								//					((ImageView)v).setImageMatrix(matrix);


								String value = input.getText().toString() ;

								if(value.length() == 0)
								{
									toast("No text entered Error") ;
									return ;
								}

								Float rotate = Float.parseFloat(value) ;
								v.setRotation(rotate);

								FileOutputStream fos = null;
								try {
									fos = openFileOutput("media", MODE_APPEND);
								} catch (FileNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

								try {
									fos.write(("r" + "," + v.getId() + "," + rotate + "\n").getBytes());
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

								try {
									fos.close();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}) ;

						alert.show();
			 */
			return true ;
		}
		else if(currentTouchMode == TouchMode.MOVE )
		{

			trace("Entered onTouch with else") ;
			boolean handledHere = false;
			final int action = ev.getAction();

			// In the situation where a long click is not needed to initiate a drag, simply start on the down event.
			if (action == MotionEvent.ACTION_DOWN && (v.getId() != R.id.blankBackground)) {
				trace("Entered StartDrag" ) ;
				handledHere = startDrag (v);
				objectSelectedForScaleRotate = v ;
			}



			return handledHere;
		}
		else
			return false ;
	}

	public static float convertDpToPixel(float dp, Context context){
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float px = dp * (metrics.densityDpi / 160f);
		return px;
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
		//		mDragLayer.setAllowDrag(true);
		//		mDragLayer.setOnTouchListener(this);
		dragController.addDropTarget (mDragLayer);
		findViewById(R.id.blankBackground).setOnTouchListener(this) ;
		//		findViewById(R.id.blankBackground).setOnTouchListener(this);

		//		ImageView lineImage = (ImageView) findViewById(R.id.blankBackground) ;
		//		trace("lineImage Height" + lineImage.getHeight() + "") ;
		//		createLine(lineImage, 0, 0, 100, 100, R.color.blue);

		//				ImageView i1 = (ImageView) findViewById (R.id.Image1);
		//		ImageView i2 = (ImageView) findViewById (R.id.Image2);

		//		    i1.setId(IDGen.generateViewId());
		//		    i2.setId(IDGen.generateViewId());


		//		i1.setId(0);
		//		i2.setId(1);

		//				i1.setOnClickListener(this);
		//		i1.setOnLongClickListener(this);
		//		i1.setOnTouchListener(this);
		//
		//		i2.setOnClickListener(this);
		//		i2.setOnLongClickListener(this);
		//		i2.setOnTouchListener(this);

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
//		toast (msg);
	}

	public void anim(int imageId,float xInitial, float yInitial, final float xFinal, final float yFinal){

		trace("ImageId move " + imageId) ;
		final View logoFocus = (View)findViewById(imageId) ;

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
				lp.y = (int) (yFinal-yOffSet) ;
				logoFocus.setLayoutParams(lp);

				//				nextMove(reader);
				if(!stepMode)
				{
					char movePer ;
					do
					{
						movePer = nextMove(reader) ;
						DragController.setMoveNo(DragController.getMoveNo()+1) ;
					}while(movePer!='m') ;
				}

			}
		});
		if(anim!=null && logoFocus!=null)
			logoFocus.startAnimation(anim);
	}

	@SuppressWarnings("deprecation")
	public char nextMove(BufferedReader reader)
	{
		final Object thisObj = this ;
		int imageId ;
		char caseRead = 'm'; 
		float initX, finX, initY, finY ;
		String[] RowData = null;
		int imageAdded = 0 ;
		float rotate = 0 ;
		float scale = 0;
		//		int i = 1 ;
		int w,h,left,top ;
		String quesRead = "" ;
		String ansRead = "" ;
		//		boolean answeredCorrect = false ;
		DragLayer.LayoutParams lp ;
		try {
			String line;
			//			trace("line NO " + lineNo) ;
			//			while(i < lineNo)
			//			{
			//				line = reader.readLine() ;
			//				i++ ;
			//				
			//			}
			if ((line = reader.readLine()) != null) 
			{
				trace("line Read in nextMove " + line) ;
				//				reader.mark(4096);

				RowData = line.split(",");
				switch(RowData[0].charAt(0))
				{
				case 'a' :
					imageAdded = Integer.parseInt(RowData[1]) ;
					imageId = Integer.parseInt(RowData[2]) ;
					ImageView newView = new ImageView (this);
					newView.setImageResource(imageIdInt[imageAdded]) ;			
//					switch(imageAdded)
//					{
//					case 0:
//						newView.setImageResource(R.drawable.exp_simulation_battery);
//						break ;
//					case 1:
//						newView.setImageResource(R.drawable.exp_simulation_bulb);
//						break ;
//					case 2:
//						newView.setImageResource(R.drawable.exp_simulation_resistor);
//						break ;
//					default:
//						break ;	
//					}

					newView.setId(imageId);
					w = 60;
					h = 60;
					left = 80;
					top = 100;
					lp = new DragLayer.LayoutParams (w, h, left, top);
					mDragLayer.addView (newView, lp);
					//					newView.setOnClickListener(this);
					//					newView.setOnLongClickListener(this);
					//					newView.setOnTouchListener(this);
					if(ghostMode)
						newView.setAlpha(30);
					//					nextMove(reader) ;

					MyAbsoluteLayout.LayoutParams lpMove = (MyAbsoluteLayout.LayoutParams) newView.getLayoutParams();
					lpMove.x = (int)(0.3 * screenMetrics.widthPixels) ;
					lpMove.y = (int) (0.4 * screenMetrics.heightPixels) - yOffSet ;
					newView.setLayoutParams(lpMove);

					if(studentMode && ghostMode)
					{
						ImageView newViewForUser = new ImageView (this);
						newViewForUser.setImageResource(imageIdInt[imageAdded]) ;
//						switch(imageAdded)
//						{
//						case 0:
//							newViewForUser.setImageResource(R.drawable.exp_simulation_battery);
//							break ;
//						case 1:
//							newViewForUser.setImageResource(R.drawable.exp_simulation_bulb);
//							break ;
//						case 2:
//							newViewForUser.setImageResource(R.drawable.exp_simulation_resistor);
//							break ;
//						default:
//							break ;	
//						}

						newViewForUser.setId(imageId+100);
						trace("NewViewForUser id = " + newViewForUser.getId()) ;
						w = 60;
						h = 60;
						left = 80;
						top = 100;
						DragLayer.LayoutParams lpForUser = new DragLayer.LayoutParams (w, h, left, top);
						mDragLayer.addView (newViewForUser, lpForUser);
						newViewForUser.setOnClickListener(this);
						newViewForUser.setOnLongClickListener(this);
						newViewForUser.setOnTouchListener(this);
						//						if(ghostMode)
						//							newView.setAlpha(30);
						//						//					nextMove(reader) ;



						MyAbsoluteLayout.LayoutParams lpMoveForUser = (MyAbsoluteLayout.LayoutParams) newViewForUser.getLayoutParams();
						lpMoveForUser.x = (int)(0.3 * screenMetrics.widthPixels) ;
						lpMoveForUser.y = (int) (0.4 * screenMetrics.heightPixels) - yOffSet ;
						newViewForUser.setLayoutParams(lpMoveForUser);

					}

					caseRead = 'a' ;
					break ;

				case 'm':

					imageId = Integer.parseInt(RowData[1]);
					initX = Float.parseFloat(RowData[2]);
					initY = Float.parseFloat(RowData[3]) ;
					finX = Float.parseFloat(RowData[4]) ;
					finY = Float.parseFloat(RowData[5]) ;

					initX *= screenMetrics.widthPixels ;
					initY *= screenMetrics.heightPixels ;
					finX *= screenMetrics.widthPixels ;
					finY *= screenMetrics.heightPixels ;

					initX /= 100 ;
					initY /= 100 ;
					finX /= 100 ;
					finY /= 100 ;

					//					trace("initX = " + initX) ;
					//					trace("heightPixels = " + screenMetrics.heightPixels) ;
					anim(imageId,initX,initY,finX,finY);
					caseRead = 'm' ;
					break ;

				case 'r':

					imageId = Integer.parseInt(RowData[1]) ;
					rotate = Float.parseFloat(RowData[2]) ;

					((ImageView)findViewById(imageId)).setRotation(rotate);
					caseRead = 'r' ;
					break ;

				case 's':

					imageId = Integer.parseInt(RowData[1]) ;
					scale = Float.parseFloat(RowData[2]) ;
					scale /= 100 ;
					scale *= screenMetrics.heightPixels ;

					//					float scaleDec = scale / 100 ;
					//					//				trace("scaleDec" + scaleDec) ;
					//					//				trace("getLayoutHeight before"+v.getLayoutParams().height) ;
					//					((ImageView)findViewById(imageId)).getLayoutParams().height *= scaleDec ;
					//					//				trace("getLayoutHeight after"+v.getLayoutParams().height) ;
					//					((ImageView)findViewById(imageId)).getLayoutParams().width *= scaleDec ;
					//					((ImageView)findViewById(imageId)).setLayoutParams(((ImageView)findViewById(imageId)).getLayoutParams());
					scaleAbsolute(findViewById(imageId), scale);
					caseRead = 's' ;
					break ;
				case 't':
					//					textRead = RowData[1] ;
					//					imageId = Integer.parseInt(RowData[2]) ;

					imageId = Integer.parseInt(RowData[1]);
					quesRead = RowData[2] ;
					ansRead = RowData[3] ;

					if(quesRead.isEmpty())
					{
						TextView tv = new TextView(this);
						//					trace(m_Text) ;
						tv.setText(ansRead);

						//					w = 60;
						//					h = 60;
						//					left = 80;
						//					top = 100;
						//					lp = new DragLayer.LayoutParams (w, h, left, top);
						mDragLayer.addView (tv);
						tv.setOnClickListener((OnClickListener) this);
						tv.setOnLongClickListener((OnLongClickListener) this);
						tv.setOnTouchListener((OnTouchListener) this);
						tv.setId(imageId);

						MyAbsoluteLayout.LayoutParams lpText = (MyAbsoluteLayout.LayoutParams) tv.getLayoutParams();
						lpText.x = (int)(0.3 * screenMetrics.widthPixels) ;
						lpText.y = (int) (0.4 * screenMetrics.heightPixels) - yOffSet ;
						tv.setLayoutParams(lpText);
						caseRead = 't' ;
					}
					else
					{
						final String ansComp = ansRead ;
						final int imageIdFinal = imageId ;

						if(studentMode)
						{
							//						do
							//						{

							final EditText input = new EditText((Context) thisObj);
							//						alert.setView(input);
							final AlertDialog d = new AlertDialog.Builder((Context) thisObj)
							.setView(input)
							.setTitle(quesRead)
							.setMessage(quesRead)
							.setPositiveButton(android.R.string.ok, null) //Set to null. We override the onclick
							//				        .setNegativeButton(android.R.string.cancel, null)
							.create();

							d.setCancelable(false);
							d.setCanceledOnTouchOutside(false);
							d.setOnShowListener(new DialogInterface.OnShowListener() {

								@Override
								public void onShow(DialogInterface dialog) {

									Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
									b.setOnClickListener(new View.OnClickListener() {

										@Override
										public void onClick(View view) {
											//										d.dismiss(); 
											Editable value = input.getText();
											trace("Got text = " + value);
											trace("ansComp = " + ansComp) ;
											trace("Value equals ansComp = " + value.toString().equalsIgnoreCase(ansComp)) ;
											if(value.toString().equalsIgnoreCase(ansComp))
											{
												DragController.setMoveNo(DragController.getMoveNo() + 1);

												TextView tv = new TextView((Context) thisObj);
												//					trace(m_Text) ;
												tv.setText(ansComp);

												//					w = 60;
												//					h = 60;
												//					left = 80;
												//					top = 100;
												//					lp = new DragLayer.LayoutParams (w, h, left, top);
												mDragLayer.addView (tv);
												tv.setOnClickListener((OnClickListener) thisObj);
												tv.setOnLongClickListener((OnLongClickListener) thisObj);
												tv.setOnTouchListener((OnTouchListener) thisObj);
												tv.setId(imageIdFinal);

												MyAbsoluteLayout.LayoutParams lpText = (MyAbsoluteLayout.LayoutParams) tv.getLayoutParams();
												lpText.x = (int)(0.3 * screenMetrics.widthPixels) ;
												lpText.y = (int) (0.4 * screenMetrics.heightPixels) - yOffSet ;
												tv.setLayoutParams(lpText);

												playBackForGhostMode(null) ;
												d.dismiss(); 
											}
											else
											{
												toast("Wrong -- Enter again");
												input.setText("");
												input.setHint("Wrong Answer - Enter Again"); 
											}
										}
									});
								}
							});

							d.show(); 
							caseRead = 'q' ;
						}
						else
						{
							TextView tv = new TextView(this);
							//					trace(m_Text) ;
							tv.setText(ansRead);

							//					w = 60;
							//					h = 60;
							//					left = 80;
							//					top = 100;
							//					lp = new DragLayer.LayoutParams (w, h, left, top);
							mDragLayer.addView (tv);
							tv.setOnClickListener((OnClickListener) this);
							tv.setOnLongClickListener((OnLongClickListener) this);
							tv.setOnTouchListener((OnTouchListener) this);
							tv.setId(imageId);

							MyAbsoluteLayout.LayoutParams lpText = (MyAbsoluteLayout.LayoutParams) tv.getLayoutParams();
							lpText.x = (int)(0.3 * screenMetrics.widthPixels) ;
							lpText.y = (int) (0.4 * screenMetrics.heightPixels) - yOffSet ;
							tv.setLayoutParams(lpText);
							caseRead = 't' ;
						}
					}
					break ;
				case 'l':
					initX = Float.parseFloat(RowData[1]) ;
					initY = Float.parseFloat(RowData[2]);
					finX = Float.parseFloat(RowData[3]) ;
					finY = Float.parseFloat(RowData[4]);

					initX *= screenMetrics.widthPixels ;
					initY *= screenMetrics.heightPixels ;
					finX *= screenMetrics.widthPixels ;
					finY *= screenMetrics.heightPixels ;

					initX /= 100 ;
					initY /= 100 ;
					finX /= 100 ;
					finY /= 100 ;

					createLine((ImageView)findViewById(R.id.blankBackground), initX, initY, finX, finY, Color.GREEN);
					caseRead = 'l' ;
					break ;
				case 'd':
					imageId = Integer.parseInt(RowData[1]) ;
					trace("Delete" + " id " + imageId) ;
					findViewById(imageId).setVisibility(View.GONE);
					break ;
				case 'c':
					imageId = Integer.parseInt(RowData[1]) ;
					int imageNoChange = Integer.parseInt(RowData[2]) ;
					((ImageView)findViewById(imageId)).setImageResource(imageIdInt[imageNoChange]) ;
//					switch(imageNoChange)
//					{
//					case 0:
//						((ImageView)findViewById(imageId)).setImageResource(R.drawable.exp_simulation_battery);
//						break ;
//					case 1:
//						((ImageView)findViewById(imageId)).setImageResource(R.drawable.exp_simulation_bulb);
//						break ;
//					case 2:
//						((ImageView)findViewById(imageId)).setImageResource(R.drawable.exp_simulation_resistor);
//						break ;
//					default:
//						break ;	
//					}

					if(studentMode && ghostMode)
					{
						((ImageView)findViewById(imageId+100)).setImageResource(imageIdInt[imageNoChange]) ;
//						switch(imageNoChange)
//						{
//						case 0:
//							((ImageView)findViewById(imageId+100)).setImageResource(R.drawable.exp_simulation_battery);
//							break ;
//						case 1:
//							((ImageView)findViewById(imageId+100)).setImageResource(R.drawable.exp_simulation_bulb);
//							break ;
//						case 2:
//							((ImageView)findViewById(imageId+100)).setImageResource(R.drawable.exp_simulation_resistor);
//							break ;
//						default:
//							break ;	
//						}
					}

					caseRead = 'c' ;
					break ;
					//				case 'q':
					//					caseRead = 'q' ;
					//					quesRead = RowData[1] ;
					//					ansRead = RowData[2] ;
					//
					//					//					final String ansComp = ansRead ;
					//					//
					//					//					if(studentMode)
					//					//					{
					//					//						//						do
					//					//						//						{
					//					//
					//					//						final EditText input = new EditText((Context) thisObj);
					//					//						//						alert.setView(input);
					//					//						final AlertDialog d = new AlertDialog.Builder((Context) thisObj)
					//					//						.setView(input)
					//					//						.setTitle(quesRead)
					//					//						.setMessage(quesRead)
					//					//						.setPositiveButton(android.R.string.ok, null) //Set to null. We override the onclick
					//					//						//				        .setNegativeButton(android.R.string.cancel, null)
					//					//						.create();
					//					//
					//					//						d.setOnShowListener(new DialogInterface.OnShowListener() {
					//					//
					//					//							@Override
					//					//							public void onShow(DialogInterface dialog) {
					//					//
					//					//								Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
					//					//								b.setOnClickListener(new View.OnClickListener() {
					//					//
					//					//									@Override
					//					//									public void onClick(View view) {
					//					//										//										d.dismiss(); 
					//					//										Editable value = input.getText();
					//					//										trace("Got text = " + value);
					//					//										trace("ansComp = " + ansComp) ;
					//					//										trace("Value equals ansComp = " + value.toString().equalsIgnoreCase(ansComp)) ;
					//					//										if(value.toString().equalsIgnoreCase(ansComp))
					//					//										{
					//					//											DragController.setMoveNo(DragController.getMoveNo() + 1);
					//					//											playBackForGhostMode(null) ;
					//					//											d.dismiss(); 
					//					//										}
					//					//										else
					//					//										{
					//					//											toast("Wrong -- Enter again");
					//					//										}
					//					//
					//					//
					//					//										// TODO Do something
					//					//
					//					//										//Dismiss once everything is OK.
					//					//										//				                d.dismiss();
					//					//									}
					//					//								});
					//					//							}
					//					//						});
					//					//
					//					//						d.show(); 
					//					//					}
					//					//					else
					//					//					{
					//					//						trace("Entered q else with ques " + quesRead + " ans " + ansRead) ;
					//					//					}
					//
					//					//					if(answeredCorrect)
					//					//					{
					//					//						answeredCorrect = false ; 
					//					//						DragController.setMoveNo(DragController.getMoveNo() + 1);
					//					//						playBackForGhostMode(null) ;
					//					//					}
					//					//					else
					//					//						trace("Answered Wrong") ;
					//					break ;
				}

				//				line = reader.readLine() ;
				//				if(line!=null)
				//				{
				//					RowData = line.split(",");
				//					caseRead = RowData[0].charAt(0) ;
				//				}
				//				reader.reset();
			}
			else
			{
				fileEndReached = true ;
			}
		}
		catch (IOException ex) 
		{
		}
		finally 
		{
			lineNo++ ;
		}
		return caseRead; 
	}

	@SuppressWarnings("unused")
	private class LoadImageTask extends AsyncTask<String, Void, Bitmap>{

		ImageView targetImageView;

		LoadImageTask(ImageView iv){
			targetImageView = iv;
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			Bitmap bm = loadImageFromUrl(params[0]);
			return bm;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			targetImageView.setImageBitmap(result);
		}

	}

	private Bitmap loadImageFromUrl(String targetUrl){
		Bitmap bm = null;

		try {
			URL url = new URL(targetUrl);
			URLConnection connection = url.openConnection();
			InputStream inputStream = connection.getInputStream();
			bm = BitmapFactory.decodeStream(inputStream);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return bm;
	}

	@SuppressWarnings("unused")
	private void scaleImageAbsolute(ImageView view, int boundBoxInDp)
	{
		// Get the ImageView and its bitmap
		Drawable drawing = view.getDrawable();
		Bitmap bitmap = ((BitmapDrawable)drawing).getBitmap();

		// Get current dimensions
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		trace("before width "+ width) ;
		trace("before height "+ height) ;
		// Determine how much to scale: the dimension requiring less scaling is
		// closer to the its side. This way the image always stays inside your
		// bounding box AND either x/y axis touches it.
		float xScale = ((float) boundBoxInDp) / width;
		float yScale = ((float) boundBoxInDp) / height;
		float scale = (xScale <= yScale) ? xScale : yScale;

		trace("Scale inside function = " + scale+"") ;
		// Create a matrix for the scaling and add the scaling data
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);

		// Create a new bitmap and convert it to a format understood by the ImageView
		Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
		@SuppressWarnings("deprecation")
		BitmapDrawable result = new BitmapDrawable(scaledBitmap);
		width = scaledBitmap.getWidth();
		height = scaledBitmap.getHeight();

		// Apply the scaled bitmap
		view.setImageDrawable(result);

		// Now change ImageView's dimensions to match the scaled image
		DragLayer.LayoutParams params = (DragLayer.LayoutParams) view.getLayoutParams();
		params.width = width;
		params.height = height;
		view.setLayoutParams(params);
	}

	@SuppressWarnings("unused")
	private void scaleImageRelative(ImageView view, float scale)
	{
		// Get the ImageView and its bitmap
		Drawable drawing = view.getDrawable();
		Bitmap bitmap = ((BitmapDrawable)drawing).getBitmap();

		// Get current dimensions
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		//
		//	    // Determine how much to scale: the dimension requiring less scaling is
		//	    // closer to the its side. This way the image always stays inside your
		//	    // bounding box AND either x/y axis touches it.
		//	    float xScale = ((float) boundBoxInDp) / width;
		//	    float yScale = ((float) boundBoxInDp) / height;
		//	    float scale = (xScale <= yScale) ? xScale : yScale;

		// Create a matrix for the scaling and add the scaling data
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);

		// Create a new bitmap and convert it to a format understood by the ImageView
		Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
		@SuppressWarnings("deprecation")
		BitmapDrawable result = new BitmapDrawable(scaledBitmap);
		width = scaledBitmap.getWidth();
		height = scaledBitmap.getHeight();

		// Apply the scaled bitmap
		view.setImageDrawable(result);

		// Now change ImageView's dimensions to match the scaled image
		DragLayer.LayoutParams params = (DragLayer.LayoutParams) view.getLayoutParams();
		params.width = width;
		params.height = height;
		view.setLayoutParams(params);
	}

	@SuppressWarnings("unused")
	private int dpToPx(int dp)
	{
		float density = getApplicationContext().getResources().getDisplayMetrics().density;
		return Math.round((float)dp * density);
	}

	@SuppressWarnings("unused")
	private void scaleRelative(View v,float scale)
	{
		float scaleDec = scale / 100 ;
		//				trace("scaleDec" + scaleDec) ;
		//				trace("getLayoutHeight before"+v.getLayoutParams().height) ;
		v.getLayoutParams().height *= scaleDec ;
		//				trace("getLayoutHeight after"+v.getLayoutParams().height) ;
		v.getLayoutParams().width *= scaleDec ;
		v.setLayoutParams(v.getLayoutParams());

	}

	private void scaleAbsolute(View v,float value)
	{
		v.getLayoutParams().height = (int)value ;
		v.getLayoutParams().width = (int)value ;
		v.setLayoutParams(v.getLayoutParams());
	}

	public void scaleTextView(View v,float value) {

		Rect bounds = new Rect();
		Paint textPaint = ((TextView) v).getPaint();
		textPaint.setTextAlign(Align.LEFT);
		textPaint.setTextSize(((TextView)v).getTextSize());
		textPaint.getTextBounds((String)(((TextView)v).getText()),0,(((TextView)v).getText().length()),bounds);
		int height = bounds.height();
		int width = bounds.width();

		//		int charNo = ((TextView)v).getText().length() ;
		//		((TextView)v).setHeight(height);
		//		((TextView)v).setWidth(width);
		//		v.getLayoutParams().height = height + yOffSet ;
		//		v.getLayoutParams().width = width + yOffSet;
		//		v.setLayoutParams(v.getLayoutParams());
		//		
		//		TextView tv = (TextView) findViewById(v.getId());
		//		tv.invalidate();
		//		int height_in_pixels = tv.getLineCount() * tv.getLineHeight();
		//		tv.setHeight(height_in_pixels);

		v.setLayoutParams(new MyAbsoluteLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, (int)v.getX(), (int)v.getY())); 


	}

	public void playBack(View v)
	{
		if(reader==null)
		{
			trace("File Not Found Error - Provide File and Reset") ;
			return ;
		}
		if(!fileEndReached)
		{	
			//			FileInputStream fis = null ;
			//
			//			try {
			//				fis = openFileInput("media") ;
			//			} catch (FileNotFoundException e1) {
			//				// TODO Auto-generated catch block
			//				//			e1.printStackTrace();
			//				toast("Error: File Not Found") ;
			//				trace("File Error");
			//				return ;
			//			}

			//		trace("Error Reaching wrong code");

			char movePerformed ;

			//			reader = new BufferedReader(new InputStreamReader(fis));
			if(!stepMode)
			{
				do
				{
					movePerformed = nextMove(reader) ;
				}while(movePerformed!='m') ;
			}
			else
			{
				if(ghostMode && studentMode)
				{
					do
					{
						movePerformed = nextMove(reader) ;
						DragController.setMoveNo(DragController.getMoveNo()+1) ;
					}while(movePerformed=='a'
							||movePerformed=='c'
							||movePerformed=='t'
							||movePerformed=='q') ;
					
				}
//				nextMove(reader) ;
			}
		}
	}

	public char playBackForGhostMode(View v)
	{
		if(reader==null)
		{
			trace("File Not Found Error - Provide File and Reset") ;
			return 'z';
		}
		if(!fileEndReached)
		{	
			//			FileInputStream fis = null ;
			//
			//			try {
			//				fis = openFileInput("media") ;
			//			} catch (FileNotFoundException e1) {
			//				// 
			//				//			e1.printStackTrace();
			//				toast("Error: File Not Found") ;
			//				trace("File Error");
			//				return ;
			//			}

			//		trace("Error Reaching wrong code");
			return nextMove(reader) ;
		}

		return 'z' ;
	}

	public void reset(View v)
	{
		AlertDialog confirmBox = resetConfirmation();
		confirmBox.show();

		//		onCreate(null);
		//		lineNo = 1 ;
		//		mLongClickStartsDrag = false ;
		//		stepMode = false ;
		//		objectSelectedForScaleRotate = null ;
		//		currentTouchMode = TouchMode.MOVE ;
		//		ghostMode = false ;
		//		studentMode = false ;
		//		DragController.setMoveNo(0);
		//		fileEndReached = false ;
		//		deleteMode = false ;
		//		objectSelectedForDelete = null ;
		////		DragController.resetReader();
		//
		//		if(studentMode)
		//			setTitle("Virtual Labs - Student Mode");
		//		else
		//			setTitle("Virtual Labs - Teacher/Admin Mode");
		//
		//		FileInputStream fis = null ;
		//
		//		try {
		//			fis = openFileInput("media") ;
		//		} catch (FileNotFoundException e1) {
		//			// TODO Auto-generated catch block
		//			//			e1.printStackTrace();
		//			toast("Error: File Not Found") ;
		//			trace("File Error");
		//			return ;
		//		}
		//
		//		reader = new BufferedReader(new InputStreamReader(fis));
	}

	public void addObject(View v)
	{
		if(findViewById(R.id.list).getVisibility()==View.INVISIBLE)
			(findViewById(R.id.list)).setVisibility(View.VISIBLE);
		else
			(findViewById(R.id.list)).setVisibility(View.INVISIBLE);
	}

	public void stepModeToggle(View v)
	{
		boolean on = ((ToggleButton) v).isChecked();

		if(on)
			stepMode = true;
		else
			stepMode = false ;
	}

	public void modeRadioClick(View view) {
		// Is the button now checked?
		boolean checked = ((RadioButton) view).isChecked();

		// Check which radio button was clicked
		switch(view.getId()) {
		case R.id.moveRadioButton:
			if (checked)
			{
				deleteMode = false ;
				toast("Move Mode") ;
				currentTouchMode = TouchMode.MOVE ;
				//				objectSelectedForScaleRotate = null ;
				findViewById(R.id.seekArc).setVisibility(View.INVISIBLE);
				findViewById(R.id.scaleBar).setVisibility(View.INVISIBLE);
				findViewById(R.id.plusSignImage).setVisibility(View.INVISIBLE);
				findViewById(R.id.minusSignImage).setVisibility(View.INVISIBLE);
				findViewById(R.id.deleteButton).setVisibility(View.INVISIBLE);
				findViewById(R.id.rotateValueText).setVisibility(View.INVISIBLE);
				if(objectSelectedForScaleRotate!=null)
					objectSelectedForScaleRotate.setBackgroundColor(Color.argb(0, 0, 0, 0));
			}

			break;
			//		case R.id.rotateRadioButton:
			//			if (checked)
			//			{
			//				toast("Rotate");
			//				currentTouchMode = TouchMode.ROTATE ;
			//			}
			//			break;
		case R.id.scaleRadioButton:
			if(checked)
			{
				deleteMode = false ;
				toast("Touch the object to scale and rotate");
				currentTouchMode = TouchMode.SCALE ;
				findViewById(R.id.seekArc).setVisibility(View.VISIBLE);
				findViewById(R.id.scaleBar).setVisibility(View.VISIBLE);
				findViewById(R.id.plusSignImage).setVisibility(View.VISIBLE);
				findViewById(R.id.minusSignImage).setVisibility(View.VISIBLE);
				findViewById(R.id.deleteButton).setVisibility(View.INVISIBLE);
				findViewById(R.id.rotateValueText).setVisibility(View.VISIBLE);
				findViewById(R.id.seekArc).bringToFront();
				findViewById(R.id.scaleBar).bringToFront();
				findViewById(R.id.plusSignImage).bringToFront();
				findViewById(R.id.minusSignImage).bringToFront();
				if(objectSelectedForScaleRotate!=null)
					objectSelectedForScaleRotate.setBackgroundColor(Color.argb(100, 255, 0, 0));
				//				objectSelectedForScaleRotate = objectSelectedForScaleRotate ;
			}
			break ;
		case R.id.deleteRadioButton:
			if(checked)
			{
				deleteMode = true ;
				findViewById(R.id.deleteButton).setVisibility(View.VISIBLE);
				findViewById(R.id.rotateValueText).setVisibility(View.INVISIBLE);
				findViewById(R.id.seekArc).setVisibility(View.INVISIBLE);
				findViewById(R.id.scaleBar).setVisibility(View.INVISIBLE);
				findViewById(R.id.plusSignImage).setVisibility(View.INVISIBLE);
				findViewById(R.id.minusSignImage).setVisibility(View.INVISIBLE);
				findViewById(R.id.rotateValueText).setVisibility(View.INVISIBLE);
			}
			break ;
		}
	}

	private class scaleListener implements SeekBar.OnSeekBarChangeListener {

		float originalScale ;

		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// Log the progress
			Log.d("DEBUG", "Progress is: "+progress);
			float scale = progress ;
			scale /= 100 ;
			scale *= screenMetrics.heightPixels ;
			if(objectSelectedForScaleRotate!=null && scale != 0)
			{
				if(objectSelectedForScaleRotate instanceof TextView)
				{
					if(progress > 5)
					{
						((TextView)objectSelectedForScaleRotate).setTextSize(progress);
						((TextView)objectSelectedForScaleRotate).setTop(0);
						((TextView)objectSelectedForScaleRotate).setLeft(0);
						//						((TextView)objectSelectedForScaleRotate).setGravity(Gravity.CENTER);
						scaleTextView(objectSelectedForScaleRotate,progress) ;
						((TextView)objectSelectedForScaleRotate).setLeft(0);
						((TextView)objectSelectedForScaleRotate).setTop(0);
						//						((TextView)objectSelectedForScaleRotate).setGravity(Gravity.CENTER);
					}
				}
				//				scaleRelative(objectSelectedForScaleRotate,scale);
				else
					scaleAbsolute(objectSelectedForScaleRotate,scale);
			}
			//set textView's text
			//            yourTextView.setText(""+progress);
		}

		public void onStartTrackingTouch(SeekBar seekBar) {
			if(objectSelectedForScaleRotate!=null)
				originalScale = objectSelectedForScaleRotate.getLayoutParams().height ;
			trace("Original Value = " + originalScale) ;
		}

		public void onStopTrackingTouch(SeekBar seekBar) {

			if(objectSelectedForScaleRotate!=null)
			{
				float scale = seekBar.getProgress() ;
				scale /= 100 ;
				scale *= screenMetrics.heightPixels ;
				if(!studentMode)
				{
					FileOutputStream fos = null;
					try {
						fos = openFileOutput("media", MODE_APPEND);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					try {
						fos.write(("s" + "," + objectSelectedForScaleRotate.getId() + "," + (scale/screenMetrics.heightPixels)*100 + "\n").getBytes());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					try {
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
						return ;
					}
				}
				else
				{
					@SuppressWarnings("unused")
					int imageId ;
					float scaleRead ;
					String[] RowData = null;
					int i = 1 ;

					FileInputStream fis = null ;

					try {
						fis = openFileInput("media") ;
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return ;
					}

					BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
					//					reader = DragController.reader ;
					try {
						String line;
						while(i < DragController.getMoveNo())
						{
							line = reader.readLine() ;
							i++ ;
						}
						if ((line = reader.readLine()) != null) 
						{
							trace("Line Compared in Scale" + line) ;

							RowData = line.split(",");
							if(RowData[0].charAt(0) != 's')
							{
								if(RowData[0].charAt(0) == 'a')
									DragController.setMoveNo(DragController.getMoveNo()+1);

								scaleAbsolute(objectSelectedForScaleRotate,originalScale);
								return ;
							}

							imageId = Integer.parseInt(RowData[1]);
							scaleRead = Float.parseFloat(RowData[2]) ; 
							
							scaleRead /= 100 ;
							scaleRead *= screenMetrics.heightPixels ;

							trace("scale Read " + scaleRead) ;
							trace("height " + objectSelectedForScaleRotate.getLayoutParams().height) ;

//							if(Math.abs(objectSelectedForScaleRotate.getLayoutParams().height - scaleRead) < (0.1)*screenMetrics.heightPixels)
//							{
//								scaleAbsolute(objectSelectedForScaleRotate,originalScale);
//								return ;
//							}

							scaleAbsolute(objectSelectedForScaleRotate,scaleRead);
							DragController.setMoveNo(DragController.getMoveNo()+1);

							if(ghostMode)
							{
								playBackForGhostMode(null) ;
								//								DragController.setMoveNo(DragController.getMoveNo() + 1 );
							}
						}
						else
						{
							scaleAbsolute(objectSelectedForScaleRotate,originalScale);
						}
					}
					catch (IOException ex)
					{
					}

				}
			}
		}

	}

	@SuppressWarnings("unused")
	private class rotateListener implements SeekBar.OnSeekBarChangeListener
	{

		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// Log the progress
			Log.d("DEBUG", "Progress is: "+progress);
			float rotate = progress ;
			if(objectSelectedForScaleRotate!=null)
			{
				//				scaleRelative(objectSelectedForScaleRotate,scale);
				objectSelectedForScaleRotate.setRotation(rotate);
			}
			//set textView's text
			//            yourTextView.setText(""+progress);
		}

		public void onStartTrackingTouch(SeekBar seekBar) {}

		public void onStopTrackingTouch(SeekBar seekBar) {

			if(objectSelectedForScaleRotate != null)
			{	
				float rotate = seekBar.getProgress() ;

				if(!studentMode)
				{
					FileOutputStream fos = null;
					try {
						fos = openFileOutput("media", MODE_APPEND);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}

					try {
						fos.write(("r" + "," + objectSelectedForScaleRotate.getId() + "," + rotate + "\n").getBytes());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					try {
						fos.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

	}

	public void ghostModeToggle(View v)
	{
		boolean on = ((ToggleButton) v).isChecked();

		if(on)
		{
			ghostMode = true;
			stepMode = true ;
			findViewById(R.id.modeRadioGroup).setVisibility(View.VISIBLE) ;
			findViewById(R.id.stepModeToggle).setVisibility(View.INVISIBLE) ;
			
		}
		else
		{
			ghostMode = false ;
			stepMode = false ;
			findViewById(R.id.modeRadioGroup).setVisibility(View.INVISIBLE) ;
			findViewById(R.id.stepModeToggle).setVisibility(View.VISIBLE) ;
		}
	}

	public void insertTextBox(final Object objectDef)
	{
		final Object thisObject = this ;

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Text to be entererd");

		// Set up the input
		final EditText input = new EditText(this);
		input.setInputType(InputType.TYPE_CLASS_TEXT);

		builder.setView(input);

		// Set up the buttons
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
			@Override
			public void onClick(DialogInterface dialog, int which) {
				m_Text = input.getText().toString();
				if(!m_Text.isEmpty())
				{
					TextView tv = new TextView((Context) thisObject);
					trace(m_Text) ;
					tv.setText(m_Text);

					//				int w = 60;
					//				int h = 60;
					//				int left = 80;
					//				int top = 100;
					//				DragLayer.LayoutParams lp = new DragLayer.LayoutParams (w, h, left, top);
					mDragLayer.addView (tv);
					tv.setOnClickListener((OnClickListener) objectDef);
					tv.setOnLongClickListener((OnLongClickListener) objectDef);
					tv.setOnTouchListener((OnTouchListener) objectDef);
					tv.setId(IDGen.generateViewId());

					MyAbsoluteLayout.LayoutParams lpMove = (MyAbsoluteLayout.LayoutParams) tv.getLayoutParams();
					lpMove.x = (int)(0.3 * screenMetrics.widthPixels) ;
					lpMove.y = (int) (0.4 * screenMetrics.heightPixels) - yOffSet ;
					tv.setLayoutParams(lpMove);

					if(!studentMode)
					{
						FileOutputStream fos = null;
						try {
							fos = openFileOutput("media", MODE_APPEND);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}

						try {
							fos.write(("t" + "," + m_Text + "," + tv.getId()+ "\n").getBytes());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						try {
							fos.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

			}
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		builder.show();
	}

	private void createLine(ImageView lineImage,float x, float y, float xEnd, float yEnd, int color) {

		Bitmap bmp = Bitmap.createBitmap(lineImage.getWidth(), lineImage.getHeight(), Config.ARGB_8888);
		Canvas c = new Canvas(bmp);
		//		c.drawColor(Color.BLACK) ;
		lineImage.draw(c); 

		Paint p = new Paint();
		p.setColor(color);
		p.setStrokeWidth(5);
		p.setAntiAlias(true);
		if(studentMode)
			p.setAlpha(150);
		c.drawLine(x, y, xEnd, yEnd, p);
		lineImage.setImageBitmap(bmp);

		//		if(studentMode)
		//			lineImage.setAlpha(20);
	}

	public void deleteObject(View V)
	{
		if(objectSelectedForDelete!=null)
		{
			AlertDialog confirmBox = deleteConfirmation();
			confirmBox.show();
		}
		else
		{
			toast("No object selected for delete");
		}
	}

	private AlertDialog deleteConfirmation()
	{
		AlertDialog myQuittingDialogBox =new AlertDialog.Builder(this) 
		//set message, title, and icon
		.setTitle("Delete") 
		.setMessage("Are you sure you want to delete ?") 
		//		.setIcon(R.drawable.delete_confirmation)

		.setPositiveButton("Delete", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int whichButton) { 
				objectSelectedForDelete.setVisibility(View.GONE);

				if(!studentMode)
				{
					FileOutputStream fos = null;
					try {
						fos = openFileOutput("media", MODE_APPEND);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}

					try {
						fos.write(("d" + "," + objectSelectedForDelete.getId() + "\n").getBytes());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					try {
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				dialog.dismiss();
			}   

		})



		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();

			}
		})
		.create();
		return myQuittingDialogBox;

	}

	private AlertDialog resetConfirmation()
	{
		AlertDialog myQuittingDialogBox =new AlertDialog.Builder(this) 
		//set message, title, and icon
		.setTitle("Reset") 
		.setMessage("Are you sure you want to reset ?") 
		//		.setIcon(R.drawable.delete_confirmation)

		.setPositiveButton("Reset", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int whichButton) { 
				onCreate(null);
				lineNo = 1 ;
				mLongClickStartsDrag = false ;
				stepMode = false ;
				objectSelectedForScaleRotate = null ;
				currentTouchMode = TouchMode.MOVE ;
				ghostMode = false ;
				studentMode = true ;
				DragController.setMoveNo(0);
				fileEndReached = false ;
				deleteMode = false ;
				objectSelectedForDelete = null ;
				//				DragController.resetReader();

				if(studentMode)
					setTitle("Virtual Labs - Student Mode");
				else
					setTitle("Virtual Labs - Teacher/Admin Mode");

				FileInputStream fis = null ;

				try {
					fis = openFileInput("media") ;
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					//			e1.printStackTrace();
					toast("Error: File Not Found") ;
					trace("File Error");
					return ;
				}

				reader = new BufferedReader(new InputStreamReader(fis));
				dialog.dismiss();
			}   

		})



		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();

			}
		})
		.create();
		return myQuittingDialogBox;

	}

} // end class
