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
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_driver_profile);
		
		profPic = (ImageView) findViewById(R.id.iv_car_pic);
		profSettings = (ListView) findViewById(R.id.lv_settings_list);
		joinDate = (TextView) findViewById(R.id.tv_edit_prof_join_date_text);
		
		//Set Join Date
		joinDate.setText("October 25, 2013");
		//Set image resources
		profPic.setImageResource(R.drawable.test_car_image);
		
		settingList = new ArrayList<String>();
		
		initList();
		adt = new EditProfileListAdapter(this, settingList, User.getActiveUser());
		profSettings.setAdapter(adt);
		
		//initOnClickListener();
		initImageClickListener();
	}

	
	private void initImageClickListener() {
		// TODO Auto-generated method stub
		
		//Set on click listener on the image to update the image
	}

	/**
	 * A method that fills the settingList element with the ordering and information to display in the list view UI element.
	 */
	private void initList(){
		settingList.add("Make");
		settingList.add("Model");
		settingList.add("Year");
		settingList.add("Passenger Seats");
		settingList.add("Rate");
	}
	
	public void update() {
		HashMap<String, Object> driver = new HashMap<String, Object>();
		User activeUser = User.getActiveUser();
		
		//Use this code, but change the 'put' details to implement this 
		
		View v1;
		Object val;
		EditText et;

		for (int i=0; i< adt.getCount(); i++){
			if(adt.getItemViewType(i)==0) {
				v1 = profSettings.getChildAt(i);
				et = (EditText) v1.findViewById(R.id.et_edit_prof_value);
				val = (Object) et.getText().toString();
				
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
					driver.put("rate", val);
			}
		}
		
		Toast.makeText(getApplicationContext(), driver.toString(), Toast.LENGTH_SHORT).show();
		
		if (!((((String)driver.get("rate")).trim()).length() > 0)){
			Toast.makeText(getApplicationContext(),
					"To register as a driver, please provide the details above.",
					Toast.LENGTH_SHORT).show();
			return; //Do not advance
		}
		
		if (activeUser.getDriverProfile() == null){
			//Register as a driver
			Toast.makeText(getApplicationContext(),
					"You are not registered as a driver.",
					Toast.LENGTH_SHORT).show();
			return; //Do not advance
		}
		
		//Update driver page
		ApiRequest request = activeUser.getDriverProfile().getUpdateRequest(driver, new Callback<Driver>() {

			@Override
			public void failure(RetrofitError arg0) {
				// TODO Auto-generated method stub
				// Send alert
				Toast.makeText(getApplicationContext(),
						"Error updating driver page",
						Toast.LENGTH_SHORT).show();
				return;
			}

			@Override
			public void success(Driver arg0, Response arg1) {
				// TODO Auto-generated method stub
				// Update list;
				Toast.makeText(getApplicationContext(),
						"Driver details updated!",
						Toast.LENGTH_SHORT).show();
				finish();
			}
			
		});
		
		Session session = Session.getSession();
		session.sendRequest(request);
	}

}
