package com.kaplandroid.colorbook;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
/**
 * 
 * @author KAPLANDROID - Omer Faruk KAPLAN - omer@omerkaplan.com
 *
 */
public class ColorBookViewPager extends ViewPager{
	
	private boolean isPagingEnabled = true;
	
	public ColorBookViewPager(Context context) {
		super(context);
	}
	public ColorBookViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		if(isPagingEnabled)
			return super.onTouchEvent(arg0);
		
		return false;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		try {
			if(this.isPagingEnabled)
				return super.onInterceptTouchEvent(event);
			
			return false;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return false;
		} catch(ArrayIndexOutOfBoundsException e){
			return false;
		}
	}
	
	public void setPagingEnabled(boolean page) {
        this.isPagingEnabled = page;
    }
}
