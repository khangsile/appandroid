package com.llc.bumpr;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidtools.Conversions;
import com.google.android.gms.maps.model.LatLng;
import com.llc.bumpr.adapters.MyReviewAdapter;
import com.llc.bumpr.lib.CircularImageView;
import com.llc.bumpr.lib.GMapV2Painter;
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
		setContentView(R.layout.user_profile); //Display layout
		
		//Get user object passed to this activity
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

		//Set user profile picture -- Use test image for now
		profPic.setImageResource(R.drawable.test_image);
	
		//Set layout parameters of image view
		carImg.setPadding(0,0, 0, Math.round(Conversions.dpToPixels(this, 50)));
		carImg.setImageResource(R.drawable.test_car_image);
		
		//Set text values
		userName.setText(user.getFirstName() + " " + user.getLastName());
		userLoc.setText(trip.getStart().title);
		//numSeats.setText("Seats: " + trip.g);
		carRate.setText("$" + String.format("%.2f", trip.getCost()));
		
		//Get reviews and fill in review list view
		initReviewList(); //Initialize list of review objects
		//Create review adapter and assign this adapter to the list view
		reviewAdp = new MyReviewAdapter(getApplicationContext(), reviewList, R.layout.my_review_row);
		reviews.setAdapter(reviewAdp);
	}
	
	/**
	 * Gets all reviews of this driver and fills them into this list
	 */
	private void initReviewList() {
		// TODO Auto-generated method stub
		//Get reviews of this driver to be displayed in the list view
		reviewList = new ArrayList<Object>();
		reviewList.add("He propmptly replied to my request and was here before I knew it!!");
		reviewList.add("He is very outgoing and has a really clean car! I highly recommend!");
		reviewList.add("He was great!  Very nice and even drove us through Canes on the way home!");
		reviewList.add("This guy is the best! Best driver I have had yet!");
	}

	/**
	 * Method called when the user presses the request button
	 * @param v Reference to the View which called this method on click 
	 */
	public void request(View v) {
		
		//Create Request object for this request using user, driver, and trip details
		//Get session and send up the request to the server 
		Session session = Session.getSession();
	}
}
