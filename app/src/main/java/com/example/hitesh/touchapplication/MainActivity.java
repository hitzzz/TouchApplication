package com.example.hitesh.touchapplication;

import android.app.DownloadManager;
import android.app.UiModeManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;

import org.w3c.dom.Text;

public class MainActivity extends Activity {

	private static final int MIN_DXDY = 2;

	// Assume no more than 20 simultaneous touches
	final private static int MAX_TOUCHES = 50;

	private static Path[] gesturePath = new Path[MAX_TOUCHES];
	private static Paint[] drawPaint = new Paint[MAX_TOUCHES];
	private int paintColor[] = {0xFFFF0000,0xFF000CFA,0xFF00EA5A,0xFFDCF70F,0xFF42D3FF,0xFFFF0000,0xFF000CFA,0xFF00EA5A,0xFFDCF70F,0xFF42D3FF,0xFFFF0000,0xFF000CFA,0xFF00EA5A,0xFFDCF70F,0xFF42D3FF,0xFFFF0000,0xFF000CFA,0xFF00EA5A,0xFFDCF70F,0xFF42D3FF,0xFFFF0000,0xFF000CFA,0xFF00EA5A,0xFFDCF70F,0xFF42D3FF,0xFFFF0000,0xFF000CFA,0xFF00EA5A,0xFFDCF70F,0xFF42D3FF,0xFFFF0000,0xFF000CFA,0xFF00EA5A,0xFFDCF70F,0xFF42D3FF,0xFFFF0000,0xFF000CFA,0xFF00EA5A,0xFFDCF70F,0xFF42D3FF,0xFFFF0000,0xFF000CFA,0xFF00EA5A,0xFFDCF70F,0xFF42D3FF,0xFFFF0000,0xFF000CFA,0xFF00EA5A,0xFFDCF70F,0xFF42D3FF};
	private static MarkerView[] current = new MarkerView[MAX_TOUCHES];
	// Pool of MarkerViews
	private static LinkedList<MarkerView> mInactiveMarkers = new LinkedList<MarkerView>();

	// Set of MarkerViews currently visible on the display
	@SuppressLint("UseSparseArrays")
	private static Map<Integer, MarkerView> mActiveMarkers = new HashMap<Integer, MarkerView>();

	protected static final String TAG = "IndicateTouchLocationActivity";
	private Canvas canvas = new Canvas();
	TextView info;
	TextView info2;
	TextView info3;
	TextView info4;
	TextView info5;



	private FrameLayout mFrame;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mFrame = (FrameLayout) findViewById(R.id.frame);
		info = (TextView)findViewById(R.id.textView);
		info2 = (TextView)findViewById(R.id.textView1);
		info3 = (TextView)findViewById(R.id.textView2);
		info4 = (TextView)findViewById(R.id.textView3);
		info5 = (TextView)findViewById(R.id.textView4);


		for(int i =0;i<MAX_TOUCHES;i++)
		{
			gesturePath[i] = new Path();
			drawPaint[i] = new Paint();
			drawPaint[i].setColor(paintColor[i]);
			drawPaint[i].setAntiAlias(true);
			drawPaint[i].setStrokeWidth(10);
			drawPaint[i].setStyle(Paint.Style.STROKE);
			drawPaint[i].setStrokeJoin(Paint.Join.ROUND);
			drawPaint[i].setStrokeCap(Paint.Cap.ROUND);
			drawPaint[i].setDither(true);
		}

		// Initialize pool of View.
		initViews();

		// Create and set on touch listener
		mFrame.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int pointerIndex = event.getActionIndex();
				int pointerID = event.getPointerId(pointerIndex);

				float x  = event.getX(pointerIndex);
				float y = event.getY(pointerIndex);

				//
				//

				if (pointerID == 0) {
					info.setText("Pointer ID = " + pointerID + "  X = " + x + "  Y = " + y);
				} else if (pointerID == 1) {
					info2.setText("Pointer ID = " + pointerID + "  X = " + x + "  Y = " + y);
				} else if (pointerID == 2) {
					info3.setText("Pointer ID = " + pointerID + "  X = " + x + "  Y = " + y);
				} else if (pointerID == 3) {
					info4.setText("Pointer ID = " + pointerID + "  X = " + x + "  Y = " + y);
				} else if (pointerID == 4) {
					info5.setText("Pointer ID = " + pointerID + "  X = " + x + "  Y = " + y);
				}

				//info.setText("PointerID="+pointerID+"X="+x+"Y="+y);

				switch (event.getActionMasked()) {

					// Show new MarkerView

					case MotionEvent.ACTION_DOWN:
					case MotionEvent.ACTION_POINTER_DOWN: {


						MarkerView marker = mInactiveMarkers.remove();

						if (null != marker) {
							mActiveMarkers.put(pointerID, marker);
							updateTouches(mActiveMarkers.size());
							marker.setXLoc(event.getX(pointerIndex));
							marker.setYLoc(event.getY(pointerIndex));
							mFrame.addView(marker);
							gesturePath[pointerID].moveTo(x, y);
							//gesturePath[pointerID].close();



						}
						break;
					}

					// Remove one MarkerView

					case MotionEvent.ACTION_UP:
					case MotionEvent.ACTION_POINTER_UP: {

						//int pointerIndex = event.getActionIndex();
						//int pointerID = event.getPointerId(pointerIndex);

						MarkerView marker = mActiveMarkers.remove(pointerID);
						//mFrame.removeView(marker);
						if ( current[pointerID] != null ) {

							mInactiveMarkers.add(marker);

							updateTouches(mActiveMarkers.size());
							gesturePath[pointerID].reset();
							marker.invalidate();


						}


						break;
					}


					// Move all currently active MarkerViews

					case MotionEvent.ACTION_MOVE: {

						for (int idx = 0; idx < event.getPointerCount(); idx++) {

							int ID = event.getPointerId(idx);

							MarkerView marker = mActiveMarkers.get(ID);
							if (null != marker && gesturePath[ID]!=null) {

								// Redraw only if finger has travel ed a minimum distance


								// Set new location
								gesturePath[ID].lineTo(event.getX(idx), event.getY(idx));
								marker.setXLoc(event.getX(idx));
								marker.setYLoc(event.getY(idx));
								marker.setID(ID);

								// Request re-draw
								marker.invalidate();

							}
						}

						break;
					}

					default:

						//Log.i(TAG, "un");
				}

				return true;
			}

			// update number of touches on each active MarkerView
			private void updateTouches(int numActive) {
				for (MarkerView marker : mActiveMarkers.values()) {
					marker.setTouches(numActive);
				}
			}
		});
	}

	private void initViews() {
		for (int idx = 0; idx < MAX_TOUCHES; idx++) {
			mInactiveMarkers.add(new MarkerView(this, -1, -1));
		}
	}

	private class MarkerView extends View {
		float mX, mY;
		int ID;
		final static private int MAX_SIZE = 400;
		int dpi = getApplicationContext().getResources().getDisplayMetrics().densityDpi;
		//radius of circle in inches
		float x = 0.3f;
		private final float touchRadius = x*(new Float(dpi));
		private int mTouches = 0;
		final private Paint mPaint = new Paint();

		public MarkerView(Context context, float x, float y) {
			super(context);
			mX = x;
			mY = y;
			mPaint.setStyle(Style.FILL);

			Random rnd = new Random();
			mPaint.setARGB(255, rnd.nextInt(256), rnd.nextInt(256),
					rnd.nextInt(256));
		}

		float getXLoc() {
			return mX;
		}

		void setXLoc(float x) {
			mX = x;
		}

		float getYLoc() {
			return mY;
		}

		void setYLoc(float y) {
			mY = y;
		}

		void setTouches(int touches) {
			mTouches = touches;
		}

		void setID(int ID)
		{
			this.ID = ID;
		}

		@Override
		protected void onDraw(Canvas canvas) {
			canvas.drawCircle(mX, mY, touchRadius, mPaint);
			canvas.drawPath(gesturePath[ID],drawPaint[ID]);
		}
	}

}