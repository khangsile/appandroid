package com.llc.bumpr;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class UserProfile extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.user_profile);
		
		//Retrieve views
		ImageView profPic = (ImageView) findViewById(R.id.iv_profile_pic);
		ImageView carImg = (ImageView) findViewById(R.id.iv_car_image);
		TextView userName = (TextView) findViewById(R.id.tv_user_name);
		
		//Set images
		carImg.setImageResource(R.drawable.test_car_image);
		GraphicsUtil imageHelper = new GraphicsUtil();
		Bitmap bm = imageHelper.getCircleBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.test_image), 16);
		//Resize image to the desired size
		Bitmap resizedBM = Bitmap.createScaledBitmap(bm, 200, 200, false);
		profPic.setImageBitmap(resizedBM);
	
		//Set layout parameters of image view
		/*MarginLayoutParams profPicMargin = new MarginLayoutParams(profPic.getLayoutParams());
		profPicMargin.setMargins(0,profPic.getHeight()/2, 0, 0);		
		RelativeLayout.LayoutParams profPicLP = new RelativeLayout.LayoutParams(profPicMargin);
		profPic.setLayoutParams(profPicLP);*/
		
		//Set user name
		userName.setText("Kyle Cooper");
	}
}
