/**
 * 
 * Color Activity.
 * 
 * When the user selects the drawing page by clicking on the paint button. The users gets to Color activity.
 * Here we put the image in a customview to be drawn on.
 * We have a Pallete for color selection and eraser selection.
 * All the functionality of the page coloring is lead thru here.
 * 
 * 
 * 
 */

package com.kaplandroid.colorbook;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.kaplandroid.coloringbook.R;

/**
 * 
 * @author KAPLANDROID - Omer Faruk KAPLAN - omer@omerkaplan.com
 * 
 */
public class ColorActivity extends Activity implements
		Pallete.OnColorChangedListener, Pallete.OnBrushSizeChangedListener {

	ColorPageView mCustomView;
	public static int mColor = Color.BLACK;
	SlidingLayer mSlidingLayer;

	ColorPickerView mColorPickerView;
	SeekBar mSeekBarBrushSize;
	GridView mGridViewRecentColors;
	ColorsList mColorsList;
	TextView mTextViewBrushSize;

	int defaultBrushSõze = 12;

	ImageButton mImageButtonEraser, mImageButtonBrush;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_color);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {

			mCustomView = (ColorPageView) findViewById(R.id.customView);
			mCustomView.colorChanged(mColor);

			mCustomView.setImageCache(MainActivity.mImageCache);

			mSlidingLayer = (SlidingLayer) findViewById(R.id.slidingLayer1);
			mSlidingLayer.setSlidingEnabled(true);
			mSlidingLayer.setSlidingFromShadowEnabled(true);
			mSlidingLayer.setShadowDrawable(R.drawable.divider);

			mColorPickerView = (ColorPickerView) findViewById(R.id.colorPickerView);
			mColorPickerView.setColor(mColor);
			mColorPickerView.setOnColorChangeListener(this);

			mSeekBarBrushSize = (SeekBar) findViewById(R.id.seekBarBrushSize);
			mSeekBarBrushSize.setProgress(defaultBrushSõze);
			mTextViewBrushSize = (TextView) findViewById(R.id.textViewBrushSize);
			mTextViewBrushSize.setText("" + defaultBrushSõze);

			mGridViewRecentColors = (GridView) findViewById(R.id.gridViewRecentColor);
			mColorsList = new ColorsList(this);
			mGridViewRecentColors.setAdapter(mColorsList);

			mImageButtonBrush = (ImageButton) findViewById(R.id.imageButtonBrush);
			mImageButtonEraser = (ImageButton) findViewById(R.id.imageButtonEraser);
			mImageButtonBrush.setSelected(true);

			setListeners();

			mCustomView.setPageNumber(bundle.getInt("pagenumber"));
		} else {
			finish();
		}
	}
 
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	/**
	 * Method to set listeners for Brush Size seekbar
	 */
	private void setListeners() {
		mSeekBarBrushSize
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						mCustomView.setBrushSize(seekBar.getProgress());
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
					}

					@Override
					public void onProgressChanged(SeekBar seekBar, int arg1,
							boolean arg2) {
						mTextViewBrushSize.setText(seekBar.getProgress() + "");
					}
				});

	}

	/**
	 * Color activity pallete on Click method.
	 * 
	 * @param view
	 */

	public void onClickPallete(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.textViewClear:
			mCustomView.clearCanvas();
			mSlidingLayer.closeLayer(true);
			break;
		case R.id.textViewSave:
			mCustomView.saveColorPage();
			mSlidingLayer.closeLayer(true);
			break;
		case R.id.textViewDone:
			mColorsList.addColor(mColor);
			mSlidingLayer.closeLayer(true);
			break;
		case R.id.buttonOpenLayer:
			if (!mSlidingLayer.isOpened()) {
				mSlidingLayer.openLayer(true);
			} else if (mSlidingLayer.isOpened()) {
				mSlidingLayer.closeLayer(true);
			}
			break;
		case R.id.imageButtonEraser:
			mCustomView.setEraser();
			mImageButtonEraser.setSelected(true);
			mImageButtonBrush.setSelected(false);
			break;
		case R.id.imageButtonBrush:
			mCustomView.colorChanged(mColor);
			mImageButtonBrush.setSelected(true);
			mImageButtonEraser.setSelected(false);
			break;
		default:
			break;
		}
	}

	@Override
	public void colorChanged(int color) {
		mColor = color;
		mCustomView.colorChanged(color);
	}

	@Override
	public void brushSizeChanged(int size) {
		mCustomView.setBrushSize(size);
	}

	@Override
	protected void onPause() {
		super.onPause();

		mCustomView.addBitmapToCache();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

}
