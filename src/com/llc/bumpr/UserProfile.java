package com.llc.bumpr;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidtools.Conversions;
import com.llc.bumpr.lib.CircularImageView;
import com.llc.bumpr.sdk.lib.Coordinate;
import com.llc.bumpr.sdk.models.Request;
import com.llc.bumpr.sdk.models.Session;
import com.llc.bumpr.sdk.models.Trip;
import com.llc.bumpr.sdk.models.User;

public class UserProfile extends Activity {

	private User user;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_profile);
		
		Bundle bundle = getIntent().getExtras();
		user = (User) bundle.getParcelable("user");
		
		if (user == null) {
			throw new NullPointerException("Instance ('user') cannot be null");
		} else if (user.getDriverProfile() == null) {
			throw new NullPointerException("Instance ('driver') cannot be null");
		}
		
		//Retrieve views
		CircularImageView profPic = (CircularImageView) findViewById(R.id.iv_profile_pic);
		ImageView carImg = (ImageView) findViewById(R.id.iv_car_image);
		TextView userName = (TextView) findViewById(R.id.tv_user_name);
		TextView userLoc = (TextView) findViewById(R.id.tv_user_loc);
		TextView userCar = (TextView) findViewById(R.id.tv_user_car);
		TextView numSeats = (TextView) findViewById(R.id.tv_num_seats);
		TextView carRate = (TextView) findViewById(R.id.tv_car_rate);
		
		profPic.setImageResource(R.drawable.test_image);
	
		//Set layout parameters of image view
		carImg.setPadding(0,0, 0, Math.round(Conversions.dpToPixels(this, 50)));
		carImg.setImageResource(R.drawable.test_car_image);
		
		//Set text values
		userName.setText(user.getFirstName() + " " + user.getLastName());
		//userLoc.setText(user.getCity() + ", " + user.getState());
		//userCar.setText("Car: 2013 Camry Hybrid XLE");
		//numSeats.setText("Seats: 4");
		//carRate.setText("Rate: $" + user.getDriverProfile().getFee() + "per mile");
	}
	
	public void request(View v) {
		Trip t = new Trip.Builder()
					.setStart(new Coordinate(36.6, 38.7))
					.setEnd(new Coordinate(36.2, 38.6))
					.build();
		
		Request r = new Request.Builder()
						.setDriverId(1)
						.setUserId(User.getActiveUser().getId())
						.setTrip(t)
						.build();
		
		Session session = Session.getSession();
		session.sendRequest(r.getPostRequest(new Callback<Request>() {

			@Override
			public void failure(RetrofitError arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void success(Request arg0, Response arg1) {
				// TODO Auto-generated method stub
				
			}
			
		}));
	}
}
