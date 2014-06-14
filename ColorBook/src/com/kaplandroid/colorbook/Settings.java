package com.kaplandroid.colorbook;

import java.io.File;

import android.os.Environment;

/**
 * 
 * @author KAPLANDROID - Omer Faruk KAPLAN - omer@omerkaplan.com
 * 
 */
public class Settings {
	private final static String photoFolderOut = "finalapp_out";
	private final static String photoFolderName = "finalapp";
	private final static String cameraFolderName = "finalapp_camera";

	/**
	 * This is folder name used to show the saved Colored pages in the Gallery
	 * folder of the phone.
	 * 
	 * Change this if you want a different folder name.
	 */
	public static final String outputFolderPath = Environment
			.getExternalStorageDirectory().toString()
			+ File.separator
			+ photoFolderOut;

	/**
	 * picture with black edges
	 */
	public static final String inputFolderPath = Environment
			.getExternalStorageDirectory().toString()
			+ File.separator
			+ photoFolderName;

	/**
	 *  
	 */
	public static final String cameraCacheFolderPath = Environment
			.getExternalStorageDirectory().toString()
			+ File.separator
			+ cameraFolderName;

	public static final String webSite = "http://www.kaplandroid.com/";
}
