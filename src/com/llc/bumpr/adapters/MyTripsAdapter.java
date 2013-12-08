package com.llc.bumpr.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.llc.bumpr.R;

public class MyTripsAdapter extends BaseAdapter {
	
	/** Reference to List holding data to be displayed */
	private List<Object> data;
	/** Reference to the application context */
	private Context context;
	/** Reference to the row layout to be displayed */
	private int layoutId;
	
	public MyTripsAdapter(Context context, List<Object> inData, int layoutId){
		data = inData;
		this.context = context;
		this.layoutId = layoutId;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int pos) {
		// TODO Auto-generated method stub
		return data.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		// TODO Auto-generated method stub
		return pos;
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
                holder = new ViewHolder((TextView)vGroup.findViewById(R.id.tv_username), (TextView)vGroup.findViewById(R.id.tv_cost),
                		(TextView) vGroup.findViewById(R.id.tv_start), (TextView) vGroup.findViewById(R.id.tv_end), 
                		(TextView)vGroup.findViewById(R.id.tv_date), (View) vGroup.findViewById(R.id.view_color));
                
                vGroup.setTag(holder);
                view = vGroup;
        }else{
        	holder = (ViewHolder)convertView.getTag();
        	view = convertView;
        }
        
        //Use class holder to and fill details with trip and driver details
        holder.start.setText("Cincinnati");
        holder.start.setTextSize(20f);
        holder.end.setText(data.get(position).toString());
        holder.end.setTextSize(20f);
        holder.tripCost.setText("23.45");
        holder.userName.setText("Kyle Cooper");
        holder.date.setText("December 6, 2013");
        
        //Hide color bar from left side
        holder.viewColor.setVisibility(View.GONE);
        
        return view;
	}

	/**View holders to improve performance of adapter! **/
	private static class ViewHolder { // Used to hold views per row in the
											// List
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
	
}
