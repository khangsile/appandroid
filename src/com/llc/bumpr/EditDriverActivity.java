package com.llc.bumpr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.content.Context;
import android.os.Bundle;
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
		EditProfileListAdapter adt = new EditProfileListAdapter(this, settingList, User.getActiveUser());
		profSettings.setAdapter(adt);
		
		//initOnClickListener();
		initImageClickListener();
	}

	
	private void initImageClickListener() {
		// TODO Auto-generated method stub
		
		//Set on click listener on the image to update the image
	}


	/**
	 * A method that sets up the item click listener for the settings menu.  Defines the actions that should take place
	 * depending on which item was clicked.
	 */
	/*private void initOnClickListener() {
		// TODO Auto-generated method stub
		profSettings.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				
				if (position == 3){ //Update Car Image!
					LayoutInflater li = LayoutInflater.from(context);
					View changePassView = li.inflate(R.layout.change_password_dialog, null);
					
					AlertDialog.Builder changePassDialogBldr = new AlertDialog.Builder(context);
					changePassDialogBldr.setTitle("Change Password");
					
					//Assign change password layout to dialog
					changePassDialogBldr.setView(changePassView);
					
					final EditText currPass = (EditText) changePassView.findViewById(R.id.et_curr_password);
					final EditText newPass = (EditText) changePassView.findViewById(R.id.et_new_password);
					final EditText confPass = (EditText) changePassView.findViewById(R.id.et_conf_password);
					
					changePassDialogBldr
						.setCancelable(true)
						.setPositiveButton(R.string.submit, new DialogInterface.OnClickListener(){
							@Override
							public void onClick(DialogInterface dialog, int id) {
								// TODO Auto-generated method stub
								//Submit Password here!
							}
						})
						.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialog.cancel();
							}
							
						});
					//Create alert dialog and show it
					AlertDialog changePassDialog = changePassDialogBldr.create();
					changePassDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
					changePassDialog.show();
				}
			}
			
		});
	}*/

	/**
	 * A method that fills the settingList element with the ordering and information to display in the list view UI element.
	 */
	private void initList(){
		settingList.add("Make");
		settingList.add("Model");
		settingList.add("Year");
		settingList.add("Passenger Seats");
	}
	
	public void update() {
		HashMap<String, Object> user = new HashMap<String, Object>();
		/*user.put("first_name", "");
		user.put("last_name", "");
		user.put("phone_number", "");
		user.put("password", "");*/
		
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
