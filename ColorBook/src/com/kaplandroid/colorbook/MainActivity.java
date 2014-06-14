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
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
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
		case R.id.buttonPaint: {
			Intent intent = new Intent(getApplicationContext(),
					ColorActivity.class);
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
		case R.id.btnTakePhoto: {
			currentname = "" + System.currentTimeMillis();

			// TODO
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

			String filePath = folder.listFiles()[position].getAbsolutePath();

			return ImageDetailFragment.newInstance(filePath, mColorData);
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

				mAdapter = new ImagePagerFragmentAdapter(
						getSupportFragmentManager());
				mCustomViewPager.setAdapter(mAdapter);

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

		// TODO Apply Sobel

		applySobelFilter(bm);
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

	private void applySobelFilter(Bitmap bm) {

		Mat source = new Mat();
		Mat out = new Mat();
		Mat out2 = new Mat();
		org.opencv.android.Utils.bitmapToMat(bm, source);

//		Imgproc.Sobel(source, out, CvType.CV_8U, 1, 1);
		Imgproc.Canny(source, out, 80,90);
		Core.convertScaleAbs(out, out, 10, 0);
		Imgproc.cvtColor(out, out2, Imgproc.COLOR_GRAY2BGRA, 4);


		
		org.opencv.android.Utils.matToBitmap(out, bm);


		System.out.println(bm.getPixel(44, 185));
		System.out.println(bm.getPixel(144, 15));
		
		
		File folder = new File(Settings.inputFolderPath);
		if (!folder.exists()) {
			folder.mkdir();
		}

		File photoOut = new File(folder + File.separator + currentname
				+ "_out.png");
		try {
			// TODO check for png
			photoOut.createNewFile();
			FileOutputStream ostream = new FileOutputStream(photoOut);
			bm.compress(CompressFormat.PNG, 70, ostream);
			ostream.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}
}
