package com.llc.bumpr.adapters;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.llc.bumpr.R;
import com.llc.bumpr.lib.DynamicImageView;
import com.llc.bumpr.sdk.models.User;

public class EditProfileListAdapter extends BaseAdapter {
		/** List holding data to be displayed in the list */
		private List<String> data;
		/** LayoutInflater to inflate the rows */
		private LayoutInflater inflater;
		/** Context of the application */
		private Context context;
		/** User whose profile is being edited */
		private User user;

		/**
		 * Constructor to set up the adapter
		 * @param context Application Context
		 * @param inData List of data
		 * @param user User who is being edited
		 */
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
			//If the row text is not password or driver settings
			if (!(data.get(position).equals("Password")) && !(data.get(position).equals("Driver Settings")))
				return 0; //EditText Row
			else if (data.get(position).equals("Password")) //If the text is password
				return 1; //Password row -- Take to a new activity. 
			else //Else, it is a driver settings row
				return 2; //Driver Settings Row
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 3; // three different view types used
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
			View view; //Reference to the view that was created

			if (!(data.get(position).equals("Password")) && !(data.get(position).equals("Driver Settings"))) { // Create TextView Row
				final TextViewHolder holder; //Reference to the TextViewHolder

				if (convertView == null) { //If this is a new row being created
					ViewGroup vGroup = (ViewGroup) inflater.inflate(
							R.layout.edit_profile_text_row, null); //Inflat the row

					// Use the view holder pattern to save already looked up
					// subviews
					holder = new TextViewHolder(
							(TextView) vGroup.findViewById(R.id.tv_edit_prof_text),
							(EditText) vGroup.findViewById(R.id.et_edit_prof_value));
					vGroup.setTag(holder); //Set the tag

					view = vGroup; //Assign the view to the view group
				} else {// If convert view exists
					// get the holder back using get tag
					holder = (TextViewHolder) convertView.getTag();
					view = convertView; //Assign view to convert view
				}
				//Assign text for text view
				holder.textView.setText(data.get(position) + ":");
				
				//Fill in data depending on which row type it is, and set the keyboard depending on the type
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
					holder.editText.setText("");
					holder.editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
				}
				else if (data.get(position).equals("Model")){
					holder.editText.setText("");
					holder.editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
				}
				else if (data.get(position).equals("Year")){
					holder.editText.setText("");
					holder.editText.setInputType(InputType.TYPE_CLASS_NUMBER);
				}
				else if (data.get(position).equals("Passenger Seats")){
					holder.editText.setText("");
					holder.editText.setInputType(InputType.TYPE_CLASS_NUMBER);
				}
				else if (data.get(position).equals("Fee")){
					holder.editText.setText(Double.toString(user.getDriverProfile().getFee()));
					holder.editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
				}
				
			} 
			else if (data.get(position).equals("Password")) { //Password row
				//Inflate the row and assign the view to the view group
				ViewGroup vGroup = (ViewGroup) inflater.inflate(R.layout.edit_profile_activity_row, null);
				view = vGroup;
			}
			else{//Driver Details Row -- Takes you to edit driver settings
				DriverSettingsViewHolder holder; //Reference to Driver Settings View Holder

				if (convertView == null) { //If new row
					//Inflate row
					ViewGroup vGroup = (ViewGroup) inflater.inflate(
							R.layout.edit_profile_activity_row, null);

					// Use the view holder pattern to save already looked up
					// subviews
					holder = new DriverSettingsViewHolder(
							(TextView) vGroup.findViewById(R.id.tv_edit_prof_text),
							(TextView) vGroup.findViewById(R.id.tv_edit_prof_val),
							(ImageView) vGroup.findViewById(R.id.iv_edit_prof_icon));
					vGroup.setTag(holder); //Set view tag

					view = vGroup; //assign view to the view group
				} else {// If convert view exists!
					// get the holder back using get tag
					holder = (DriverSettingsViewHolder) convertView.getTag();
					view = convertView; //Assign view
				}
				//Set left text view text but clear right text view text
				holder.passTitle.setText("Driver Settings:");
				holder.passText.setText("");
			}
			return view; // Return view to display
		}

		public static class TextViewHolder { // Used to hold views per row in the List
			/** Reference to text view in the row */
			final TextView textView;
			/** Reference to Edit Text in row */
			final EditText editText;

			/**
			 * Constructor to TextViewHolder
			 * @param textView Reference to TextView in the layout
			 * @param editText Reference to the EditText in the layout
			 */
			private TextViewHolder(TextView textView, EditText editText) {
				this.textView = textView;
				this.editText = editText;
			}
			
			/**
			 * @return reference to the edit text box
			 */
			public EditText getEditText(){
				return editText;
			}
		}
		
		private static class DriverSettingsViewHolder {
			/** Reference to the Title text view */
			final TextView passTitle;
			/** Reference to right text view -- Always empty */
			final TextView passText;
			/** Reference to icon to be displayed */
			ImageView icon;
			
			/**
			 * Constructor for DriverSettingsViewHolder
			 * @param passTitle Reference to the Title text view
			 * @param passText Reference to the Right text view
			 * @param icon Reference to the Icon to be displayed
			 */
			private DriverSettingsViewHolder(TextView passTitle, TextView passText, ImageView icon) {
			this.passTitle = passTitle;
			this.passText = passText;
			this.icon = icon;
			}
		}
}
