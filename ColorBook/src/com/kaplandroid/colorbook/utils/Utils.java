package com.kaplandroid.colorbook.utils;

import java.io.File;
import java.util.Locale;
import java.util.Random;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
/**
 * 
 * @author KAPLANDROID - Omer Faruk KAPLAN - omer@omerkaplan.com
 *
 */
public class Utils {

	private final static String[] imageMIMEType = { ".jpeg", ".jpg", ".png" };
	public final static String PAGES_FOLDER = "pages";
	public final static String PREFS_NAME = "ColorBookPrefs";
	public static final String MUSICONOFF = "musiconoff";

	public static enum AUDIO {
		PAUSE, STOP, PLAY
	}

	private static Random mRandom;

	/**
	 * Get a random boolean
	 */
	public static boolean getNextRandomBoolean() {

		if (mRandom == null) {
			mRandom = new Random();
		}

		return mRandom.nextBoolean();
	}

	public static int isJpgOrPng(String name) {
		for (String type : imageMIMEType) {
			if (name.toLowerCase(Locale.ENGLISH).endsWith(type)) {
				if (type.equals(imageMIMEType[0])
						|| type.equals(imageMIMEType[1]))
					return 0;
				else if (type.equals(imageMIMEType[2]))
					return 1;
			}
		}
		return -1;
	}

	/**
	 * Method to Check if the file name is Image
	 * 
	 * @param name
	 * @return
	 */
	public static boolean isImage(String name) {
		for (String type : imageMIMEType) {
			if (name.toLowerCase(Locale.ENGLISH).endsWith(type)) {
				return true;
			}
		}
		return false;
	}
 
	public static int getPageNumber(String page) {

		if (page.startsWith("page")) {
			String pageNumber = page.substring(4);
			try {
				return Integer.parseInt(pageNumber);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return -1;
			}
		}
		return -1;
	}

	public static int getWallPaperNumber(String page) {

		if (page.startsWith("wallpaper")) {
			String pageNumber = page.substring("wallpaper".length());
			try {
				return Integer.parseInt(pageNumber);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return -1;
			}
		}

		return -1;
	}

	public static boolean hasFroyo() {
		// Can use static final constants like FROYO, declared in later versions
		// of the OS since they are inlined at compile time. This is guaranteed
		// behavior.
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
	}

	public static boolean hasGingerbread() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
	}

	public static boolean hasHoneycomb() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	}

	public static boolean hasHoneycombMR1() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
	}

	public static boolean hasJellyBean() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
	}

	@SuppressLint("NewApi")
	public static boolean isExternalStorageRemovable() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			return Environment.isExternalStorageRemovable();
		}
		return true;
	}

	public static File getExternalCacheDir(Context context) {
		if (hasExternalCacheDir()) {
			return context.getExternalCacheDir();
		}

		// Before Froyo we need to construct the external cache dir ourselves
		final String cacheDir = "/Android/data/" + context.getPackageName()
				+ "/cache/";
		return new File(Environment.getExternalStorageDirectory().getPath()
				+ cacheDir);
	}

	public static boolean hasExternalCacheDir() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
	}

	/**
	 * Get a usable cache directory (external if available, internal otherwise).
	 * 
	 * @param context
	 *            The context to use
	 * @param uniqueName
	 *            A unique directory name to append to the cache dir
	 * @return The cache dir
	 */
	public static File getDiskCacheDir(Context context, String uniqueName) {
		// Check if media is mounted or storage is built-in, if so, try and use
		// external cache dir
		// otherwise use internal cache dir

		final String cachePath = Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())
				|| !Utils.isExternalStorageRemovable() ? Utils
				.getExternalCacheDir(context).getPath() : context.getCacheDir()
				.getPath();

		return new File(cachePath + File.separator + uniqueName);
	}

 
}
