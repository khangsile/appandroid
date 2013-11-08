package com.llc.bumpr;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.llc.bumpr.sdk.models.Driver;
import com.llc.bumpr.sdk.models.Request;
import com.llc.bumpr.sdk.models.Review;
import com.llc.bumpr.sdk.models.Session;
import com.llc.bumpr.sdk.models.User;

public class CreateReviewActivity extends ProfileActivity {
	/** Reference to the request object being reviewed */ 
	private Request request;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_review);

		initialize(); //Initialize the Header of the view 
	}
	
	@Override
	protected void initialize() {
		super.initialize();
		
		Bundle bundle = getIntent().getExtras();
		request = bundle.getParcelable("request");
		
		if (user.getDriverProfile() == null) throw new NullPointerException("Instance 'driverProfile' cannot be null");
		if (request == null) throw new NullPointerException("Instance ('request') cannot be null");
	}
	
	/**
	 * Creates a review object and submits the review
	 * @param v Reference to view object that called this method on click
	 */
	public void createReview(View v) {
		User activeUser = User.getActiveUser(); //Grab the active user

		//Get references to the description and rating bar
		String description = ((EditText) findViewById(R.id.et_review_description)).getText().toString();
		RatingBar ratingBar = (RatingBar) findViewById(R.id.rb_driver_rating);
		int rating = (int)ratingBar.getRating(); //Convert rating bar value to an integer value

		//Create review object
		Review review = new Review.Builder()
			.setDriverId(user.getDriverProfile().getId())
			.setRequestId(request.getId())
			.setRating(rating)
			.setContent(description)
			.build();

		//Get the active session and send the request to the server
		Session session = Session.getSession();
		session.sendRequest(review.getPostRequest(new Callback<Response>() {

			@Override
			public void failure(RetrofitError arg0) {
				//do nothing
				Toast.makeText(getApplicationContext(),
						"Failed to post review",
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void success(Response arg0, Response arg1) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(),
						"Review posted!",
						Toast.LENGTH_SHORT).show();
				finish();
			}
			
		}));
	}	
}
