package com.llc.bumpr.adapters;

import java.text.DecimalFormat;
import java.util.List;

import com.llc.bumpr.R;
import com.llc.bumpr.sdk.models.Trip;
import com.llc.bumpr.sdk.models.User;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GuestListAdapter extends ArrayAdapter<User> {
	/** List to hold all of the users */
	private List<User> users;
	/** Reference to the context of the application */
	private Context context;
	/** Reference to the row layout */
	private int layoutId;
	/** Reference to the trip being displayed */
	private Trip trip;
	

	public GuestListAdapter(Context context,int layoutId, Trip trip) {
		super(context, layoutId);
		this.context = context;
		this.layoutId = layoutId;
		this.trip = trip;
		//this.users = trip.getUsers();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder; 
        View view; 
        
        if (convertView == null) { //If new view, inflate view
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                ViewGroup vGroup = (ViewGroup) inflater.inflate(layoutId, null);
                
                holder = new ViewHolder((ImageView)vGroup.findViewById(R.id.iv_guest_pic), (TextView)vGroup.findViewById(R.id.tv_guest_name),
                		(TextView) vGroup.findViewById(R.id.tv_num_pass));
                
                vGroup.setTag(holder);
                view = vGroup;
        }else{
        	holder = (ViewHolder)convertView.getTag();
        	view = convertView;
        }
        
        User u = users.get(position);
        
        //Use class holder to and fill details with trip and driver details
        holder.guestName.setText(u.getFirstName() + " " + u.getLastName());
        holder.guestName.setTextSize(20f);
        holder.passCnt.setVisibility(View.GONE);
        //holder.passCnt.setText(compressTitle(t.getEnd().title));
        //holder.passCnt.setTextSize(20f);
        
        return view;
	}

	/**View holders to improve performance of adapter! **/
	private static class ViewHolder { // Used to hold views per row in the list
		final ImageView userPic;
		final TextView guestName;
		final TextView passCnt;
	
		private ViewHolder(ImageView userPic, TextView guestName, TextView passCnt) {
			this.userPic = userPic;
			this.guestName = guestName;
			this.passCnt = passCnt;
		}
	}
	
}
