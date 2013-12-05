package com.llc.bumpr.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.llc.bumpr.R;
import com.llc.bumpr.lib.CircularImageView;

public class MyRequestsAdapter extends BaseAdapter {
	/** Reference to List holding data to be displayed */
	private List<Object> data;
	/** Reference to the application context */
	private Context context;
	/** Reference to the row layout to be displayed */
	private int layoutId;
	/** Reference to LayoutInflater to display row */
	private LayoutInflater inflater;
	
	public MyRequestsAdapter(Context context, List<Object> inData, int layoutId){
		data = inData;
		this.context = context;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.layoutId = layoutId;
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 1; // 1 different view type used
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int pos) {
		// TODO Auto-generated method stub
		//Return the driver so the correct user is reviewed!
		return data.get(pos);
	}
	
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
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
        
        //Use class holder to and fill details with trip and driver details
        holder.startAdd.setText("Cincinnati, OH");
        holder.endAdd.setText(data.get(position).toString());
        holder.imageView.setImageResource(R.drawable.test_image);
        holder.userName.setText("Kyle Cooper");
        
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

}
