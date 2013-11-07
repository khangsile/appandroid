package com.llc.bumpr.adapters;

import java.util.List;

import com.androidtools.Conversions;
import com.llc.bumpr.R;
import com.llc.bumpr.lib.CircularImageView;
import com.llc.bumpr.lib.GraphicsUtil;
import com.llc.bumpr.sdk.models.User;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

public class MyReviewAdapter extends ArrayAdapter<Object> {
	private List<Object> reviewList;
	private Context context;
	private int layoutId;

	public MyReviewAdapter(Context context, List<Object> reviewList, int layoutId) {
		super(context, layoutId, reviewList);
		this.context = context;
		this.reviewList = reviewList;
		this.layoutId = layoutId;
	}
	

	@Override
    public int getCount() {                
            return reviewList.size() ;
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
            ViewHolder holder;
            View view;
            
            if (convertView == null) {
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    ViewGroup vGroup = (ViewGroup) inflater.inflate(layoutId, null);
                    
                    holder = new ViewHolder((TextView)vGroup.findViewById(R.id.tv_review_name), 
                    				(RatingBar)vGroup.findViewById(R.id.rb_user_rating), 
                    				(TextView) vGroup.findViewById(R.id.tv_review_text));
                    
                    vGroup.setTag(holder);
                    view = vGroup;
            }else{
            	holder = (ViewHolder)convertView.getTag();
            	view = convertView;
            }
            
            holder.rvName.setText("Kyle Cooper");
            holder.rvRtBar.setRating(3.2f);
            holder.rvText.setText(reviewList.get(position).toString());
            
            return view;
    }
    
    
	/**View holders to improve performance of adapter! **/
	private static class ViewHolder { // Used to hold views per row in the
											// List
		//final ImageView imageView;
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
