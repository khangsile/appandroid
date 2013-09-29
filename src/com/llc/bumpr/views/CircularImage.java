package com.llc.bumpr.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CircularImage extends ImageView {
	
	public CircularImage(Context context){
		super(context);
	}
	
	//Are these two required??
	public CircularImage(Context context, AttributeSet attrSet){
		super(context, attrSet);
	}
	
	public CircularImage(Context context, AttributeSet attrSet, int style){
		super(context, attrSet, style);
	}
	
	@Override
	protected void onDraw(Canvas canvas){
	    Drawable drawable = getDrawable();

	    if (drawable == null) {
	        return;
	    }

	    if (getWidth() == 0 || getHeight() == 0) {
	        return; 
	    }
	    Bitmap b =  ((BitmapDrawable)drawable).getBitmap() ;
	    Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);

	    int w = getWidth(), h = getHeight();


	    Bitmap roundBitmap =  circularBitmap(bitmap, w);
	    canvas.drawBitmap(roundBitmap, 0,0, null);
		
		/*Drawable d = getDrawable();
		
		if (d == null)
			return; //No Image
		
		if (getWidth() == 0 || getHeight() == 0)
			return; //Image is invalid height
		
		Bitmap b = ((BitmapDrawable) d).getBitmap();
		Bitmap bMap = b.copy(Bitmap.Config.ARGB_8888, true);
		
		int width = getWidth();
		int height = getHeight();
		
		Bitmap circularBitmap = circularBitmap(bMap, width);
		canvas.drawBitmap(circularBitmap, 0, 0, null); */
		}

	public static Bitmap circularBitmap(Bitmap bMap, int width) {
		Bitmap bitmap;
		if(bMap.getWidth() != width || bMap.getHeight() != width)
			bitmap = Bitmap.createScaledBitmap(bMap,width, width, false);
		else
			bitmap = bMap;
		
		Bitmap bit = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas c = new Canvas(bit);
		
		final Paint p = new Paint();
		final Rect r = new Rect (0,0,bitmap.getWidth(), bitmap.getHeight());
		
		p.setAntiAlias(true);
		p.setFilterBitmap(true);
		p.setDither(true);
		c.drawARGB(0,0,0,0);
		p.setColor(Color.parseColor("#BAB399"));
		c.drawCircle(bitmap.getWidth()/(2+0.7f), bitmap.getHeight()/(2+0.7f), bitmap.getWidth()/(2+0.1f), p);
		p.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		c.drawBitmap(bitmap, r, r, p);
		
		return bit;
	}
}
