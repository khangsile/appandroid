package com.llc.bumpr.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.llc.bumpr.R;
import com.llc.bumpr.sdk.models.Trip;
import com.llc.bumpr.sdk.models.User;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.Target;

public class GuestListAdapter extends ArrayAdapter<User> {
	/** List to hold all of the users */
	private static List<User> users = new ArrayList<User>();
	/** Reference to the context of the application */
	private Context context;
	/** Reference to the row layout */
	private int layoutId;
	/** Reference to the trip being displayed */
	private Trip trip;
	
	/** Target **/
	private Target target;
	

	public GuestListAdapter(Context context,int layoutId, Trip trip) {
		super(context, layoutId, users);
		this.context = context;
		this.layoutId = layoutId;
		this.trip = trip;
		
		GuestListAdapter.users.clear();
		GuestListAdapter.users.add(trip.getOwner());
		if(trip.getUsers() != null)
			GuestListAdapter.users.addAll(trip.getUsers());
		for(int i = 0; i < GuestListAdapter.users.size(); i++)
			Log.i("com.llc.bumpr", GuestListAdapter.users.get(i).getFirstName());
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder; 
        View view; 
        
        if (convertView == null) { //If new view, inflate view
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                ViewGroup vGroup = (ViewGroup) inflater.inflate(layoutId, null);
                
                holder = new ViewHolder((ImageView)vGroup.findViewById(R.id.iv_guest_pic), (TextView)vGroup.findViewById(R.id.tv_guest_name),
                		(TextView) vGroup.findViewById(R.id.tv_num_pass), (RelativeLayout) vGroup.findViewById(R.id.rl_guest_row));
                
                vGroup.setTag(holder);
                view = vGroup;
        }else{
        	holder = (ViewHolder)convertView.getTag();
        	view = convertView;
        }
        
        User u = users.get(position);
        
        if(position == 0) //Trip Host
        	holder.row.setBackgroundResource(R.color.ghost_white);
        else
        	holder.row.setBackgroundColor(0xefefef);
        
        //Use class holder to and fill details with trip and driver details
        holder.guestName.setText(u.getFirstName() + " " + u.getLastName());
        holder.guestName.setTextSize(20f);
        holder.passCnt.setVisibility(View.GONE);
        loadImage(holder, u.getProfileImage() + "?type=large");
        //holder.passCnt.setText(compressTitle(t.getEnd().title));
        //holder.passCnt.setTextSize(20f);
        
        return view;
	}

	/**View holders to improve performance of adapter! **/
	private static class ViewHolder { // Used to hold views per row in the list
		final ImageView userPic;
		final TextView guestName;
		final TextView passCnt;
		final RelativeLayout row;
	
		private ViewHolder(ImageView userPic, TextView guestName, TextView passCnt, RelativeLayout row) {
			this.userPic = userPic;
			this.guestName = guestName;
			this.passCnt = passCnt;
			this.row = row;
		}
	}
	
	private void loadImage(final ViewHolder holder, String url) {
		target = new Target() {

			@Override
			public void onBitmapFailed(Drawable arg0) {
				//do nothing
			}

			@Override
			public void onBitmapLoaded(Bitmap arg0, LoadedFrom arg1) {
				holder.userPic.setImageBitmap(arg0);
			}

			@Override
			public void onPrepareLoad(Drawable arg0) {
				holder.userPic.setImageResource(R.drawable.missing);
			}
		};
		
		Picasso.with(context).load(url).into(target);
	}
	
}
