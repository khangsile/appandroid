package com.llc.bumpr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.llc.bumpr.adapters.EditProfileListAdapter;
import com.llc.bumpr.lib.DynamicImageView;
import com.llc.bumpr.sdk.lib.ApiRequest;
import com.llc.bumpr.sdk.models.Driver;
import com.llc.bumpr.sdk.models.Session;
import com.llc.bumpr.sdk.models.User;

public class EditDriverActivity extends SherlockActivity {
	
	/** List to hold the profile details that can be edited */
	private List<String> settingList;
	/** Reference to the profile picture UI element */
	private ImageView profPic;
	
	/** Reference to the List View UI element that holds the profile settings information */
	private ListView profSettings;
	/** Reference to the join date UI text element */
	private TextView joinDate;
	/**A reference to the current context to be used in inner classes */
	final private Context context = this;
	/** A reference to the listview adapter */
	private EditProfileListAdapter adt;
	/** Reference to the active user */
	private User user;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_driver_profile); //Use the edit driver profile layout
		
		//Get reference to views in the layout
		profPic = (ImageView) findViewById(R.id.iv_car_pic);
		profSettings = (ListView) findViewById(R.id.lv_settings_list);
		joinDate = (TextView) findViewById(R.id.tv_edit_prof_join_date_text);
		
		//Set Join Date
		joinDate.setText("October 25, 2013"); //Update this to grab it from the current user
		//Set image resources
		profPic.setImageResource(R.drawable.test_car_image); //Set User's Car image resource
		
		user = User.getActiveUser(); //Assign reference to the active user
		
		//Set up settings list, create edit profile adapter, and set the adapter
		settingList = new ArrayList<String>();
		initList();
		adt = new EditProfileListAdapter(this, settingList, user);
		profSettings.setAdapter(adt);
		
		//initOnClickListener();
		//initImageClickListener(); //Not used currently
	}

	//Will be used in the future, but not currently 
	/*private void initImageClickListener() {
		// TODO Auto-generated method stub
		
		//Set on click listener on the image to update the image
	}*/

	/**
	 * A method that fills the settingList element with the ordering and information to display in the list view UI element.
	 */
	private void initList(){
		//Set up array list to hold the information that should be displayed in the driver setting list view 
		settingList.add("Make");
		settingList.add("Model");
		settingList.add("Year");
		settingList.add("Passenger Seats");
		settingList.add("Rate");
	}
	
	/**
	 * Update the current driver with the values stored the list
	 */
	public void update() {
		//Create hash map to hold the key value pairs of driver details
		HashMap<String, Object> driver = new HashMap<String, Object>();
		//Get reference to active user
		User activeUser = User.getActiveUser();
		
		View v1; //Reference to hold row view
		Object val; //Reference to hold updated values 
		EditText et; //Reference to the edit text object in each row view

		for (int i=0; i< adt.getCount(); i++){ //For all the detail in the list view
			if(adt.getItemViewType(i)==0) { //Verify it is an edit text row
				v1 = profSettings.getChildAt(i); //Get row view
				et = (EditText) v1.findViewById(R.id.et_edit_prof_value); //Get edit text in that row
				val = (Object) et.getText().toString(); //Grab value in edit text field
				
				/*if(adt.getItem(i).toString().equals("Make"))
					driver.put("first_name", val);
				if(adt.getItem(i).toString().equals("Model"))
					driver.put("last_name", val);
				if(adt.getItem(i).toString().equals("Year"))
					driver.put("phone_number", val);
				if(adt.getItem(i).toString().equals("Passenger Seats"))
					driver.put("email", val);*/
				
				//Only keep track of Rate at the moment
				if(adt.getItem(i).toString().equals("Rate"))
					driver.put("rate", val); //Add rate and it's value to hash map
			}
		}
		
		Toast.makeText(getApplicationContext(), driver.toString(), Toast.LENGTH_SHORT).show(); //FOr testing
		
		if (!((((String)driver.get("rate")).trim()).length() > 0)){ //If no rate value, display error message
			Toast.makeText(getApplicationContext(),
					"To register as a driver, please provide the details above.",
					Toast.LENGTH_SHORT).show();
			return; //Do not advance
		}
		
		if (activeUser.getDriverProfile() == null){ //If the current user is not a registered driver, register them
			//Register as a driver
			Toast.makeText(getApplicationContext(),
					"You are not registered as a driver.",
					Toast.LENGTH_SHORT).show();
			return; //Do not advance
		}
		
		//Update driver page with update request
		ApiRequest request = activeUser.getDriverProfile().getUpdateRequest(driver, new Callback<Driver>() {

			@Override
			public void failure(RetrofitError arg0) {
				// TODO Auto-generated method stub
				// Display error
				Toast.makeText(getApplicationContext(),
						"Error updating driver page",
						Toast.LENGTH_SHORT).show();
				return;
			}

			@Override
			public void success(Driver arg0, Response arg1) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(),
						"Driver details updated!",
						Toast.LENGTH_SHORT).show();
				finish();
			}
			
		});
		//Send request with current session
		Session session = Session.getSession();
		session.sendRequest(request);
	}

}
