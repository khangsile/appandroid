package com.llc.bumpr;

import android.os.Bundle;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.llc.bumpr.lib.CircularImageView;
import com.llc.bumpr.sdk.models.User;

public class ProfileActivity extends SherlockActivity {
	
	/** Reference to the user to be displayed in the header */
	protected User user;
	
	/**
	 * Call this method after setting the content view. Populates the user img and checks for NullPointerExceptions
	 */
	protected void initialize() {
		//Get the objects passed to this activity
		Bundle bundle = getIntent().getExtras();
		//Get the user passed to display in the header
		user = (User) bundle.getParcelable("user");
		
		//Verify a user was passed
		if (user == null) {
			throw new NullPointerException("Instance ('user') cannot be null");
		}
		
		//Fill in views in the header
		TextView userName = (TextView) findViewById(R.id.tv_user_name);
		userName.setText(user.getFirstName() + " " + user.getLastName());
		//Fill in circular image with the user's profile picture -- Test image for now
		CircularImageView userPhoto = (CircularImageView) findViewById(R.id.img_user);
		userPhoto.setImageResource(R.drawable.test_image);
	}
	
	
}
