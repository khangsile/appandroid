package com.llc.bumpr;

import android.os.Bundle;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.llc.bumpr.lib.CircularImageView;
import com.llc.bumpr.sdk.models.User;

public class ProfileActivity extends SherlockActivity {
	
	protected User user;
	
	/**
	 * Call this method after setting the content view. Populates the user img and checks for NullPointerExceptions
	 */
	protected void initialize() {
		Bundle bundle = getIntent().getExtras();
		user = (User) bundle.getParcelable("user");
		
		if (user == null) {
			throw new NullPointerException("Instance ('user') cannot be null");
		}
		
		TextView userName = (TextView) findViewById(R.id.tv_user_name);
		userName.setText(user.getFirstName() + " " + user.getLastName());
		
		CircularImageView userPhoto = (CircularImageView) findViewById(R.id.img_user);
		userPhoto.setImageResource(R.drawable.test_image);
	}
	
	
}
