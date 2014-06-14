package com.kaplandroid.colorbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.DisplayMetrics;
import android.util.Log;

import com.kaplandroid.colorbook.utils.Utils;

/**
 * 
 * @author KAPLANDROID - Omer Faruk KAPLAN - omer@omerkaplan.com
 * 
 */
public class ColorData {

	private class ColorPage {

		private int pageNumber;
		private String imageUrl;

		public int getPageNumber() {
			return pageNumber;
		}

		public void setPageNumber(int pageNumber) {
			this.pageNumber = pageNumber;
		}

		public String getImageUrl() {
			return imageUrl;
		}

		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
		}
	}

	public enum QUALITY {
		LOW, HIGH, PAINT
	}

	private final String TAG = "ColorData";

	private static int MAX_HEIGHT = 2000;
	private static int MAX_WIDTH = 2000;

	private String mBackGroundMusic = null;

	ArrayList<ColorPage> mColorPages = new ArrayList<ColorPage>();
	Context mContext;

	private DisplayMetrics mDisMetrics;

	public void setMetrics(DisplayMetrics metrics) {
		this.mDisMetrics = metrics;
	}

	public ColorData(Context context) {
		mContext = context;
		getFilesFromAssets();
	}

	/**
	 * Method to get data from assets folder images and text files
	 */
	@SuppressLint("DefaultLocale")
	private void getFilesFromAssets() {
		try {
			if (mContext.getAssets().list("") != null) {
				String[] folders = mContext.getAssets().list("");
				for (String folder : folders) {
					if (folder.toLowerCase().equals(Utils.PAGES_FOLDER)) {
						getImages(folder);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			Log.d(TAG,
					"Folder does not exist or Invalid folder name. (images or text)");
		}
	}

	/**
	 * Matching the existing names. Method to store image name in the cards
	 * array
	 * 
	 * @param folder
	 */
	private void getImages(String folder) {
		try {
			int index = -1;
			String[] files = mContext.getAssets().list(folder);
			for (String file : files) {
				if (Utils.isImage(file)) {
					index = -1;
					String name = file.substring(0, file.indexOf('.'));
					int num = Utils.getPageNumber(name);
					if (num >= 0) {
						for (int i = 0; i < mColorPages.size(); i++) {
							if (mColorPages.get(i).getPageNumber() == num) {
								index = i;
							}
						}

						if (index < 0) {
							ColorPage cPage = new ColorPage();
							cPage.setPageNumber(num);
							cPage.setImageUrl(file);
							mColorPages.add(cPage);
						} else {
							mColorPages.get(index).setImageUrl(file);
						}
					} else {
						Log.d(TAG, "Invalid Page Number");
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
			Log.d(TAG, "Images do not exist");
		}
	}

	private ColorPage getComicIndex(int position) {
		for (ColorPage cPage : mColorPages) {
			if (cPage.getPageNumber() == position + 1) {
				return cPage;
			}
		}

		return null;
	}

	public int getPageType(int position) {
		for (ColorPage cPage : mColorPages) {
			if (cPage.getPageNumber() == position + 1) {
				return Utils.isJpgOrPng(cPage.getImageUrl());
			}
		}
		return -1;
	}

	public InputStream getImageFromAsset(int position) {
		try {
			if (getComicIndex(position) != null) {
				InputStream imageStream = mContext.getAssets().open(
						Utils.PAGES_FOLDER + "/"
								+ getComicIndex(position).getImageUrl());
				return imageStream;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressLint("NewApi")
	public synchronized Bitmap decodeBitmap(String imageNum, int width,
			int height, QUALITY quality) {
		final BitmapFactory.Options options = new BitmapFactory.Options();

		options.inJustDecodeBounds = true;
		options.inPurgeable = true;
		options.inSampleSize = calculateBitmapSize(options.outWidth,
				options.outHeight, quality);

		options.inJustDecodeBounds = false;
		if (quality == QUALITY.PAINT) {
			Bitmap b = BitmapFactory.decodeFile(imageNum, options);
			int bWidth = b.getWidth();
			int bHeight = b.getHeight();

			float scaleWidth = ((float) width) / bWidth;
			float scaleHeight = ((float) height) / bHeight;
			float scale = (scaleWidth <= scaleHeight) ? scaleWidth
					: scaleHeight;
			Matrix matrix = new Matrix();
			matrix.postScale(scale, scale);

			return Bitmap.createBitmap(b, 0, 0, bWidth, bHeight, matrix, true);
		} else
			return BitmapFactory.decodeFile(imageNum, options);
	}

	/**
	 * Method to the set the calculate the actual bitmap size in the file
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public synchronized int calculateBitmapSize(int reqWidth, int reqHeight,
			QUALITY quality) {

		boolean landscape = reqWidth > reqHeight;

		int maxWidth = mDisMetrics.widthPixels + 600;
		int maxHeight = mDisMetrics.heightPixels + 300;

		if (quality == QUALITY.HIGH) {
			maxHeight = MAX_HEIGHT;
			maxWidth = MAX_WIDTH;
		} else if (quality == QUALITY.LOW) {
			maxHeight = 600;
			maxWidth = 600;
		}

		int newWidth;
		if (landscape) {
			float ratio = (float) reqHeight / (float) reqWidth;
			float containerRatio = (float) maxHeight / (float) maxWidth;
			if (ratio < containerRatio) {
				newWidth = maxWidth;
			} else {
				newWidth = reqWidth * maxHeight / reqHeight;
			}
		} else {
			float ratio = (float) reqWidth / (float) reqHeight;
			float containerRatio = (float) maxWidth / (float) maxHeight;
			if (ratio < containerRatio) {
				newWidth = reqWidth * maxHeight / reqHeight;
			} else {
				newWidth = maxWidth;
			}
		}
		float sampleSizeF = (float) reqWidth / (float) newWidth;
		int sampleSize = (int) Math.round(Math.ceil(sampleSizeF));
		return sampleSize;
	}

	public String getBackgroundMusicFile() {
		return mBackGroundMusic;
	}

	public int getSize() {
		return mColorPages.size();
	}

}
