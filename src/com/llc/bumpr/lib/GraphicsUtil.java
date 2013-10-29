package com.llc.bumpr.lib;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;

/**
 * GraphicsUtil an utility class which convert the 
 * image in circular shape
 * Author : Mukesh Yadav
 */
public class GraphicsUtil {

	/**
	 * Draw original image in circular shape.
	 * @param bitmap The original image source to turn into a circular image
	 */
	public Bitmap getCircleBitmap(Bitmap bitmap, int pixels) {
		//Commented out lines below to improve performance		
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xffff0000;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		//final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		//paint.setDither(true);
		//paint.setFilterBitmap(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawCircle(bitmap.getWidth()/2, bitmap.getHeight()/2, bitmap.getWidth()/2, paint);
		//canvas.drawOval(rectF, paint);

		//paint.setStyle(Paint.Style.STROKE);
		//paint.setStrokeWidth((float) 4);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

}