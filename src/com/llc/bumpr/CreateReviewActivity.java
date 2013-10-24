package com.llc.bumpr;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import com.actionbarsherlock.app.SherlockActivity;
import com.llc.bumpr.lib.CircularImageView;
import com.llc.bumpr.sdk.models.User;

public class CreateReviewActivity extends SherlockActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_review);
		
		Bundle bundle = getIntent().getExtras();
		User user = (User) bundle.getParcelable("user");
		
		CircularImageView imageView = (CircularImageView) findViewById(R.id.img_user);
		imageView.setImageResource(R.drawable.test_car_image);
		
		if (user == null) {
			throw new NullPointerException("Instance ('user') cannot be null");
		}
		
	}
	
	public void createReview(View v) {
		User user = User.getActiveUser();
	}
	
}
