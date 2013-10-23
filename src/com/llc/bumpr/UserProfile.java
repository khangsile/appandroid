package com.llc.bumpr;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidtools.Conversions;
import com.llc.bumpr.sdk.models.User;

public class UserProfile extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_profile);
		
		Bundle bundle = getIntent().getExtras();
		User user = (User) bundle.getParcelable("user");
		
		if (user == null) {
			throw new NullPointerException("Instance ('user') cannot be null");
		}
		
		//Retrieve views
		ImageView profPic = (ImageView) findViewById(R.id.iv_profile_pic);
		ImageView carImg = (ImageView) findViewById(R.id.iv_car_image);
		TextView userName = (TextView) findViewById(R.id.tv_user_name);
		TextView userLoc = (TextView) findViewById(R.id.tv_user_loc);
		TextView userCar = (TextView) findViewById(R.id.tv_user_car);
		TextView numSeats = (TextView) findViewById(R.id.tv_num_seats);
		TextView carRate = (TextView) findViewById(R.id.tv_car_rate);
		
		//Set images
		float imageSize = Conversions.dpToPixels(this, 100); //**TO DO - Change to pixel size based on dp value of phone. Use chris allen lib.
		GraphicsUtil imageHelper = new GraphicsUtil();
		Bitmap bm = imageHelper.getCircleBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.test_image), 16);
		//Resize image to the desired size
		Bitmap resizedBM = Bitmap.createScaledBitmap(bm, Math.round(imageSize), Math.round(imageSize), false);
		profPic.setImageBitmap(resizedBM);
	
		//Set layout parameters of image view
		carImg.setPadding(0,0, 0, Math.round(Conversions.dpToPixels(this, 50)));
		carImg.setImageResource(R.drawable.test_car_image);
		
		//Set text values
		userName.setText(user.getFirstName() + " " + user.getLastName());
		userLoc.setText(user.getCity() + ", " + user.getState());
		userCar.setText("Car: 2013 Camry Hybrid XLE");
		numSeats.setText("Seats: 4");
		carRate.setText("Rate: $2.25/mi");
	}
}
