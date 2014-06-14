package com.example.omerfinaldeneme;

import java.io.IOException;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * 
 * @author KAPLANDROID
 * 
 */
public class MainActivity extends Activity   {

	String TAG = "CARTOON";
	private ImageView cartoonView;
	private SeekBar bar;

	Bitmap cartoonBMP;
	CartoonFilter cf;

	Mat output;
	Mat imgMat;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_main);

		cartoonView = (ImageView) findViewById(R.id.cartoonImg);
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this,
				mLoaderCallback);
	}

	private void init() {
		imgMat = new Mat();
		output = new Mat();

		try {
			imgMat = Utils.loadResource(getBaseContext(), R.drawable.omeryol);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		cf = new CartoonFilter();
		cf.processFrame(imgMat, output);

		Bitmap tmp = Bitmap.createBitmap(output.cols(), output.rows(),
				Bitmap.Config.ARGB_8888);
		Utils.matToBitmap(output, tmp);

		cartoonView.setImageBitmap(tmp);
	}

	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS: {
				Log.i(TAG, "OpenCV loaded successfully");
				init();
			}
				break;
			default: {
				super.onManagerConnected(status);
			}
				break;
			}
		}
	};
}
