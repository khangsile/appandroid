package com.llc.bumpr;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.llc.bumpr.sdk.models.Request;
import com.llc.bumpr.sdk.models.Review;
import com.llc.bumpr.sdk.models.Session;
import com.llc.bumpr.sdk.models.User;

public class CreateReviewActivity extends SherlockActivity {
	private User driver;
	private Request request;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_review);
		
		Bundle bundle = getIntent().getExtras();
		driver = (User) bundle.getParcelable("user");
		request = (Request) bundle.getParcelable("request");
		
		
		if (driver == null) {
			throw new NullPointerException("Instance ('driver') cannot be null");
		}
		
		TextView userName = (TextView) findViewById(R.id.tv_user_name);
		userName.setText(driver.getFirstName() + " " + driver.getLastName());
		
	}
	
	public void createReview(View v) {
		User user = User.getActiveUser();

		String description = ((EditText) findViewById(R.id.et_review_description)).getText().toString();
		RatingBar ratingBar = (RatingBar) findViewById(R.id.rb_driver_rating);
		int rating = (int)ratingBar.getRating();

		Review review = new Review.Builder()
											.setDriverId(driver.getId())
											.setUserId(User.getActiveUser().getId())
											.setRequestId(request.getId())
											.setDriverRating(rating)
											.setDescription(description)
											.build();

	
		Session session = Session.getSession();
		session.sendRequest(review.getPostRequest(new Callback<Response>() {

			@Override
			public void failure(RetrofitError arg0) {
				// TODO Auto-generated method stub
				//do nothing
			}

			@Override
			public void success(Response arg0, Response arg1) {
				// TODO Auto-generated method stub
				//do nothing
			}
			
		}));
	}	
}
