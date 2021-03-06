package com.llc.bumpr.adapters;

import java.util.List;

import org.jraf.android.backport.switchwidget.Switch;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.ion.Ion;
import com.llc.bumpr.R;
import com.llc.bumpr.lib.CircularImageView;
import com.llc.bumpr.sdk.lib.ApiRequest;
import com.llc.bumpr.sdk.models.Driver;
import com.llc.bumpr.sdk.models.Session;
import com.llc.bumpr.sdk.models.User;
import com.llc.bumpr.services.DriverLocationService;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Picasso.LoadedFrom;

public class SlidingMenuListAdapter extends BaseAdapter {
	/** List holding data to be displayed in the list */
	private List<Pair<String, Object>> data;
	
	/** LayoutInflater to inflate the rows */
	private LayoutInflater inflater;
	
	/** Context of the application */
	private Context context;
	
	/** User whose profile is being edited */
	private User user;

	/**A Target callback to handle the image view loading */
	private Target target;
	
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
		Object dataObj = data.get(position).second; 
		View view; 

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
								
				vGroup.setTag(holder); 

				view = vGroup; //Assign view to view group
			} else {// If convert view exists!
				// get the holder back
				holder = (ImageViewHolder) convertView.getTag(); 
				view = convertView; 
			}
			
			holder.textView.setText(dataObj.toString());
			loadImage(holder);
		} 
		else if (data.get(position).first == "Switch") { // Create Switch Row
			final SwitchViewHolder holder;
			final Switch switchView;

			if (convertView == null) { //If new row
				ViewGroup vGroup = (ViewGroup) inflater.inflate(
						R.layout.sliding_menu_row_switch, null); //Inflate row

				holder = new SwitchViewHolder(
						(TextView) vGroup.findViewById(R.id.tv_sl_menu_driver_mode),
						(Switch) vGroup.findViewById(R.id.tb_sl_menu_switch));
				vGroup.setTag(holder);
				
				switchView = holder.getSwitch(); 

				view = vGroup;
			} else {// If convert view exists!

				holder = (SwitchViewHolder) convertView.getTag();

				switchView = holder.getSwitch();
				view = convertView;
			}

			holder.textView.setText(dataObj.toString());
			if(user.getDriverProfile() == null) //If user is not driver, set checked to false
				holder.switchView.setChecked(false);
			else //Set initial switch value to current status 
				holder.switchView.setChecked(user.getDriverProfile().getStatus());
			
			switchView.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					
					//Verify the user is a registered driver
					if (user.getDriverProfile() != null){
					
						ApiRequest request = user.getDriverProfile().toggleStatusRequest(isChecked, new Callback<Driver>() {

							@Override
							public void failure(RetrofitError arg0) {
								Log.i("SlidingMenuListAdapter", "Fail response");
							}

							@Override
							public void success(Driver driver, Response response) {
								Log.i("SlidingMenuListAdapter", "Success response");

								//Start sending location updates
								if (driver.getStatus()) {
									Intent intent = new Intent(context, DriverLocationService.class);
									intent.putExtra(DriverLocationService.DRIVER, User.getActiveUser().getDriverProfile());
									context.startService(intent);
								} else { //Stop sending location updates
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
		else { 
			TextViewHolder holder;

			if (convertView == null) {//If new row, inflate row
				ViewGroup vGroup = (ViewGroup) inflater.inflate(
						R.layout.sliding_menu_row_text, null);

				holder = new TextViewHolder((TextView) vGroup.findViewById(R.id.tv_sliding_menu_text)
						,(ImageView) vGroup.findViewById(R.id.iv_row_icon));
				vGroup.setTag(holder);

				view = vGroup; 
			} else {// If convert view exists!

				holder = (TextViewHolder) convertView.getTag();
				view = convertView;
			}

			holder.textView.setText(dataObj.toString());

			if(dataObj.toString().equals("Start Trip")) {
				holder.icon.setImageResource(R.drawable.ic_create_trip);
				holder.icon.setVisibility(View.VISIBLE);
			}
			else if (dataObj.toString().equals("Trips")) {
				holder.icon.setImageResource(R.drawable.ic_trips);
				holder.icon.setVisibility(View.VISIBLE);
			}
			else if(dataObj.toString().equals("Inbox")) {
				holder.icon.setImageResource(R.drawable.ic_inbox);
				holder.icon.setVisibility(View.VISIBLE);
			}
			else if(dataObj.toString().equals("Outbox")) {
				holder.icon.setImageResource(R.drawable.ic_outbox);
				holder.icon.setVisibility(View.VISIBLE);
			}
			else
				holder.icon.setVisibility(View.GONE);
		}

		return view; 
	}
	
	private void loadImage(final ImageViewHolder holder) {
		target = new Target() {

			@Override
			public void onBitmapFailed(Drawable arg0) {
				//do nothing
			}

			@Override
			public void onBitmapLoaded(Bitmap arg0, LoadedFrom arg1) {
				holder.imageView.setImageBitmap(arg0);
			}

			@Override
			public void onPrepareLoad(Drawable arg0) {
				holder.imageView.setImageResource(R.drawable.missing);
			}
		};
		
		Picasso.with(context).load(User.getActiveUser().getProfileImage()+"?type=large").into(target);
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
		final ImageView icon;

		private TextViewHolder(TextView textView, ImageView icon) {
			this.textView = textView;
			this.icon = icon;
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
