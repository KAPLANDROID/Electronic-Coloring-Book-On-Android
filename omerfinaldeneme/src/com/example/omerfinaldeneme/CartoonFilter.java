package com.example.omerfinaldeneme;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class CartoonFilter {

	float sp;
	float sr;

	Mat gray, edges;
	Mat hsv;
	Mat bgr, img0;
	Mat edgesBgr;

	public CartoonFilter() {
		gray = new Mat();
		edges = new Mat();
		hsv = new Mat();
		bgr = new Mat();
		img0 = new Mat();
		edgesBgr = new Mat();

		sp = Math.max(1, Math.max(1, 15));
		sr = Math.max(1, Math.max(1, 40));
	}

	public void changeCartoonParameter(float spMax, float srMax) {
		sp = Math.max(1, Math.max(1, spMax));
		sr = Math.max(1, Math.max(1, srMax));
	}

	boolean processFrame(Mat inputFrame, Mat outputFrame) {
		Imgproc.cvtColor(inputFrame, bgr, Imgproc.COLOR_BGRA2BGR);
		Imgproc.pyrMeanShiftFiltering(bgr.clone(), bgr, sp, sr);

		getGray(bgr, gray);
		Imgproc.Canny(gray, edges, 150, 150);

		Imgproc.cvtColor(edges, edgesBgr, Imgproc.COLOR_GRAY2BGR);

		// bgr = bgr - edgesBgr;
		Core.subtract(bgr, edgesBgr, bgr);

		Imgproc.cvtColor(bgr, outputFrame, Imgproc.COLOR_BGR2BGRA);
		return true;
	}

	void getGray(Mat input, Mat gray) {
		int numChannes = input.channels();

		if (numChannes == 4) {
			Imgproc.cvtColor(input, gray, Imgproc.COLOR_BGRA2GRAY);

		} else if (numChannes == 3) {
			Imgproc.cvtColor(input, gray, Imgproc.COLOR_BGR2GRAY);
		} else if (numChannes == 1) {
			gray = input;
		}
	}

}