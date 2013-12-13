package com.llc.bumpr.adapters;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.llc.bumpr.R;
import com.llc.bumpr.lib.Colors;
import com.llc.bumpr.sdk.models.Trip;

public class SearchTripsAdapter extends ArrayAdapter<Trip> {

	private Activity context;
	private ArrayList<Trip> list;
	private ArrayList<Color> colors;
	private int layoutId;

	/*************************** View Holder ***************************/

	private static class ViewHolder {
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
            ViewGroup vGroup = (ViewGroup) inflater.inflate(layoutId, null);

			holder = new ViewHolder();
			holder.tvCost = (TextView) vGroup.findViewById(R.id.tv_cost);
			holder.tvUsername = (TextView) vGroup.findViewById(R.id.tv_username);
			holder.tvDate = (TextView) vGroup.findViewById(R.id.tv_date);
			holder.tvStart = (TextView) vGroup.findViewById(R.id.tv_start);
			holder.tvEnd = (TextView) vGroup.findViewById(R.id.tv_end);
			holder.color = vGroup.findViewById(R.id.view_color);

			vGroup.setTag(holder);
			view = vGroup;
		} else {
			holder = (ViewHolder) convertView.getTag();
			view = convertView;
		}
		
		Trip trip = (Trip) list.get(position);

		holder.tvUsername.setText(trip.getOwner().getFirstName() + " " + trip.getOwner().getLastName());
		
		double cost = trip.getCost(); //Get cost and display with two decimal points
		holder.tvCost.setText("$" + new DecimalFormat("##.##").format(cost));
		String start = trip.getStart().title;
		holder.tvStart.setText(compressTitle(start));
		String end = trip.getEnd().title;
		holder.tvEnd.setText(compressTitle(end));
		java.text.DateFormat format = DateFormat.getMediumDateFormat(context);
        holder.tvDate.setText(format.format(trip.getDate()));
		
		holder.color.setBackgroundColor(Colors.getColor(position));
		
		return view;
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
