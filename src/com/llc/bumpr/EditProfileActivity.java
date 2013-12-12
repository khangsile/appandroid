package com.llc.bumpr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.Target;

public class EditProfileActivity extends SherlockActivity {
	/** List to hold the profile details that can be edited */
	private List<String> settingList;
	
	/** Reference to the profile picture UI element */
	private ImageView profPic;
	
	/** Reference to the User name UI element */
	private TextView userName;
	
	/** Reference to the List View UI element that holds the profile settings information */
	private ListView profSettings;
	
	/**A reference to the current context to be used in inner classes */
	final private Context context = this;
	
	/** A reference to the listview adapter */
	private EditProfileListAdapter adt;
	
	/**A Target callback to handle the image view loading */
	private Target target;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_profile);
		
		//Get references to the view objects in the layout and fill these in with user details
		userName = (TextView) findViewById(R.id.tv_user_name);
		userName.setText(User.getActiveUser().getFirstName() + " " + User.getActiveUser().getLastName());

		profPic = (CircularImageView) findViewById(R.id.img_user);
	
		target = new Target() {

			@Override
			public void onBitmapFailed(Drawable arg0) {
				//do nothing
			}

			@Override
			public void onBitmapLoaded(Bitmap arg0, LoadedFrom arg1) {
				profPic.setImageBitmap(arg0);
			}

			@Override
			public void onPrepareLoad(Drawable arg0) {
				profPic.setImageResource(R.drawable.missing);
			}
		};
		
		Picasso.with(context).load(User.getActiveUser().getProfileImage()+"?type=large").into(target);
		
		profSettings = (ListView) findViewById(R.id.lv_settings_list);
		
		//Set up settings list, create edit profile adapter, and set the adapter
		settingList = new ArrayList<String>();
		initList();
		adt = new EditProfileListAdapter(this, settingList, User.getActiveUser());
		profSettings.setAdapter(adt);
		
		initOnClickListener();
	}

	/**
	 * A method that sets up the item click listener for the settings menu.  Defines the actions that should take place
	 * depending on which item was clicked.
	 */
	private void initOnClickListener() {
		profSettings.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				if (position == 2){ //Update Password dialog box!
					LayoutInflater li = LayoutInflater.from(context);
					View changePassView = li.inflate(R.layout.change_password_dialog, null); 
					
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
						.setCancelable(true) 
						.setPositiveButton(R.string.submit, new DialogInterface.OnClickListener(){
							@Override
							public void onClick(DialogInterface dialog, int id) {
								//Submit Password here when API endpoint is in place
							}
						})
						.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel(); 
							}
							
						});

					AlertDialog changePassDialog = changePassDialogBldr.create();
					changePassDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
					changePassDialog.show();
				}
			}
		});
	}

	/**
	 * A method that fills the settingList element with the ordering and information to display in the list view UI element.
	 */
	private void initList(){
		//Fill edit profile list with details for a user
		settingList.add("Email");
		settingList.add("Phone");
		settingList.add("Password");
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
				if(adt.getItem(i).toString().equals("Phone"))
					user.put("phone_number", val);
				if(adt.getItem(i).toString().equals("Email"))
					user.put("email", val);
			}
		}
		//For testing
		Toast.makeText(getApplicationContext(), user.toString(), Toast.LENGTH_SHORT).show();

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
		
		Session session = Session.getSession();
		session.sendRequest(request);
	}
	
}
