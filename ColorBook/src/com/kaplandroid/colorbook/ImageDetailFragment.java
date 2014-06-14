package com.kaplandroid.colorbook;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;

import com.kaplandroid.colorbook.ColorData.QUALITY;
import com.kaplandroid.coloringbook.R;
/**
 * 
 * @author KAPLANDROID - Omer Faruk KAPLAN - omer@omerkaplan.com
 *
 */
public class ImageDetailFragment extends Fragment {

	private static final String IMAGE_DATA_EXTRA = "resId";
	private String mImageNum;
	ImageView mImageView;
	Button buttonSetAsWallPaper;
	boolean imageQualityHigh = false;
	ProgressBar imageProgress;

	ImageAsyncTask task;

	BitmapDrawable drawable;
	static ColorData mColorData;

	static ImageDetailFragment newInstance(String imageNum, ColorData colorData) {
		final ImageDetailFragment f = new ImageDetailFragment();
		final Bundle args = new Bundle();
		args.putString(IMAGE_DATA_EXTRA, imageNum);
		f.setArguments(args);
		mColorData = colorData;
		return f;
	}

	public ImageDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mImageNum = getArguments() != null ? getArguments().getString(
				IMAGE_DATA_EXTRA) : null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		final View view = inflater
				.inflate(R.layout.image_detail_fragment, null);
		mImageView = (ImageView) view.findViewById(R.id.fragmentImage);
	
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		if (MainActivity.class.isInstance(getActivity())) {

			if (mColorData != null) {
				if (!mImageNum.equalsIgnoreCase( MainActivity.currentFilePath)) {
					task = new ImageAsyncTask(QUALITY.LOW);
					task.execute(mImageNum);
					imageQualityHigh = false;
				} else {
					task = new ImageAsyncTask(QUALITY.HIGH);
					task.execute(mImageNum);
					imageQualityHigh = true;
				}
			}
		}
		super.onActivityCreated(savedInstanceState);
	}

	public void setImageRes() {
		if (!imageQualityHigh) {
			task = new ImageAsyncTask(QUALITY.HIGH);
			task.execute(mImageNum);
			imageQualityHigh = true;
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mImageView.setImageDrawable(null);
	}

	@Override
	public void onDetach() {
		super.onDetach();

	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	public class ImageAsyncTask extends AsyncTask<String, Void, Drawable> {

		QUALITY mQuality;
		

		public ImageAsyncTask(QUALITY quality) {
			mQuality = quality;
			
		}

		@Override
		protected void onPreExecute() {
//			if (mQuality != QUALITY.LOW) {
//				imageProgress = (ProgressBar) getActivity().findViewById(R.id.progressBarImage);
//				imageProgress.setVisibility(View.VISIBLE);
//			}
			super.onPreExecute();
		}

		@Override
		protected Drawable doInBackground(String... params) {
			if (isAdded())
				drawable = new BitmapDrawable(getResources(),
						mColorData.decodeBitmap(mImageNum, 100 ,100, mQuality));
			return drawable;
		}

		@Override
		protected void onPostExecute(Drawable result) {
			if (result != null) {
				mImageView.setImageDrawable(result);
				mImageView.setScaleType(ScaleType.FIT_CENTER);
				if(imageProgress != null)
					imageProgress.setVisibility(View.GONE);
			}
			super.onPostExecute(result);
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}
	}

}
