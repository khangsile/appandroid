package com.llc.bumpr.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.llc.bumpr.R;
import com.llc.bumpr.sdk.models.Trip;

public class SearchTripsAdapter extends ArrayAdapter<Trip> {

	private Activity context;
	private ArrayList<Trip> list;
	private ArrayList<Color> colors;
	private int layoutId;

	/*************************** View Holder ***************************/

	static class ViewHolder {
		public TextView tvStart;
		public TextView tvEnd;
		public TextView tvUsername;
		public TextView tvDate;
		public TextView tvCost;
		public View color;
	}

	public SearchTripsAdapter(Activity context, int layoutId, ArrayList<Trip> list, ArrayList<Color> colors) {
		super(context, layoutId, list);
		this.context = context;
		this.list = list;
		this.layoutId = layoutId;
		this.colors = colors;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		View view;

		if (convertView == null) { // If new row, inflate row
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(layoutId, null);

			holder = new ViewHolder();
			holder.tvCost = (TextView) view.findViewById(R.id.tv_cost);
			holder.tvUsername = (TextView) view.findViewById(R.id.tv_username);
			holder.tvDate = (TextView) view.findViewById(R.id.tv_date);
			holder.tvStart = (TextView) view.findViewById(R.id.tv_start);
			holder.tvEnd = (TextView) view.findViewById(R.id.tv_end);
			holder.color = view.findViewById(R.id.view_color);

			view.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
			view = convertView;
		}
		
		Trip trip = (Trip) list.get(position);

		holder.tvUsername.setText("Khang Le");
		holder.tvCost.setText("$35");
		holder.tvDate.setText("December 12, 2013");
		holder.tvStart.setText("CIN");
		holder.tvEnd.setText("NYC");
		
		int red = (int) (Math.random() * 240);
		int blue = (int) (Math.random() * 255);
		int green = (int) (Math.random() * 255);
		
		holder.color.setBackgroundColor(Color.rgb(red, green, blue));
				
		return view;
	}

}
