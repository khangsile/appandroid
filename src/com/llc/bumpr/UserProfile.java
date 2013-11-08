package com.llc.bumpr;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.androidtools.Conversions;
import com.google.android.gms.maps.model.LatLng;
import com.llc.bumpr.adapters.MyReviewAdapter;
import com.llc.bumpr.lib.CircularImageView;
import com.llc.bumpr.lib.GMapV2Painter;
import com.llc.bumpr.sdk.lib.Coordinate;
import com.llc.bumpr.sdk.models.Request;
import com.llc.bumpr.sdk.models.Session;
import com.llc.bumpr.sdk.models.Trip;
import com.llc.bumpr.sdk.models.User;

public class UserProfile extends Activity {

	/** Reference to the driver's user object */
	private User user;
	
	/** Reference to the trip to request */
	private Trip trip;
	
	/** List holding all of the reviews for the driver */
	private List<Object> reviewList;
	
	/** Reference to a MyReviewAdapter to display the reviews in the reviewList object */
	private MyReviewAdapter reviewAdp;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_profile);
		
		Bundle bundle = getIntent().getExtras();
		user = (User) bundle.getParcelable("user");
		trip = (Trip) bundle.getParcelable("trip");
		
		if (user == null) throw new NullPointerException("Instance ('user') cannot be null");
		else if (user.getDriverProfile() == null) throw new NullPointerException("Instance ('driver') cannot be null");
		if (trip == null) throw new NullPointerException("Instance ('trip') cannot be null");
		
		//Retrieve views
		CircularImageView profPic = (CircularImageView) findViewById(R.id.iv_profile_pic);
		ImageView carImg = (ImageView) findViewById(R.id.iv_car_image);
		TextView userName = (TextView) findViewById(R.id.tv_user_name);
		TextView userLoc = (TextView) findViewById(R.id.tv_user_loc);
		TextView userCar = (TextView) findViewById(R.id.tv_user_car);
		TextView numSeats = (TextView) findViewById(R.id.tv_num_seats);
		TextView carRate = (TextView) findViewById(R.id.tv_car_rate);
		ListView reviews = (ListView) findViewById(R.id.lv_user_reviews);
		
		//Make list view visible!
		reviews.setVisibility(View.VISIBLE);
		
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
		ArrayList<LatLng> points = new ArrayList<LatLng>();
		points.add(new LatLng(trip.getStart().lat, trip.getStart().lon));
		points.add(new LatLng(trip.getEnd().lat, trip.getEnd().lon));
		
		//Get the total distance of the trip
		GMapV2Painter.getDistance(points, new Callback<Integer>() {

			@Override
			public void failure(RetrofitError arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void success(Integer arg0, Response arg1) {
				// TODO Auto-generated method stub
				TextView carRate = (TextView) findViewById(R.id.tv_car_rate);
				carRate.setText("$" + arg0 * user.getDriverProfile().getFee());
			}
			
		});
		
		
		//Get reviews and fill in review list view
		initReviewList();
		reviewAdp = new MyReviewAdapter(getApplicationContext(), reviewList, R.layout.my_review_row);
		reviews.setAdapter(reviewAdp);
	}
	
	/**
	 * Gets all reviews of this driver and fills them into this list
	 */
	private void initReviewList() {
		// TODO Auto-generated method stub
		reviewList = new ArrayList<Object>();
		reviewList.add("This is a very long ..................... text object to span" +
            		"multiple lines to test that the reviews are being set properly");
		reviewList.add("Test a short review");
		reviewList.add("Test a mean mean mean mean review review review review review .......... damn damn damn damn damn damn damn this is very very very very long long long long.");
		reviewList.add("Hi");
		reviewList.add("This guys blows");
		reviewList.add("He wrecked");
		reviewList.add("I puked in his car");
	}

	public void request(View v) {
		Request r = new Request.Builder()
						.setDriverId(user.getId())
						.setUserId(User.getActiveUser().getId())
						.setTrip(trip)
						.build();
		
		Session session = Session.getSession();
		session.sendRequest(r.postRequest(new Callback<Request>() {

			@Override
			public void failure(RetrofitError arg0) { // do nothing
			}

			@Override
			public void success(Request arg0, Response arg1) { // do nothing
			}
			
		}));
	}
}
