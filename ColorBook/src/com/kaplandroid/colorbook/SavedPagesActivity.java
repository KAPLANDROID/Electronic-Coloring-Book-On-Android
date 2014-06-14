package com.kaplandroid.colorbook;

import java.io.File;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.kaplandroid.coloringbook.R;

/**
 * 
 * @author KAPLANDROID
 * 
 */
public class SavedPagesActivity extends FragmentActivity {

	// llSavedScroll

	LinearLayout llSavedScroll;
	private File folder;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_savedpages);

		llSavedScroll = (LinearLayout) findViewById(R.id.llSavedScroll);

		folder = new File(Settings.outputFolderPath);

		int count = folder.listFiles().length;

		System.out.println(count);

		for (int i = 0; i < count; i++) {

			ImageView iv = new ImageView(this);
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;
			Bitmap bitmap = BitmapFactory.decodeFile(
					folder.listFiles()[i].toString(), options);
			iv.setImageBitmap(bitmap);

			llSavedScroll.addView(iv);
		}

	}

}
