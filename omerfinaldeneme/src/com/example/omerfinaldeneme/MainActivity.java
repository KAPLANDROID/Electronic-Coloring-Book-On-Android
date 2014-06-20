package com.example.omerfinaldeneme;

import java.io.IOException;
import java.util.List;
import java.util.ListIterator;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvException;
import org.opencv.core.Mat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity implements CvCameraViewListener2 {
	private static final String TAG = "Activity";

	private MainView mOpenCvCameraView;
	private List<Size> mResolutionList;
	ImageView resultImageView, previewImageView, backgroundImageView;

	public Button btnBackGroundTake, btnShowDiff;
	public boolean takeBackGroundFlag, showDiffFlag;
	public Mat mBackGroundCamera, resultMatrix;
	public Mat matBackGroundRes;
	Bitmap bmpBackGroundCamera;

	Size minCameraResolution;
	int minCameraResolutionWidth = 999000;

	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS: {
				Log.i(TAG, "OpenCV loaded successfully");
				mOpenCvCameraView.enableView();

				try {
					// Arka tarafta gosterilecek olan dunya resmi matrix olarak
					// hafizaya aliniyor
					matBackGroundRes = Utils.loadResource(MainActivity.this,
							R.drawable.bg);
				} catch (IOException e1) {
					e1.printStackTrace();
				}

			}
				break;
			default: {
				super.onManagerConnected(status);
			}
				break;
			}
		}
	};

	public MainActivity() {
		Log.i(TAG, "Instantiated new " + this.getClass());
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "called onCreate");
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.surface_view);
		resultImageView = (ImageView) findViewById(R.id.resultImageView);
		previewImageView = (ImageView) findViewById(R.id.previewImageView);
		backgroundImageView = (ImageView) findViewById(R.id.backgroundImageView);
		mOpenCvCameraView = (MainView) findViewById(R.id.tutorial3_activity_java_surface_view);
		btnBackGroundTake = (Button) findViewById(R.id.btnBackGroundTake);
		btnShowDiff = (Button) findViewById(R.id.btnShowDiff);
		mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

		mOpenCvCameraView.setCvCameraViewListener(this);

		btnBackGroundTake.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				takeBackGroundFlag = true;
				new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						takeBackGroundFlag = false;
					}
				}).start();
			}
		});

		// Isleme Basla butonunun eventi
		btnShowDiff.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (mBackGroundCamera != null) {
					// Oncelikle forground un saklanacagi matrix olusturuluyor.
					if (resultMatrix == null)
						resultMatrix = Mat.ones(mBackGroundCamera.height(),
								mBackGroundCamera.width(),
								mBackGroundCamera.type());

					showDiffFlag = !showDiffFlag;
				} else {
					Toast.makeText(
							MainActivity.this,
							"Oncelikle \"Arkaplan resmini cek\" butonu ile\narkaplan resmini cekin",
							Toast.LENGTH_LONG).show();
				}

				if (showDiffFlag == true) {
					btnShowDiff.setText("Islemi durdur");
				} else {
					btnShowDiff.setText("Isleme basla");
				}

			}
		});

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		// set title
		alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_info);
		alertDialogBuilder.setTitle("Nasil Kullanilir ?");
		// set dialog message
		alertDialogBuilder
				.setMessage(
						"Oncelikle sag ust koseden arkaplan resmini cekin sonrasinda isleme basla diyerek sonucu ekranda gorun."+
					"\n\nSonuc cihaz kapasitesinden dolayi GrayScale olarak gosterilecektir."+
						"\n\n Omer KAPLAN")
				.setCancelable(false)
				.setPositiveButton("Tamam",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
							}
						});
		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();

	}

	@Override
	public void onPause() {
		super.onPause();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}

	@Override
	public void onResume() {
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this,
				mLoaderCallback);

		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				mResolutionList = mOpenCvCameraView.getResolutionList();
				ListIterator<Size> resolutionItr = mResolutionList
						.listIterator();
				while (resolutionItr.hasNext()) {
					Size element = resolutionItr.next();
					if (element.width < minCameraResolutionWidth) {
						minCameraResolutionWidth = element.width;
						minCameraResolution = element;
					}
				}
				mOpenCvCameraView.setResolution(minCameraResolution);

			}
		}).start();

	}

	public void onDestroy() {
		super.onDestroy();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}

	public void onCameraViewStarted(int width, int height) {
	}

	public void onCameraViewStopped() {
	}

	/**
	 * Kameradan her goruntu alindiginda buraya aktarilir.
	 */
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

		// /////////////// Sol ustteki gercek kamera goruntusu olusturuluyor
		final Bitmap bmp = Bitmap.createBitmap(inputFrame.rgba().width(),
				inputFrame.gray().height(), Bitmap.Config.ARGB_8888);
		Mat tmp = inputFrame.rgba();
		try {
			Utils.matToBitmap(tmp, bmp);
		} catch (CvException e) {
			Log.d("Exception", e.getMessage());
		}

		MainActivity.this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				previewImageView.setImageBitmap(bmp);

			}
		});
		// //////////////////////////

		/**
		 * Arkaplan resmini cek butonuna tiklandiginda buraya girer ve matrix
		 * olusturulur.
		 */
		if (takeBackGroundFlag) {
			mBackGroundCamera = inputFrame.gray().clone();
			bmpBackGroundCamera = Bitmap.createBitmap(
					mBackGroundCamera.width(), mBackGroundCamera.height(),
					Bitmap.Config.ARGB_8888);

		}

		if (bmpBackGroundCamera != null) {
			try {
				Utils.matToBitmap(mBackGroundCamera, bmpBackGroundCamera);
			} catch (CvException e) {
				Log.d("Exception", e.getMessage());
			}
			MainActivity.this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					backgroundImageView.setImageBitmap(bmpBackGroundCamera);

				}
			});
		}
		// //////////////////////

		/**
		 * Isleme Basla butonuna tiklandiginda Hesaplama baslar
		 * 
		 * Threshold olarak 50 yaptim degistirilebilir
		 */
		int threshold = 50;
		if (showDiffFlag) {
			for (int i = 0; i < tmp.height(); i++) {
				for (int j = 0; j < tmp.width(); j++) {
					double diff = Math.abs(tmp.get(i, j)[0]
							- mBackGroundCamera.get(i, j)[0]);
					if (diff > threshold) {
						resultMatrix.put(i, j, inputFrame.rgba().get(i, j)[0]);
					} else {
						// resultMatrix.put(i, j,matBackGroundRes.get(i, j)) ;
						resultMatrix.put(i, j, matBackGroundRes.get(i, j)[0]);
					}
				}
			}
			final Bitmap bmpDiff = Bitmap.createBitmap(
					mBackGroundCamera.width(), mBackGroundCamera.height(),
					Bitmap.Config.ARGB_8888);
			try {
				Utils.matToBitmap(resultMatrix, bmpDiff);
			} catch (CvException e) {
				Log.d("Exception", e.getMessage());
			}

			MainActivity.this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					resultImageView.setImageBitmap(bmpDiff);
				}
			});
		}
		// /////////////////////////////////////////////

		return inputFrame.gray();
	}

}