package com.llc.bumpr.adapters;

import java.text.DecimalFormat;
import java.util.List;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.llc.bumpr.R;
import com.llc.bumpr.sdk.models.Trip;

public class MyTripsAdapter extends ArrayAdapter<Trip> {
	
	/** Reference to List holding data to be displayed */
	private List<Trip> data;
	
	/** Reference to the application context */
	private Context context;
	
	/** Reference to the row layout to be displayed */
	private int layoutId;
	
	public MyTripsAdapter(Context context, List<Trip> inData, int layoutId){
		super(context, layoutId, inData);
		data = inData;
		this.context = context;
		this.layoutId = layoutId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder; 
        View view; 
        
        if (convertView == null) { //If new view, inflate view
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                ViewGroup vGroup = (ViewGroup) inflater.inflate(layoutId, null);
                
                holder = new ViewHolder((TextView)vGroup.findViewById(R.id.tv_username), (TextView)vGroup.findViewById(R.id.tv_cost),
                		(TextView) vGroup.findViewById(R.id.tv_start), (TextView) vGroup.findViewById(R.id.tv_end), 
                		(TextView)vGroup.findViewById(R.id.tv_date), (View) vGroup.findViewById(R.id.view_color));
                
                vGroup.setTag(holder);
                view = vGroup;
        }else{
        	holder = (ViewHolder)convertView.getTag();
        	view = convertView;
        }
        
        Trip t = data.get(position);
        
        //Use class holder to and fill details with trip and driver details
        holder.start.setText(compressTitle(t.getStart().title));
        holder.start.setTextSize(20f);
        holder.end.setText(compressTitle(t.getEnd().title));
        holder.end.setTextSize(20f);
        
        double cost = t.getCost(); //Get cost and display with two decimal points
        holder.tripCost.setText("$ " + new DecimalFormat("##.##").format(cost));
        
        holder.userName.setText(t.getOwner().getFirstName() +  " " + t.getOwner().getLastName());
        java.text.DateFormat format = DateFormat.getMediumDateFormat(context);
        holder.date.setText(format.format(t.getDate()));
        
        //Hide color bar from left side
        holder.viewColor.setVisibility(View.GONE);
        
        return view;
	}

	/**View holders to improve performance of adapter! **/
	private static class ViewHolder { // Used to hold views per row in the list
		final TextView userName;
		final TextView tripCost;
		final TextView start;
		final TextView end;
		final TextView date;
		final View viewColor;
	
		private ViewHolder(TextView userName, TextView tripCost, TextView start, TextView end, TextView date, View viewColor) {
			this.start = start;
			this.end = end;
			this.userName = userName;
			this.tripCost = tripCost;
			this.date = date;
			this.viewColor = viewColor;
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
