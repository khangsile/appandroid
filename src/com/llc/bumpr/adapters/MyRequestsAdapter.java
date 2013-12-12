package com.llc.bumpr.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.llc.bumpr.R;
import com.llc.bumpr.lib.CircularImageView;
import com.llc.bumpr.sdk.models.Request;
import com.llc.bumpr.sdk.models.Trip;

public class MyRequestsAdapter extends ArrayAdapter<Request> {
	/** Reference to List holding data to be displayed */
	private List<Request> data;
	
	/** Reference to the application context */
	private Context context;
	
	/** Reference to the row layout to be displayed */
	private int layoutId;
	
	/** String that denotes the type of adapter */
	private String type;
	
	public MyRequestsAdapter(Context context, List<Request> inData, int layoutId, String type){
		super(context, layoutId, inData);
		data = inData;
		this.context = context;
		this.layoutId = layoutId;
		this.type = type;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder; //Reference to view holder
        View view; //Reference to View
        
        if (convertView == null) { //If new view, inflate view
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                ViewGroup vGroup = (ViewGroup) inflater.inflate(layoutId, null);
                
                //Create new ViewHolder object
                holder = new ViewHolder((CircularImageView)vGroup.findViewById(R.id.iv_profile_pic), (TextView)vGroup.findViewById(R.id.tv_start_add), 
                				(TextView)vGroup.findViewById(R.id.tv_end_add), (TextView)vGroup.findViewById(R.id.tv_driver_name));
                
                vGroup.setTag(holder);
                view = vGroup;
        }else{
        	holder = (ViewHolder)convertView.getTag();
        	view = convertView;
        }
        
        Request r = data.get(position);
        Trip t = r.getTrip();
        
        //Use class holder to and fill details with trip and driver details
        holder.startAdd.setText(compressTitle(t.getStart().title));
        holder.endAdd.setText(compressTitle(t.getEnd().title));
        holder.imageView.setImageResource(R.drawable.test_image);
        if (type.equals("Inbox")) {
        	holder.userName.setText(r.getUser().getFirstName() + " " + r.getUser().getLastName());
        } else {
        	holder.userName.setText(t.getOwner().getFirstName() + " " + t.getOwner().getLastName());
        }
        
        return view;
	}
	
	/**View holders to improve performance of adapter! **/
	private static class ViewHolder { // Used to hold views per row in the
											// List
		final CircularImageView imageView;
		final TextView userName;
		final TextView startAdd;
		final TextView endAdd;

		private ViewHolder(CircularImageView imageView, TextView startAdd, TextView endAdd, TextView userName) {
			this.imageView = imageView;
			this.startAdd = startAdd;
			this.endAdd = endAdd;
			this.userName = userName;
		}
	}
	
	private String compressTitle(String title) {
		String[] titles = title.split(",");
		if (titles.length > 3) {
			return titles[1] + titles[2];
		} else if (titles.length > 2){
			return titles[0] + titles[1];
		} else {
			return titles[0];
		}
	}

}
