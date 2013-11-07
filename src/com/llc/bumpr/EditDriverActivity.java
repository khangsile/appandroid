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

import com.actionbarsherlock.app.SherlockActivity;
import com.llc.bumpr.adapters.EditProfileListAdapter;
import com.llc.bumpr.lib.DynamicImageView;
import com.llc.bumpr.sdk.lib.ApiRequest;
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
		HashMap<String, Object> user = new HashMap<String, Object>();
		
		//Use this code, but change the 'put' details to implement this 
		
		/*View v1;
		Object val;
		EditText et;
		//EditProfileListAdapter.TextViewHolder holder;
		for (int i=0; i< adt.getCount(); i++){
			if(adt.getItemViewType(i)==0) {
				v1 = profSettings.getChildAt(i);
				et = (EditText) v1.findViewById(R.id.et_edit_prof_value);
				val = (Object) et.getText().toString();
				
				if(adt.getItem(i).toString().equals("Make"))
					user.put("first_name", val);
				if(adt.getItem(i).toString().equals("Model"))
					user.put("last_name", val);
				if(adt.getItem(i).toString().equals("Year"))
					user.put("phone_number", val);
				if(adt.getItem(i).toString().equals("Passenger Seats"))
					user.put("email", val);
				if(adt.getItem(i).toString().equals("Rate"))
					user.put("rate", val);
			}
		}*/
		
		User activeUser = User.getActiveUser();
		ApiRequest request = activeUser.getUpdateRequest(user, new Callback<User>() {

			@Override
			public void failure(RetrofitError arg0) {
				// TODO Auto-generated method stub
				// Send alert
			}

			@Override
			public void success(User arg0, Response arg1) {
				// TODO Auto-generated method stub
				// Update list;
			}
			
		});
		
		Session session = Session.getSession();
		session.sendRequest(request);
	}

}
