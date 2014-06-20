/**
 * This class contains the method and functionality to draw on an image.
 * 
 * Save the drawn image
 */

package com.kaplandroid.colorbook;

import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

import com.kaplandroid.colorbook.ColorData.QUALITY;
import com.kaplandroid.colorbook.utils.ImageCache;

class Pallete {

	public interface OnColorChangedListener {
		public void colorChanged(int color);
	}

	public interface OnBrushSizeChangedListener {
		public void brushSizeChanged(int size);
	}

}

/**
 * 
 * @author KAPLANDROID - Omer Faruk KAPLAN - omer@omerkaplan.com
 * 
 */
public class ColorPageView extends View implements OnTouchListener {

	public Paint mPaint;

	private Bitmap mBitmap;
	private Canvas mCanvas;
	private Path mPath;
	private Paint mBitmapPaint;
	Bitmap bitmap;
	Bitmap bitmapLayer;
	Bitmap mColorLayerBitmap;
	boolean showLayer = false;
	boolean isJpg = false;
	boolean erase = false;
	boolean clear = false;

	ColorData mColorData;
	int mPageNum = 0;
	Context mContext;

	private int mWidth;
	private int mHeight;
	private int mBitmapWidth;
	private int mBitmapHeight;
	private int cX, cY;

	private ImageCache mImageCache;

	public ColorPageView(Context c, AttributeSet attr) {
		super(c, attr);

		mContext = c;
		setPaint();
		mPath = new Path();
		if (!isInEditMode()) {
			mColorData = new ColorData(c);
			mColorData.setMetrics(c.getResources().getDisplayMetrics());
		}
	}

	public void colorChanged(int color) {
		mPaint.setXfermode(null);
		mPaint.setColor(color);
	}

	public void setEraser() {
		mPaint.setAlpha(0xFF);
		mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		erase = true;
	}

	public void setPageNumber(int pageNum) {
		mPageNum = pageNum;
	}

	public float getBrushSize() {
		return mPaint.getStrokeWidth();
	}

	public void setBrushSize(float size) {
		mPaint.setStrokeWidth(size);
	}

	private void setPaint() {
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(0xFFFFFF00);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(12);
		mBitmapPaint = new Paint(Paint.DITHER_FLAG);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (!isInEditMode()) {
			mBitmap = mColorData.decodeBitmap(MainActivity.currentFilePath,
					getWidth(), getHeight(), QUALITY.PAINT);
			mBitmapWidth = mBitmap.getWidth();
			mBitmapHeight = mBitmap.getHeight();

			isJpg = !mBitmap.hasAlpha();

		}

		mWidth = getWidth();
		mHeight = getHeight();

		cX = (mWidth - mBitmapWidth) / 2;
		cY = (mHeight - mBitmapHeight) / 2;

		mColorLayerBitmap = getBitmap(mPageNum);
		mCanvas = new Canvas(mColorLayerBitmap);
		// mCanvas.drawColor(Color.WHITE);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.WHITE);

		if (!isInEditMode()) {
			if (!isJpg) {
				canvas.drawBitmap(mColorLayerBitmap, 0, 0, mBitmapPaint);
				canvas.drawPath(mPath, mPaint);
				canvas.drawBitmap(mBitmap, cX, cY, mBitmapPaint);
			} else {
				canvas.drawBitmap(mBitmap, cX, cY, mBitmapPaint);
				canvas.drawBitmap(mColorLayerBitmap, 0, 0, mBitmapPaint);
				canvas.drawPath(mPath, mPaint);
			}

			if (showLayer) {
				canvas.drawBitmap(bitmapLayer, cX, cY, mBitmapPaint);
			}
		}
	}

	private float mX, mY;
	private static final float TOUCH_TOLERANCE = 4;
	private boolean touchMove = false;

	/**
	 * Method called when the finger is first touched down.
	 * 
	 * @param x
	 * @param y
	 */
	private void touch_start(float x, float y) {
		mPath.reset();
		mPath.moveTo(x, y);
		mX = x;
		mY = y;
		touchMove = false;
	}

	/**
	 * Method is called after the finger is down and moving.
	 * 
	 * @param x
	 * @param y
	 */
	private void touch_move(float x, float y) {
		float dx = Math.abs(x - mX);
		float dy = Math.abs(y - mY);
		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
			mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
			mX = x;
			mY = y;
		}
		touchMove = true;
		if (erase) {
			touch_up();
			touch_start(x, y);
		}
	}

	/**
	 * Method called when the finger is lifted after touching the device.
	 */
	private void touch_up() {

		if (erase || !touchMove) {
			mCanvas.drawPoint(mX, mY, mPaint);
		}
		mPath.lineTo(mX, mY);
		mCanvas.drawPath(mPath, mPaint);
		mPath.reset();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			touch_start(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			touch_move(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			touch_up();
			invalidate();
			break;
		}

		return true;
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		return false;
	}

	/**
	 * Method to Clear Canvas which has been drawn. Refresh Canvas.
	 */
	public void clearCanvas() {
		clear = true;
		onSizeChanged(getWidth(), getHeight(), mBitmap.getWidth(),
				mBitmap.getHeight());
		invalidate();
	}

	/**
	 * Method to show layer over drawings.
	 */
	public void showAnswer(boolean show) {
		if (bitmapLayer != null) {
			showLayer = show;
			bitmapLayer = Bitmap.createScaledBitmap(bitmapLayer, getWidth(),
					getHeight(), true);
			invalidate();
		}
	}

	/**
	 * Method to save Color Pages to Gallery folder.
	 */
	public void saveColorPage() {
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {

			File colorPagesFolder = new File(Settings.outputFolderPath);

			if (!colorPagesFolder.exists())
				colorPagesFolder.mkdir();

			try {
				Calendar cal = Calendar.getInstance();
				String append = "kaplanOut" + cal.get(Calendar.DAY_OF_MONTH)
						+ cal.get(cal.get(Calendar.MONTH))
						+ cal.get(Calendar.YEAR) + cal.get(Calendar.HOUR)
						+ cal.get(Calendar.MINUTE) + cal.get(Calendar.SECOND)
						+ mPageNum;

				File file = new File(colorPagesFolder.getAbsolutePath()
						+ File.separator + append + ".jpg");
				file.createNewFile();
				FileOutputStream out = new FileOutputStream(file);
				Bitmap saveBitmap = Bitmap.createBitmap(mWidth, mHeight,
						Config.ARGB_8888);
				Canvas canvas = new Canvas(saveBitmap);
				canvas.drawColor(Color.WHITE);
				if (isJpg) {
					canvas.drawBitmap(mBitmap, cX, cY, mBitmapPaint);
					canvas.drawBitmap(mColorLayerBitmap, 0, 0, mBitmapPaint);
				} else {
					canvas.drawBitmap(mColorLayerBitmap, 0, 0, mBitmapPaint);
					canvas.drawBitmap(mBitmap, cX, cY, mBitmapPaint);
				}
				saveBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
				mContext.sendBroadcast(new Intent(
						Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri
								.parse("file://"// //
										+ Environment
												.getExternalStorageDirectory()
										+ file.getAbsolutePath())));
				Toast.makeText(mContext, "Image saved to Gallery",
						Toast.LENGTH_SHORT).show();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public Bitmap getBitmap(Object data) {
		if (data == null) {
			return null;
		}

		final String dataString = String.valueOf(data);
		Bitmap bitmap = null;

		if (mImageCache != null && !clear) {
			bitmap = mImageCache.getBitmapFromDiskCache(dataString);

			if (bitmap != null)
				return bitmap.copy(Config.ARGB_8888, true);

			bitmap = mImageCache.getBitmapFromMemCache(dataString);
			if (bitmap != null)
				return bitmap.copy(Config.ARGB_8888, true);
			clear = false;
		}
		return createBitmap(mPageNum);
	}

	private Bitmap createBitmap(Object data) {
		if (data == null) {
			return null;
		}

		Bitmap bitmap = Bitmap.createBitmap(mWidth, mHeight, Config.ARGB_8888);
		bitmap.eraseColor(Color.TRANSPARENT);
		Canvas canvas = new Canvas(bitmap);
		canvas.drawColor(Color.TRANSPARENT);
		return bitmap;
	}

	/**
	 * 
	 * @param imageCache
	 */
	public void setImageCache(ImageCache imageCache) {
		mImageCache = imageCache;
	}

	public void addBitmapToCache() {
		final String dataString = String.valueOf(mPageNum);
		mImageCache.addBitmapToCache(dataString, mColorLayerBitmap);
	}

	protected void initDiskCacheInternal() {
		if (mImageCache != null) {
			mImageCache.initDiskCache();
		}
	}

	protected void clearCacheInternal() {
		if (mImageCache != null) {
			mImageCache.clearCache();
		}
	}

	protected void flushCacheInternal() {
		if (mImageCache != null) {
			mImageCache.flush();
		}
	}

	protected void closeCacheInternal() {
		if (mImageCache != null) {
			mImageCache.close();
			mImageCache = null;
		}
	}

}
