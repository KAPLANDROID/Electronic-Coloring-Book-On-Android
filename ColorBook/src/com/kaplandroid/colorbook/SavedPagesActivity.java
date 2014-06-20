package com.kaplandroid.colorbook;

import java.io.File;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kaplandroid.coloringbook.R;

/**
 * 
 * @author KAPLANDROID
 * 
 */
public class SavedPagesActivity extends FragmentActivity {

	ViewPager pager;
	private SavedPagerFragmentAdapter adapter;
	TextView tvNoSavedPage;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_savedpages);

		tvNoSavedPage = (TextView) findViewById(R.id.tvNoSavedPage);

		pager = (ViewPager) findViewById(R.id.viewPagerSaved);
		refreshPager();

	}

	private void refreshPager() {
		adapter = new SavedPagerFragmentAdapter(getSupportFragmentManager());
		pager.setAdapter(adapter);
		if (adapter.getCount() == 0) {
			tvNoSavedPage.setVisibility(View.VISIBLE);
			pager.setVisibility(View.INVISIBLE);
			findViewById(R.id.btnSavedDelete).setVisibility(View.INVISIBLE);
		} else {
			tvNoSavedPage.setVisibility(View.INVISIBLE);
			pager.setVisibility(View.VISIBLE);
			findViewById(R.id.btnSavedDelete).setVisibility(View.VISIBLE);
		}
	}

	/**
	 * SavedPagesActivity layout onclick method.
	 * 
	 * @param view
	 */
	public void onClick(View view) {
		int id = view.getId();
		switch (id) {

		case R.id.btnSavedDelete:

			File itemToDelete = new File(adapter.getItemPath(pager
					.getCurrentItem()));

			itemToDelete.delete();

			refreshPager();
			break;
		}

	}

	/**
	 * Image Pager Adapter This class populates the View pager with images using
	 * Fragments
	 * 
	 * @author KAPLANDROID
	 * 
	 */
	public class SavedPagerFragmentAdapter extends FragmentStatePagerAdapter {

		int size;
		private File folder;

		public SavedPagerFragmentAdapter(FragmentManager fm) {
			super(fm);
			// this.size = mColorData.getSize();

			folder = new File(Settings.outputFolderPath);
			if (!folder.exists()) {
				folder.mkdir();
			}

			this.size = folder.listFiles().length;

		}

		@Override
		public Fragment getItem(int position) {

			String filePath = folder.listFiles()[position].getAbsolutePath();

			return SavedPageFragment.newInstance(filePath);
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
			SavedPageFragment fragment = (SavedPageFragment) object;
			((ViewPager) container).removeView(fragment.getView());
			if (fragment.drawable != null)
				fragment.drawable.getBitmap().recycle();
			fragment = null;
		}

		@Override
		public Fragment instantiateItem(ViewGroup container, int position) {
			return (Fragment) super.instantiateItem(container, position);
		}

		@Override
		public void setPrimaryItem(ViewGroup container, int position,
				Object object) {
			super.setPrimaryItem(container, position, object);
		}

		@Override
		public void finishUpdate(ViewGroup container) {
			super.finishUpdate(container);
		}
	}

}
