package com.llc.bumpr;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.llc.bumpr.lib.CircularImageView;
import com.llc.bumpr.sdk.models.Request;
import com.llc.bumpr.sdk.models.User;

public class RequestActivity extends SherlockFragmentActivity {

	private User user;
	private Request request;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.request);
		
		initialize();
	}
	
	protected void initialize() {
		Bundle bundle = getIntent().getExtras();
		request = (Request) bundle.getParcelable("request");
		user = (User) bundle.getParcelable("user");
		
		if (user == null) {
			throw new NullPointerException("Instance ('user') cannot be null");
		}
		
		TextView userName = (TextView) findViewById(R.id.tv_user_name);
		userName.setText(user.getFirstName() + " " + user.getLastName());
		
		CircularImageView userPhoto = (CircularImageView) findViewById(R.id.img_user);
		userPhoto.setImageResource(R.drawable.test_image);
		
		TextView toAddress = (TextView) findViewById(R.id.tv_toAddress);
		TextView toCityState = (TextView) findViewById(R.id.tv_toCityState);
		TextView fromAddress = (TextView) findViewById(R.id.tv_fromAddress);
		TextView fromCityState = (TextView) findViewById(R.id.tv_fromCityState);
		
		toAddress.setText("619 Braddock Ct.");
		toCityState.setText("Edgewood, KY 41017");
		fromAddress.setText("557 Lone Oak Dr.");
		fromCityState.setText("Lexington, KY 40503");
		
	}
	
	public void acceptRequest(View v) {
		
	}
	
	public void declineRequest(View v) {
		
	}
	
}
