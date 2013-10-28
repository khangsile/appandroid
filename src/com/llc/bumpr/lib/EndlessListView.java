package com.llc.bumpr.lib;

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
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.llc.bumpr.adapters.EndlessAdapter;

public class EndlessListView extends ListView implements OnScrollListener {
        
        private View footer;
        private boolean isLoading;
        private EndlessListener listener;
        private EndlessAdapter adapter;
        
        public EndlessListView(Context context, AttributeSet attrs, int defStyle) {
                super(context, attrs, defStyle);        
                this.setOnScrollListener(this);
        }

        public EndlessListView(Context context, AttributeSet attrs) {
                super(context, attrs);
                this.setOnScrollListener(this);
        }

        public EndlessListView(Context context) {
                super(context);                
                this.setOnScrollListener(this);
        }
        
        public void setListener(EndlessListener listener) {
                this.listener = listener;
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                        int visibleItemCount, int totalItemCount) {
                                
                if (getAdapter() == null)
                        return ;
                
                if (getAdapter().getCount() == 0)
                        return ;
                
                int l = visibleItemCount + firstVisibleItem;
                if (l >= totalItemCount && !isLoading) {
                        // It is time to add new data. We call the listener
                        this.addFooterView(footer);
                        isLoading = true;
                        listener.loadData();
                }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {}
        
        public void setLoadingView(int resId) {
                LayoutInflater inflater = (LayoutInflater) super.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                footer = (View) inflater.inflate(resId, null);
                this.addFooterView(footer);
                
        }

        
        public void setAdapter(EndlessAdapter adapter) {                
                super.setAdapter(adapter);
                this.adapter = adapter;
                this.removeFooterView(footer);
        }

        
        public void addNewData(List<Object> data) {
                this.removeFooterView(footer);
                
                adapter.addAll(data);
                adapter.notifyDataSetChanged();
                isLoading = false;
        }

        /**
    	 * A method that clears the data in the endless list and adds in the new data. This is needed
    	 * when the user enters a new destination and a new set of drivers should populate the list.
    	 * @param data List of new information to populate the endless list.
    	 */
        public void resetData(List<Object> data){
        	adapter.clear();
        	adapter.addAll(data);
        	adapter.notifyDataSetChanged();
        	isLoading = false;
        }
        
        public EndlessListener setListener() {
                return listener;
        }


        public static interface EndlessListener {
                public void loadData() ;
        }
        
}
