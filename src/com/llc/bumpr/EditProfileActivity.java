package com.llc.bumpr;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.llc.bumpr.adapters.EditProfileListAdapter;
import com.llc.bumpr.lib.CircularImageView;
import com.llc.bumpr.sdk.models.User;

public class EditProfileActivity extends SherlockActivity {
	/** List to hold the profile details that can be edited */
	private List<String> settingList;
	/** Reference to the profile picture UI element */
	private CircularImageView profPic;
	
	//private DynamicImageView carImage;
	/** Reference to the List View UI element that holds the profile settings information */
	private ListView profSettings;
	/** Reference to the join date UI text element */
	private TextView joinDate;
	/**A reference to the current context to be used in inner classes */
	final private Context context = this;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_profile);
		
		profPic = (CircularImageView) findViewById(R.id.iv_profile_pic);
		profSettings = (ListView) findViewById(R.id.lv_settings_list);
		joinDate = (TextView) findViewById(R.id.tv_edit_prof_join_date_text);
		
		//Set Join Date
		joinDate.setText("October 25, 2013");
		//Set image resources
		profPic.setImageResource(R.drawable.test_image);
		//carImage.setImageResource(R.drawable.test_car_image);
		
		settingList = new ArrayList<String>();
		
		initList();
		EditProfileListAdapter adt = new EditProfileListAdapter(this, settingList, User.getActiveUser());
		profSettings.setAdapter(adt);
		
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
	}

	/**
	 * A method that fills the settingList element with the ordering and information to display in the list view UI element.
	 */
	private void initList(){
		settingList.add("First Name");
		settingList.add("Last Name");
		settingList.add("Phone");
		settingList.add("Email");
		settingList.add("Password");
		settingList.add("Car Image");
	}
	
}
