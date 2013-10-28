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
import java.util.Random;

import com.androidtools.Conversions;
import com.llc.bumpr.R;
import com.llc.bumpr.lib.GraphicsUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class EndlessAdapter extends ArrayAdapter<Object> {
        
        private List<Object> itemList;
        private Context ctx;
        private int layoutId;
        private float imageSize;
        private GraphicsUtil imageHelper;
        
        public EndlessAdapter(Context ctx, List<Object> itemList, int layoutId) {
                super(ctx, layoutId, itemList);
                this.itemList = itemList;
                this.ctx = ctx;
                this.layoutId = layoutId;

                //Set up circular image preferences here
                setCircleImagePrefs();
        }

		@Override
        public int getCount() {                
                return itemList.size() ;
        }

        @Override
        public String getItem(int position) {                
                return itemList.get(position).toString();
        }

        @Override
        public long getItemId(int position) {                
                return itemList.get(position).hashCode();
        }
        
        private void setCircleImagePrefs(){
        	//Change size of image here
			imageSize = Conversions.dpToPixels(ctx, 50);
			imageHelper = new GraphicsUtil();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder holder;
                View view;
                
                if (convertView == null) {
                        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        ViewGroup vGroup = (ViewGroup) inflater.inflate(layoutId, null);
                        
                        holder = new ViewHolder((ImageView)vGroup.findViewById(R.id.iv_driver_prof_pic), (TextView)vGroup.findViewById(R.id.tv_driver_name), 
                        				(TextView)vGroup.findViewById(R.id.tv_driver_rate), (RatingBar)vGroup.findViewById(R.id.rb_user_rating));
                        
                        vGroup.setTag(holder);
                        view = vGroup;
                }else{
                	holder = (ViewHolder)convertView.getTag();
                	view = convertView;
                }
                
                // We should use class holder pattern
                holder.drvName.setText(itemList.get(position).toString());
                holder.drvRate.setText("$2.55/hr");
                holder.drvRtBar.setRating(3.2f);
                
                //Use AsyncTask to create circular images!
                new CircleImageAsyncTask().execute(holder);
                
    			/*//Original way of loading circular images
    			Bitmap bm = imageHelper.getCircleBitmap(BitmapFactory.decodeResource(ctx.getResources(), R.drawable.test_image), 16);
    			//Resize image to the desired size
    			Bitmap resizedBM = Bitmap.createScaledBitmap(bm, Math.round(imageSize), Math.round(imageSize), false);
    			holder.imageView.setImageBitmap(resizedBM);*/
                
                return view;

        }
        
        /** Async task to improve performance of creating circular images! **/
        private class CircleImageAsyncTask extends AsyncTask<ViewHolder, Void, ViewHolder> {

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
        }
        
        
    	/**View holders to improve performance of adapter! **/
    	private static class ViewHolder { // Used to hold views per row in the
    											// List
    		final ImageView imageView;
    		final TextView drvName;
    		final TextView drvRate;
    		final RatingBar drvRtBar;
    		
    		Bitmap imageBitmap; //Needed to hold the Bitmap of the AsyncTask

    		private ViewHolder(ImageView imageView, TextView drvName, TextView drvRate, RatingBar drvRtBar) {
    			this.imageView = imageView;
    			this.drvName = drvName;
    			this.drvRate = drvRate;
    			this.drvRtBar = drvRtBar;
    		}
    	}

}