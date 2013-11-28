package com.llc.bumpr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.koushikdutta.async.future.FutureCallback;
import com.llc.bumpr.adapters.EditProfileListAdapter;
import com.llc.bumpr.lib.CircularImageView;
import com.llc.bumpr.sdk.lib.ApiRequest;
import com.llc.bumpr.sdk.models.Session;
import com.llc.bumpr.sdk.models.User;

public class EditProfileActivity extends SherlockActivity {
	/** List to hold the profile details that can be edited */
	private List<String> settingList;
	/** Reference to the profile picture UI element */
	private CircularImageView profPic;
	
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
		setContentView(R.layout.edit_profile);
		//Get reference to the views in the layout
		profPic = (CircularImageView) findViewById(R.id.iv_profile_pic);
		profSettings = (ListView) findViewById(R.id.lv_settings_list);
		joinDate = (TextView) findViewById(R.id.tv_edit_prof_join_date_text);
		
		//Set Join Date -- Use user details in the future
		joinDate.setText("October 25, 2013");
		//Set image resources
		profPic.setImageResource(R.drawable.test_image);
		
		//Set up settings list, create edit profile adapter, and set the adapter
		settingList = new ArrayList<String>();
		initList();
		adt = new EditProfileListAdapter(this, settingList, User.getActiveUser());
		profSettings.setAdapter(adt);
		
		//Initialize on click listener
		initOnClickListener();
	}

	/**
	 * A method that sets up the item click listener for the settings menu.  Defines the actions that should take place
	 * depending on which item was clicked.
	 */
	private void initOnClickListener() {
		// TODO Auto-generated method stub
		profSettings.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				if (position == 4){ //Update Password dialog box!
					//Inflate change password dialog
					LayoutInflater li = LayoutInflater.from(context);
					View changePassView = li.inflate(R.layout.change_password_dialog, null); 
					
					//Build alert dialog for current context and set title
					AlertDialog.Builder changePassDialogBldr = new AlertDialog.Builder(context);
					changePassDialogBldr.setTitle("Change Password");
					
					//Assign change password layout to dialog
					changePassDialogBldr.setView(changePassView);
					
					//Get reference to the edit text views in the dialog
					final EditText currPass = (EditText) changePassView.findViewById(R.id.et_curr_password);
					final EditText newPass = (EditText) changePassView.findViewById(R.id.et_new_password);
					final EditText confPass = (EditText) changePassView.findViewById(R.id.et_conf_password);
					
					//Apply settings to the change dialog builder
					changePassDialogBldr
						.setCancelable(true) //Allow dialog to be canceled
						.setPositiveButton(R.string.submit, new DialogInterface.OnClickListener(){
							@Override
							public void onClick(DialogInterface dialog, int id) {
								// TODO Auto-generated method stub
								//Submit Password here when API endpoint is in place
							}
						})
						.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialog.cancel(); //Close dialog when cancel is pressed
							}
							
						});
					//Create alert dialog and show it
					AlertDialog changePassDialog = changePassDialogBldr.create();
					changePassDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
					changePassDialog.show();
				}
				else if(position == 5) { //Edit Driver Settings Page
					//Create intent for edit driver activity
					Intent i = new Intent(getApplicationContext(), EditDriverActivity.class);
					i.putExtra("user", User.getActiveUser()); //Pass the current active user
					startActivity(i); //Start the activity
				}
			}
		});
	}

	/**
	 * A method that fills the settingList element with the ordering and information to display in the list view UI element.
	 */
	private void initList(){
		//Fill edit profile list with details for a user
		settingList.add("First Name");
		settingList.add("Last Name");
		settingList.add("Phone");
		settingList.add("Email");
		settingList.add("Password");
		settingList.add("Driver Settings");
	}
	
	/**
	 * Updates the active user with the edit text fields list view
	 * @param v View reference that called this method on click
	 */
	public void update(View v) {
		//Hash map to hold details to be updated
		final HashMap<String, Object> user = new HashMap<String, Object>();
		View v1; //Reference to hold row view
		Object val; //Reference to hold updated values
		EditText et; //Reference to the edit text object in each row view
		
		for (int i=0; i< adt.getCount(); i++){ //For all details in list view
			if(adt.getItemViewType(i)==0) { //If the row is an edit text row
				v1 = profSettings.getChildAt(i); //Get row view
				et = (EditText) v1.findViewById(R.id.et_edit_prof_value); //Get edit text in that row
				val = (Object) et.getText().toString(); //Save the value in the edit text
				
				//Add each field to the hash map
				if(adt.getItem(i).toString().equals("First Name"))
					user.put("first_name", val);
				if(adt.getItem(i).toString().equals("Last Name"))
					user.put("last_name", val);
				if(adt.getItem(i).toString().equals("Phone"))
					user.put("phone_number", val);
				if(adt.getItem(i).toString().equals("Email"))
					user.put("email", val);
			}
		}
		//For testing
		Toast.makeText(getApplicationContext(), user.toString(), Toast.LENGTH_SHORT).show();
		//Get reference to active user to update
		User activeUser = User.getActiveUser();
		//Send request update
		ApiRequest request = activeUser.getUpdateRequest(this, user, new FutureCallback<User>() {

			@Override
			public void onCompleted(Exception arg0, User arg1) {
				if (arg0 == null) {
					finish();
				} else {
					Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
				}
			}
			
		});
		
		//Get active session and submit the request
		Session session = Session.getSession();
		session.sendRequest(request);
	}
	
}
