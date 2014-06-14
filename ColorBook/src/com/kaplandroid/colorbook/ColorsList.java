package com.kaplandroid.colorbook;

import java.util.Vector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.kaplandroid.colorbook.Pallete.OnColorChangedListener;
import com.kaplandroid.coloringbook.R;
/**
 * 
 * @author KAPLANDROID - Omer Faruk KAPLAN - omer@omerkaplan.com
 *
 */
public class ColorsList extends BaseAdapter {

	private Vector<Integer> mColors;
	int mSelectedColor;
	OnColorChangedListener mListener;

	public ColorsList(OnColorChangedListener l) {
		mListener = l;
		mColors = new Vector<Integer>();
	}

	@Override
	public int getCount() {
		return mColors.size();
	}

	@Override
	public Object getItem(int position) {
		return mColors.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) parent.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = inflater.inflate(R.layout.color_item, null);
		ImageView imageView = (ImageView) convertView
				.findViewById(R.id.imageViewGridItem);
		imageView.setTag(mColors.get(position));
		imageView.setColorFilter(mColors.get(position));
		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mListener.colorChanged((Integer) v.getTag());
			}
		});
		return convertView;
	}

	public int getColor() {
		return mSelectedColor;
	}

	public void addColor(int color) {
		boolean isColorPresent = false;
		for (int col : mColors) {
			if (col == color)
				isColorPresent = true;
		}

		if (!isColorPresent) {
			if (mColors.size() == 9) {
				mColors.remove(8);
			}
			mColors.add(0, color);
			notifyDataSetChanged();
		}
	}
}
