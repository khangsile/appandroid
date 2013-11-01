package com.llc.bumpr.adapters;

import java.util.List;

import android.content.Context;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.llc.bumpr.R;
import com.llc.bumpr.lib.DynamicImageView;
import com.llc.bumpr.sdk.models.User;

public class EditProfileListAdapter extends BaseAdapter {
		private List<String> data;
		private LayoutInflater inflater;
		private Context context;
		private User user;

		public EditProfileListAdapter(Context context,
				List<String> inData, User user) {
			data = inData;
			this.context = context;
			this.user = user;
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getItemViewType(int position) {
			// TODO Auto-generated method stub
			if (!(data.get(position).equals("Password")) && !(data.get(position).equals("Driver Settings")))
				return 0; //EditText Row
			else if (data.get(position).equals("Password"))
				return 1; //Password row -- Take to a new activity. 
			else
				return 2; //Driver Settings Row, hide the password text view
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 3; // two different view types used
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view;

			if (!(data.get(position).equals("Password")) && !(data.get(position).equals("Driver Settings"))) { // Create TextView Row
				TextViewHolder holder;

				if (convertView == null) {
					ViewGroup vGroup = (ViewGroup) inflater.inflate(
							R.layout.edit_profile_text_row, null);

					// Use the view holder pattern to save already looked up
					// subviews
					holder = new TextViewHolder(
							(TextView) vGroup.findViewById(R.id.tv_edit_prof_text),
							(EditText) vGroup.findViewById(R.id.et_edit_prof_value));
					vGroup.setTag(holder);

					view = vGroup;
				} else {// If convert view exists!
					// get the holder back
					holder = (TextViewHolder) convertView.getTag();
					view = convertView;
				}
				holder.textView.setText(data.get(position) + ":");
				
				if(data.get(position).equals("First Name")) {
					holder.editText.setText(user.getFirstName());
					holder.editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
				}
				else if (data.get(position).equals("Last Name")) {
					holder.editText.setText(user.getLastName());
					holder.editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
				}
				else if (data.get(position).equals("Phone")){
					holder.editText.setText(user.getPhoneNumber());
					holder.editText.setInputType(InputType.TYPE_CLASS_PHONE);
				}
				else if (data.get(position).equals("Email")){
					holder.editText.setText(user.getEmail());
					holder.editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
				}
				else if (data.get(position).equals("Make")){
					holder.editText.setText("Toyota");
					holder.editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
				}
				else if (data.get(position).equals("Model")){
					holder.editText.setText("Hybrid Camry");
					holder.editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
				}
				else if (data.get(position).equals("Year")){
					holder.editText.setText("2013");
					holder.editText.setInputType(InputType.TYPE_CLASS_NUMBER);
				}
				else if (data.get(position).equals("Passenger Seats")){
					holder.editText.setText("4");
					holder.editText.setInputType(InputType.TYPE_CLASS_NUMBER);
				}
				
			} 
			else if (data.get(position).equals("Password")) { 
				ViewGroup vGroup = (ViewGroup) inflater.inflate(R.layout.edit_profile_activity_row, null);
				view = vGroup;
			}
			else{//Create Car Image Row
				DriverSettingsViewHolder holder;

				if (convertView == null) {
					ViewGroup vGroup = (ViewGroup) inflater.inflate(
							R.layout.edit_profile_activity_row, null);

					// Use the view holder pattern to save already looked up
					// subviews
					holder = new DriverSettingsViewHolder(
							(TextView) vGroup.findViewById(R.id.tv_edit_prof_text),
							(TextView) vGroup.findViewById(R.id.tv_edit_prof_val),
							(ImageView) vGroup.findViewById(R.id.iv_edit_prof_icon));
					vGroup.setTag(holder);

					view = vGroup;
				} else {// If convert view exists!
					// get the holder back
					holder = (DriverSettingsViewHolder) convertView.getTag();
					view = convertView;
				}
				holder.passTitle.setText("Driver Settings:");
				holder.passText.setText("");
			}
			return view; // Return view to display
		}

		private static class TextViewHolder { // Used to hold views per row in the
												// List
			final TextView textView;
			final EditText editText;

			private TextViewHolder(TextView textView, EditText editText) {
				this.textView = textView;
				this.editText = editText;
			}
		}
		
		private static class DriverSettingsViewHolder {
			final TextView passTitle;
			final TextView passText;
			ImageView icon;
			
			private DriverSettingsViewHolder(TextView passTitle, TextView passText, ImageView icon) {
			this.passTitle = passTitle;
			this.passText = passText;
			this.icon = icon;
			}
		}
}
