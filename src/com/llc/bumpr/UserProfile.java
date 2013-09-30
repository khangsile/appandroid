package com.llc.bumpr;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

public class UserProfile extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.user_profile);
		ImageView profPic = (ImageView) findViewById(R.id.profile_pic);
		GraphicsUtil imageHelper = new GraphicsUtil();
		Bitmap bm = imageHelper.getCircleBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.test_image), 16);
		//Resize image to the desired size
		Bitmap resizedBM = Bitmap.createScaledBitmap(bm, 200, 200, false);
		profPic.setImageBitmap(resizedBM);
	}
}
