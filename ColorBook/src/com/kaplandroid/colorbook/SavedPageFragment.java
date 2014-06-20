package com.kaplandroid.colorbook;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.kaplandroid.coloringbook.R;

/**
 * 
 * @author KAPLANDROID - Omer Faruk KAPLAN - omer@omerkaplan.com
 * 
 */
public class SavedPageFragment extends Fragment {

	private ImageView mImageView;

	BitmapDrawable drawable;

	private String mImagePath;

	static SavedPageFragment newInstance(String imagePath) {
		final SavedPageFragment f = new SavedPageFragment();
		
		f.mImagePath=imagePath;
		return f;
	}

	public SavedPageFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		final View view = inflater
				.inflate(R.layout.image_detail_fragment, null);
		mImageView = (ImageView) view.findViewById(R.id.fragmentImage);

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		Bitmap bitmap = BitmapFactory.decodeFile(
				mImagePath, options);
		mImageView.setImageBitmap(bitmap);
		
		
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
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

}
