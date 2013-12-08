package com.llc.bumpr.adapters;

import java.util.ArrayList;

import com.llc.bumpr.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SearchLocationAdapter extends ArrayAdapter<String> {

	private Activity context;
	private ArrayList<String> list;
	private int layoutId;
	private int minRowHeight = 30;

	/*************************** View Holder ***************************/

	static class ViewHolder {
		private TextView title;
		private TextView address;
	}

	public SearchLocationAdapter(Activity context, int layoutId, ArrayList<String> list) {
		super(context, layoutId, list);
		this.context = context;
		this.list = list;
		this.layoutId = layoutId;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		View view;

		if (convertView == null) { // If new row, inflate row
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(layoutId, null);

			holder = new ViewHolder();
			
			holder.address = (TextView) view.findViewById(R.id.tv_address);
			holder.title = (TextView) view.findViewById(R.id.tv_title);
			
			view.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
			view = convertView;
		}

		view.setMinimumHeight(minRowHeight);
		
		holder.address.setText(list.get(position));
				
		return view;
	}

}
