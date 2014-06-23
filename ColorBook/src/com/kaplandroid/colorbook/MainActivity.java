/**
 * 
 * Main Activity. 
 * 
 * Consists of a View pager and menu to access the images put in the assest folder.
 * 
 * Swiping, previous and next image selection is done in this activity.
 * The paint button switches the image to the color activity to be drawn on.
 * 
 */

package com.kaplandroid.colorbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.kaplandroid.colorbook.utils.ImageCache;
import com.kaplandroid.colorbook.utils.Utils;
import com.kaplandroid.coloringbook.R;

/**
 * 
 * @author KAPLANDROID - Omer Faruk KAPLAN - omer@omerkaplan.com
 * 
 */
public class MainActivity extends FragmentActivity {

	private static final int REQUESTCODE_CAMERA = 77;
	String currentname;

	private final String CURRENTPAGE = "currentpage";
	private final String CURRENTPATH = "currentpath";
	private final String CACHEFOLDER = "colorpages";
	private ImagePagerFragmentAdapter mAdapter;

	ColorBookViewPager mCustomViewPager;

	ColorData mColorData;
	static SharedPreferences mSharedPreferences;

	ImageDetailFragment currentFragment;

	boolean res = true;
	static ImageCache mImageCache;

	/**
	 * Menu Views
	 */
	ImageButton mImageButtonNextPage;
	ImageButton mImageButtonPreviousPage;

	TextView tvNoMainPage;

	Button btnSavedPages;
	Button btnTakePhoto;
	private File folderCamera;

	public static int currentPage = -1;
	public static String currentFilePath = "";

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (mImageCache == null) {
			mImageCache = new ImageCache(this, CACHEFOLDER);
		}

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		mSharedPreferences = getSharedPreferences(Utils.PREFS_NAME, 0);

		tvNoMainPage = (TextView) findViewById(R.id.tvNoMainPage);
		tvNoMainPage.setVisibility(View.INVISIBLE);

		mImageButtonNextPage = (ImageButton) findViewById(R.id.imageButtonNextPage);
		mImageButtonPreviousPage = (ImageButton) findViewById(R.id.imageButtonPreviousPage);

		btnTakePhoto = (Button) findViewById(R.id.btnTakePhoto);
		btnSavedPages = (Button) findViewById(R.id.btnSaved);

		mColorData = new ColorData(this);

		mColorData.setMetrics(metrics);

		mCustomViewPager = (ColorBookViewPager) findViewById(R.id.customViewPager);
		mCustomViewPager.setPageMargin(30);
		mAdapter = new ImagePagerFragmentAdapter(getSupportFragmentManager());
		mCustomViewPager.setAdapter(mAdapter);

		setListeners();
		getSavedInstance(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt(CURRENTPAGE, currentPage);
		outState.putString(CURRENTPATH, mAdapter.getItemPath(currentPage));
		super.onSaveInstanceState(outState);
	}

	private void getSavedInstance(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			if (savedInstanceState.getInt(CURRENTPAGE) > -1) {
				mCustomViewPager.setCurrentItem(
						savedInstanceState.getInt(CURRENTPAGE), true);
				currentPage = savedInstanceState.getInt(CURRENTPAGE);
				currentFilePath = savedInstanceState.getString(CURRENTPATH);

			}
		} else {
			currentPage = 0;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return false;
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	/**
	 * MainActivity layout onclick method.
	 * 
	 * @param view
	 */
	public void onClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.imageButtonNextPage:
			if ((currentPage + 1) <= mAdapter.getCount()) {
				currentPage = currentPage + 1;
				mCustomViewPager.setCurrentItem(currentPage, true);
				currentFilePath = mAdapter.getItemPath(currentPage);
			}
			break;
		case R.id.imageButtonPreviousPage:
			if ((currentPage + 1) >= 0) {
				currentPage = currentPage - 1;
				mCustomViewPager.setCurrentItem(currentPage, true);
				currentFilePath = mAdapter.getItemPath(currentPage);
			}
			break;
		case R.id.btnPaint: {
			Intent intent = new Intent(MainActivity.this, ColorActivity.class);
			intent.putExtra("pagenumber", currentPage);
			currentFilePath = mAdapter.getItemPath(currentPage);
			startActivity(intent);
			break;
		}
		case R.id.btnSaved: {
			Intent intent = new Intent(getApplicationContext(),
					SavedPagesActivity.class);
			startActivity(intent);
			break;
		}
		case R.id.btnMainDelete: {

			if (mAdapter.isDeleteAllowed(mCustomViewPager.getCurrentItem())) {

				File itemToDelete = new File(
						mAdapter.getItemPath(mCustomViewPager.getCurrentItem()));

				itemToDelete.delete();

				refreshPager();

			} else {
				Toast.makeText(MainActivity.this,
						"You can not delete this page", Toast.LENGTH_LONG)
						.show();
			}

			break;
		}
		case R.id.btnTakePhoto: {
			currentname = "" + System.currentTimeMillis();

			// TO.DO
			folderCamera = new File(Settings.cameraCacheFolderPath);
			if (!folderCamera.exists()) {
				folderCamera.mkdir();
			}

			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			File f = new File(folderCamera, currentname + ".jpg");
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
			startActivityForResult(intent, REQUESTCODE_CAMERA);

			break;
		}
		}

	}

	/**
	 * This method sets listeners for the Seek Bar and View pager.
	 */
	public void setListeners() {

		mCustomViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				mImageButtonNextPage.setVisibility(View.VISIBLE);
				mImageButtonPreviousPage.setVisibility(View.VISIBLE);

				if (position == mAdapter.getCount() - 1) {
					mImageButtonNextPage.setVisibility(View.GONE);
				} else if (position == 0) {
					mImageButtonPreviousPage.setVisibility(View.GONE);
				}
			}

			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
			}

			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});

	}

	/**
	 * Image Pager Adapter This class populates the View pager with images using
	 * Fragments
	 * 
	 * @author KAPLANDROID
	 * 
	 */
	public class ImagePagerFragmentAdapter extends FragmentStatePagerAdapter {

		int size;
		private File folder;

		public ImagePagerFragmentAdapter(FragmentManager fm) {
			super(fm);
			// this.size = mColorData.getSize();

			folder = new File(Settings.inputFolderPath);
			if (!folder.exists()) {
				folder.mkdir();
			}

			this.size = folder.listFiles().length;

		}

		@Override
		public Fragment getItem(int position) {
			return ImageDetailFragment.newInstance(getItemPath(position),
					mColorData);
		}

		public boolean isDeleteAllowed(int position) {
			File temp = new File(getItemPath(position));
			if (temp.getName().contains("page")) {
				return false;
			} else {
				return true;
			}
		}

		public String getItemPath(int position) {
			return folder.listFiles()[position].getAbsolutePath();
		}

		@Override
		public int getCount() {
			return size;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			super.destroyItem(container, position, object);
			ImageDetailFragment fragment = (ImageDetailFragment) object;
			((ColorBookViewPager) container).removeView(fragment.getView());
			if (fragment.drawable != null)
				fragment.drawable.getBitmap().recycle();
			fragment = null;
		}

		@Override
		public Fragment instantiateItem(ViewGroup container, int position) {
			res = true;
			return (Fragment) super.instantiateItem(container, position);
		}

		@Override
		public void setPrimaryItem(ViewGroup container, int position,
				Object object) {
			super.setPrimaryItem(container, position, object);
			currentFragment = (ImageDetailFragment) object;
			currentPage = position;
			// currentFilePath = mAdapter.getItemPath(currentPage);
			if (currentPage == size) {
				res = true;
			}
		}

		@Override
		public void finishUpdate(ViewGroup container) {
			super.finishUpdate(container);
			if (currentFragment != null) {
				if (!currentFragment.imageQualityHigh)
					currentFragment.setImageRes();
				res = false;
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			if (requestCode == REQUESTCODE_CAMERA) {

				//

				prepaireOutputFile(new File(folderCamera, "" + currentname
						+ ".jpg").toString());

				//

			}

		} else {
			Toast.makeText(MainActivity.this, "error", Toast.LENGTH_LONG)
					.show();
		}
	}

	private void prepaireOutputFile(String sourcePath) {

		File photoSource = new File(sourcePath);
		Bitmap bitmap;
		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		bitmap = BitmapFactory.decodeFile(photoSource.getAbsolutePath(),
				bitmapOptions);

		double xFactor = 0;
		double width = Double.valueOf(bitmap.getWidth());
		double height = Double.valueOf(bitmap.getHeight());

		xFactor = 1024 / width;

		int Nheight = (int) ((xFactor * height));
		int NWidth = (int) (xFactor * width);

		Bitmap bm = Bitmap.createScaledBitmap(bitmap, NWidth, Nheight, true);

		// TO.DO Apply Sobel

		applyCannyFilter(bm);
	}

	@Override
	public void onResume() {
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this,
				mLoaderCallback);
	}

	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS: {
				Log.i("zz", "OpenCV loaded successfully");
				// mOpenCvCameraView.enableView();
			}
				break;
			default: {
				super.onManagerConnected(status);
			}
				break;
			}
		}
	};
	ProgressDialog progress;
	Mat out;

	private void applyCannyFilter(final Bitmap bm) {

		Mat source = new Mat();
		out = new Mat();
		// Mat out2 = new Mat();
		org.opencv.android.Utils.bitmapToMat(bm, source);

		Imgproc.Canny(source, out, 60, 70);
		Core.convertScaleAbs(out, out, 10, 0);

		addTransparentLayer(bm);

	}

	private void addTransparentLayer(final Bitmap bm) {
		progress = ProgressDialog.show(this, "Loading", "Image is in progress",
				true);
		progress.setCancelable(false);
		progress.setCanceledOnTouchOutside(false);

		progress.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface arg0) {
				refreshPager();

			}
		});

		new Thread(new Runnable() {
			@Override
			public void run() {
				out = swapBackAndWhitePixels(out);

				org.opencv.android.Utils.matToBitmap(out, bm);

				bm.setHasAlpha(true);

				int width = bm.getWidth();
				int height = bm.getHeight();
				for (int x = 0; x < width; x++) {
					for (int y = 0; y < height; y++) {
						int argb = bm.getPixel(x, y);
						if (argb == Color.WHITE) {
							bm.setPixel(x, y, Color.TRANSPARENT);
						}
					}
				}

				File folder = new File(Settings.inputFolderPath);
				if (!folder.exists()) {
					folder.mkdir();
				}

				File photoOut = new File(folder + File.separator + currentname
						+ "_out.png");
				try {
					// TO.DO check for png
					photoOut.createNewFile();
					FileOutputStream ostream = new FileOutputStream(photoOut);
					bm.compress(CompressFormat.PNG, 70, ostream);
					ostream.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (Exception e2) {
					e2.printStackTrace();
				}

				progress.dismiss();

			}
		}).start();
	}

	private Mat swapBackAndWhitePixels(Mat out) {
		for (int i = 0; i < out.height(); i++) {
			for (int j = 0; j < out.width(); j++) {

				out.put(i, j, (out.get(i, j)[0] == 0) ? 255.0 : 0.0);

			}
		}
		System.out.println("out.channels()   " + out.channels());

		return out;
	}

	private void refreshPager() {
		mAdapter = new ImagePagerFragmentAdapter(getSupportFragmentManager());
		mCustomViewPager.setAdapter(mAdapter);

		mCustomViewPager.setCurrentItem(mAdapter.getCount() - 1);

		if (mAdapter.getCount() == 0) {
			tvNoMainPage.setVisibility(View.VISIBLE);
			mCustomViewPager.setVisibility(View.INVISIBLE);
			findViewById(R.id.btnPaint).setVisibility(View.INVISIBLE);
			findViewById(R.id.btnMainDelete).setVisibility(View.INVISIBLE);
		} else {
			tvNoMainPage.setVisibility(View.INVISIBLE);
			mCustomViewPager.setVisibility(View.VISIBLE);
			findViewById(R.id.btnPaint).setVisibility(View.VISIBLE);
			findViewById(R.id.btnMainDelete).setVisibility(View.VISIBLE);
		}

	}
}
