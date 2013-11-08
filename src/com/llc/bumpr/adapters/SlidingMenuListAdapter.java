package com.llc.bumpr.adapters;

import java.util.List;

import org.jraf.android.backport.switchwidget.Switch;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.llc.bumpr.R;
import com.llc.bumpr.lib.CircularImageView;
import com.llc.bumpr.sdk.lib.ApiRequest;
import com.llc.bumpr.sdk.models.Driver;
import com.llc.bumpr.sdk.models.Session;
import com.llc.bumpr.sdk.models.User;
import com.llc.bumpr.services.DriverLocationService;

public class SlidingMenuListAdapter extends BaseAdapter {
	/** List holding data to be displayed in the list */
	private List<Pair<String, Object>> data;
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
	public SlidingMenuListAdapter(Context context,
			List<Pair<String, Object>> inData, User user) {
		data = inData;
		this.context = context; 
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.user = user;
	}

	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		if (data.get(position).first == "Image")
			return 0; // Use Image Row layout
		else if (data.get(position).first == "Switch")
			return 1; // Use switch row layout
		else
			return 2; // Use Text Row Layout
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 3; // Three different view types used
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position).second;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Object dataObj = data.get(position).second; // Get data for the row
		View view; //Reference to row to be displayed

		if (data.get(position).first == "Image") { // Create Image Row
			ImageViewHolder holder;

			if (convertView == null) { //New row being created
				ViewGroup vGroup = (ViewGroup) inflater.inflate(
						R.layout.sliding_menu_row_image, null); //Inflate the row

				// Use the view holder pattern to save already looked up
				// subviews
				holder = new ImageViewHolder(
						(CircularImageView) vGroup.findViewById(R.id.iv_sl_menu_prof_pic),
						(TextView) vGroup.findViewById(R.id.tv_sl_menu_username));
				vGroup.setTag(holder); //Set the tag

				view = vGroup; //Assign view to view group
			} else {// If convert view exists!
				// get the holder back
				holder = (ImageViewHolder) convertView.getTag(); //Get holder from tag
				view = convertView; //Assign view from convertview
			}
			//Set data in row
			holder.textView.setText(dataObj.toString());
			holder.imageView.setImageResource(R.drawable.test_image);
		} 
		else if (data.get(position).first == "Switch") { // Create Switch Row
			//Reference to the view holder and the switch view
			final SwitchViewHolder holder;
			final Switch switchView;

			if (convertView == null) { //If new row
				ViewGroup vGroup = (ViewGroup) inflater.inflate(
						R.layout.sliding_menu_row_switch, null); //Inflate row

				// Use the view holder pattern to save already looked up
				// subviews
				holder = new SwitchViewHolder(
						(TextView) vGroup.findViewById(R.id.tv_sl_menu_driver_mode),
						(Switch) vGroup.findViewById(R.id.tb_sl_menu_switch));
				vGroup.setTag(holder);
				
				switchView = holder.getSwitch(); //Assign switch reference for the row

				view = vGroup;
			} else {// If convert view exists!
				// get the holder back
				holder = (SwitchViewHolder) convertView.getTag();
				//Assign the switch
				switchView = holder.getSwitch();
				view = convertView;
			}
			//Set the row text and check status
			holder.textView.setText(dataObj.toString());
			if(user.getDriverProfile() == null) //If user is not driver, set checked to false
				holder.switchView.setChecked(false);
			else //Set initial switch value to current status 
				holder.switchView.setChecked(user.getDriverProfile().getStatus());
			
			switchView.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					//Verify the user is a registered driver
					if (user.getDriverProfile() != null){
						//Toggle driver mode
						Log.i("Mein Tag", isChecked + " ");
						
						ApiRequest request = user.getDriverProfile().toggleStatusRequest(isChecked, new Callback<Driver>() {

							@Override
							public void failure(RetrofitError arg0) {
								Log.i("SlidingMenuListAdapter", "Fail response");
							}

							@Override
							public void success(Driver driver, Response response) {
								Log.i("SlidingMenuListAdapter", "Success response");

								if (driver.getStatus()) {
									Intent intent = new Intent(context, DriverLocationService.class);
									intent.putExtra(DriverLocationService.DRIVER, User.getActiveUser().getDriverProfile());
									context.startService(intent);
								} else {
									Intent intent = new Intent(context, DriverLocationService.class);
									context.stopService(intent);
								}
							}
							
						});
						Session.getSession().sendRequest(request);
					} 
					else{
						switchView.setChecked(false); //Not working for some reason
						Toast.makeText(context, "Please register as driver before using this feature", Toast.LENGTH_SHORT).show();
					}
				}
				
			});
		} 
		else { // Create Text Row
			TextViewHolder holder;

			if (convertView == null) {
				ViewGroup vGroup = (ViewGroup) inflater.inflate(
						R.layout.sliding_menu_row_text, null);

				// Use the view holder pattern to save already looked up
				// subviews
				holder = new TextViewHolder(
						(TextView) vGroup
								.findViewById(R.id.tv_sliding_menu_text));
				vGroup.setTag(holder);

				view = vGroup;
			} else {// If convert view exists!
				// get the holder back
				holder = (TextViewHolder) convertView.getTag();
				view = convertView;
			}
			holder.textView.setText(dataObj.toString());
		}

		return view; // Return view to display
	}

	/**View holders to improve performance of adapter! **/
	private static class ImageViewHolder { // Used to hold views per row in the
											// List
		final CircularImageView imageView;
		final TextView textView;

		private ImageViewHolder(CircularImageView imageView, TextView textView) {
			this.imageView = imageView;
			this.textView = textView;
		}
	}

	private static class TextViewHolder { // Used to hold views per row in the
											// List
		final TextView textView;

		private TextViewHolder(TextView textView) {
			this.textView = textView;
		}
	}

	private static class SwitchViewHolder { // Used to hold views per row in the
											// List
		final TextView textView;
		final Switch switchView;

		private SwitchViewHolder(TextView textView, Switch switchView) {
			this.textView = textView;
			this.switchView = switchView;
		}
		
		private Switch getSwitch(){
			return switchView;
		}
	}

}
