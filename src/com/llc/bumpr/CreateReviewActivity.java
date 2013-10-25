package com.llc.bumpr;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.llc.bumpr.sdk.models.User;

public class CreateReviewActivity extends SherlockActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_review);
		
		Bundle bundle = getIntent().getExtras();
		User user = (User) bundle.getParcelable("user");
		
		
		if (user == null) {
			throw new NullPointerException("Instance ('user') cannot be null");
		}
		
		TextView userName = (TextView) findViewById(R.id.tv_user_name);
		userName.setText(user.getFirstName() + " " + user.getLastName());
		
	}
	
	public void createReview(View v) {
		User user = User.getActiveUser();
	}
	
}
