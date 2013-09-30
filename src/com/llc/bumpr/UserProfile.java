package com.llc.bumpr;

import android.app.Activity;
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
		profPic.setImageBitmap(imageHelper.getCircleBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher), 256));
	}

}
