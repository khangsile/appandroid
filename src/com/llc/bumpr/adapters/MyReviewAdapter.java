package com.llc.bumpr.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.llc.bumpr.R;

public class MyReviewAdapter extends ArrayAdapter<Object> {
	/** List reference to hold data to be displayed */
	private List<Object> reviewList;
	/** Reference to the application context */
	private Context context;
	/** Reference to the row layout to be displayed */
	private int layoutId;

	public MyReviewAdapter(Context context, List<Object> reviewList, int layoutId) {
		super(context, layoutId, reviewList);
		this.context = context;
		this.reviewList = reviewList;
		this.layoutId = layoutId;
	}
	

	@Override
    public int getCount() {                
            return reviewList.size();
    }

    @Override
    public String getItem(int position) {                
            return (String) reviewList.get(position);
    }

    @Override
    public long getItemId(int position) {                
            return reviewList.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder; //Reference to view holder
            View view; //Reference to view to be displayed
            
            if (convertView == null) { //If new row, inflate the row
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    ViewGroup vGroup = (ViewGroup) inflater.inflate(layoutId, null);
                    
                    //Create a view holder 
                    holder = new ViewHolder((TextView)vGroup.findViewById(R.id.tv_review_name), 
                    				(RatingBar)vGroup.findViewById(R.id.rb_user_rating), 
                    				(TextView) vGroup.findViewById(R.id.tv_review_text));
                    
                    vGroup.setTag(holder); //Set the tag
                    view = vGroup;
            }else{
            	//Get view holder back from tag
            	holder = (ViewHolder)convertView.getTag();
            	view = convertView;
            }
            
            //Set row values from user details 
            holder.rvName.setText("Kyle Cooper");
            holder.rvRtBar.setRating(3.2f);
            holder.rvText.setText(reviewList.get(position).toString());
            
            return view;
    }
    
    
	/**View holders to improve performance of adapter! **/
	private static class ViewHolder { // Used to hold views per row in the
											// List
		final TextView rvName;
		final RatingBar rvRtBar;
		final TextView rvText;

		private ViewHolder(TextView rvName, RatingBar rvRtBar, TextView rvText) {
			this.rvName = rvName;
			this.rvRtBar = rvRtBar;
			this.rvText = rvText;
		}
	}
}
