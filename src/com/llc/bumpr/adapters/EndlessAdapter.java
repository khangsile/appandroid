package com.llc.bumpr.adapters;
/*
 * Copyright (C) 2012 Surviving with Android (http://www.survivingwithandroid.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.llc.bumpr.R;
import com.llc.bumpr.lib.CircularImageView;
import com.llc.bumpr.sdk.models.User;

public class EndlessAdapter extends ArrayAdapter<User> {
        /** Reference to list to hold data displayed in the list */
        private List<User> itemList;
        /** Context of the application */
        private Context ctx;
        /** Reference to the layout to be displayed */
        private int layoutId;

        //Not used currently
        //private float imageSize;
        //private GraphicsUtil imageHelper;
        
        public EndlessAdapter(Context ctx, List<User> itemList, int layoutId) {
                super(ctx, layoutId, itemList);
                this.itemList = itemList;
                this.ctx = ctx;
                this.layoutId = layoutId;

                //Set up circular image preferences here --Not used currently
                //setCircleImagePrefs();
        }

		@Override
        public int getCount() {                
                return itemList.size() ;
        }

        @Override
        public User getItem(int position) {                
                return (User) itemList.get(position);
        }

        @Override
        public long getItemId(int position) {                
                return itemList.get(position).hashCode();
        }
        
        //Not used currently
        /*private void setCircleImagePrefs(){
        	//Change size of image here
			imageSize = Conversions.dpToPixels(ctx, 50);
			imageHelper = new GraphicsUtil();
        }*/

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder holder;
                View view;
                
                if (convertView == null) { //If new row, inflate row
                        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        ViewGroup vGroup = (ViewGroup) inflater.inflate(layoutId, null);
                        
                        //Create new ViewHolder object
                        holder = new ViewHolder((CircularImageView)vGroup.findViewById(R.id.iv_driver_prof_pic), (TextView)vGroup.findViewById(R.id.tv_driver_name), 
                        				(TextView)vGroup.findViewById(R.id.tv_driver_rate), (RatingBar)vGroup.findViewById(R.id.rb_user_rating),
                        				(TextView) vGroup.findViewById(R.id.tv_driver_cnt), (View) vGroup.findViewById(R.id.num_divider));
                        
                        vGroup.setTag(holder); //Set tag
                        view = vGroup;
                }else{ //Get view holder from tag
                	holder = (ViewHolder)convertView.getTag();
                	view = convertView;
                }
                
                //Get user from current row
                User user = (User) itemList.get(position);
                
                //Fill row with user data
                holder.drvName.setText(user.getFirstName() + " " + user.getLastName());
                holder.drvRate.setText(user.getDriverProfile().getFee() + "");
                holder.drvRtBar.setRating((float)user.getDriverProfile().getRating());
                holder.driverCnt.setText(Integer.toString(position+1));
                holder.blackSep.getLayoutParams().height = holder.imageView.getLayoutParams().height;
                holder.imageView.setImageResource(R.drawable.test_image);
                
                //Use AsyncTask to create circular images! -- Not used for the time being
                //new CircleImageAsyncTask().execute(holder);
                
    			/*//Original way of loading circular images
    			Bitmap bm = imageHelper.getCircleBitmap(BitmapFactory.decodeResource(ctx.getResources(), R.drawable.test_image), 16);
    			//Resize image to the desired size
    			Bitmap resizedBM = Bitmap.createScaledBitmap(bm, Math.round(imageSize), Math.round(imageSize), false);
    			holder.imageView.setImageBitmap(resizedBM);*/
                
                return view;

        }
        
        //Not used right now!
        /** Async task to improve performance of creating circular images! **/
       /* private class CircleImageAsyncTask extends AsyncTask<ViewHolder, Void, ViewHolder> {

			@Override
			protected ViewHolder doInBackground(ViewHolder... arg0) {
				// TODO Auto-generated method stub
				ViewHolder viewHolder = arg0[0];
				Bitmap bm = imageHelper.getCircleBitmap(BitmapFactory.decodeResource(ctx.getResources(), R.drawable.test_image), 16);
    			//Resize image to the desired size
    			viewHolder.imageBitmap = Bitmap.createScaledBitmap(bm, Math.round(imageSize), Math.round(imageSize), false);
    			return viewHolder;
			}

			@Override
			protected void onPostExecute(ViewHolder result) {
				// TODO Auto-generated method stub
				result.imageView.setImageBitmap(result.imageBitmap);
			}
        } */
        
        
    	/**View holders to improve performance of adapter! **/
    	private static class ViewHolder { // Used to hold views per row in the
    											// List
    		//final ImageView imageView;
    		final CircularImageView imageView;
    		final TextView drvName;
    		final TextView drvRate;
    		final RatingBar drvRtBar;
    		final TextView driverCnt;
    		final View blackSep;
    		
    		//Bitmap imageBitmap; //Needed to hold the Bitmap of the AsyncTask

    		private ViewHolder(CircularImageView imageView, TextView drvName, TextView drvRate, RatingBar drvRtBar, TextView driverCnt, View blackSep) {
    			this.imageView = imageView;
    			this.drvName = drvName;
    			this.drvRate = drvRate;
    			this.drvRtBar = drvRtBar;
    			this.driverCnt = driverCnt;
    			this.blackSep = blackSep;
    		}
    	}

}