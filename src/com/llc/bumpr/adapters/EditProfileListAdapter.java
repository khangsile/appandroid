package com.llc.bumpr.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.llc.bumpr.R;
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
			if (position != data.size()-1)
				return 0; //EditText Row
			else
				return 1; //Password row -- Take to a new activity
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 2; // Three different view types used
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
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

			if (position != data.size()-1) { // Create TextView Row
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
				switch(position){
				case 1:
					holder.textView.setText("First Name:");
					holder.editText.setText(user.getFirstName());
					break;
				case 2:
					holder.textView.setText("Last Name:");
					holder.editText.setText(user.getLastName());
					break;
				case 3:
					holder.textView.setText("Phone:");
					holder.editText.setText(user.getPhoneNumber());
					break;
				case 4:
					holder.textView.setText("Email:");
					holder.editText.setText(user.getEmail());
					break;
				}
			} 
			else { 
				ViewGroup vGroup = (ViewGroup) inflater.inflate(R.layout.edit_profile_activity_row, null);
				view = vGroup;
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
}
