package com.llc.bumpr;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.util.Pair;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockActivity;
import com.llc.bumpr.adapters.EditProfileListAdapter;
import com.llc.bumpr.lib.CircularImageView;
import com.llc.bumpr.lib.DynamicImageView;
import com.llc.bumpr.sdk.models.User;

public class EditProfileActivity extends SherlockActivity {
	private List<String> settingList;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_profile);
		
		CircularImageView profPic = (CircularImageView) findViewById(R.id.iv_profile_pic);
		DynamicImageView carImage = (DynamicImageView) findViewById(R.id.iv_car_pic);
		ListView profSettings = (ListView) findViewById(R.id.lv_settings_list);
		
		//Set image resources
		profPic.setImageResource(R.drawable.test_image);
		carImage.setImageResource(R.drawable.test_car_image);
		
		settingList = new ArrayList<String>();
		
		initList();
		EditProfileListAdapter adt = new EditProfileListAdapter(this, settingList, User.getActiveUser());
		profSettings.setAdapter(adt);
	}
	
	private void initList(){
		settingList.add("First Name");
		settingList.add("Last Name");
		settingList.add("Phone");
		settingList.add("Phone Number");
		settingList.add("Password");
	}
	
}
