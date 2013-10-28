package com.llc.bumpr.adapters;

import java.util.List;

import org.jraf.android.backport.switchwidget.Switch;

import com.androidtools.Conversions;
import com.llc.bumpr.R;
import com.llc.bumpr.lib.CircularImageView;
import com.llc.bumpr.lib.GraphicsUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class SlidingMenuListAdapter extends BaseAdapter {
	private List<Pair<String, Object>> data;
	private LayoutInflater inflater;
	private Context context;

	public SlidingMenuListAdapter(Context context,
			List<Pair<String, Object>> inData) {
		data = inData;
		this.context = context; 
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
		// TODO Auto-generated method stub
		Object dataObj = data.get(position).second; // Get data for the row
		View view;

		if (data.get(position).first == "Image") { // Create Image Row
			ImageViewHolder holder;

			if (convertView == null) {
				ViewGroup vGroup = (ViewGroup) inflater.inflate(
						R.layout.sliding_menu_row_image, null);

				// Use the view holder pattern to save already looked up
				// subviews
				holder = new ImageViewHolder(
						(CircularImageView) vGroup.findViewById(R.id.iv_sl_menu_prof_pic),
						(TextView) vGroup.findViewById(R.id.tv_sl_menu_username));
				vGroup.setTag(holder);

				view = vGroup;
			} else {// If convert view exists!
				// get the holder back
				holder = (ImageViewHolder) convertView.getTag();
				view = convertView;
			}
			holder.textView.setText("Welcome, " + dataObj.toString() + "!");
			
			//Change size of image here
			/*float imageSize = Conversions.dpToPixels(context, 75);
			GraphicsUtil imageHelper = new GraphicsUtil();
			Bitmap bm = imageHelper.getCircleBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.test_image), 16);
			//Resize image to the desired size
			Bitmap resizedBM = Bitmap.createScaledBitmap(bm, Math.round(imageSize), Math.round(imageSize), false);
			holder.imageView.setImageBitmap(resizedBM);*/
			holder.imageView.setImageResource(R.drawable.test_image);
		} 
		else if (data.get(position).first == "Switch") { // Create Switch Row
			SwitchViewHolder holder;

			if (convertView == null) {
				ViewGroup vGroup = (ViewGroup) inflater.inflate(
						R.layout.sliding_menu_row_switch, null);

				// Use the view holder pattern to save already looked up
				// subviews
				holder = new SwitchViewHolder(
						(TextView) vGroup.findViewById(R.id.tv_sl_menu_driver_mode),
						(Switch) vGroup.findViewById(R.id.tb_sl_menu_switch));
				vGroup.setTag(holder);

				view = vGroup;
			} else {// If convert view exists!
				// get the holder back
				holder = (SwitchViewHolder) convertView.getTag();
				view = convertView;
			}
			holder.textView.setText(dataObj.toString());
			holder.switchView.setChecked(false);
		} 
		else { // Create Text Row
			TextViewHolder holder;

			if (convertView == null) {
				ViewGroup vGroup = (ViewGroup) inflater.inflate(
						R.layout.sliding_menu_row_text, null);

				// Use the view holder pattern to save already looked up
				// subviews
				holder = new TextViewHolder(
						(TextView) vGroup
								.findViewById(R.id.tv_sliding_menu_text));
				vGroup.setTag(holder);

				view = vGroup;
			} else {// If convert view exists!
				// get the holder back
				holder = (TextViewHolder) convertView.getTag();
				view = convertView;
			}
			holder.textView.setText(dataObj.toString());
		}

		return view; // Return view to display
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

		private TextViewHolder(TextView textView) {
			this.textView = textView;
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
	}

}
