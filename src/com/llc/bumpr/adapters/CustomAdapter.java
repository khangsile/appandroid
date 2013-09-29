package com.llc.bumpr.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class CustomAdapter extends BaseAdapter {

	private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;
	
    private int typeCount = 1;
    
    private ArrayList data = new ArrayList();
    private LayoutInflater inflater;
    
    public CustomAdapter(Context c, int typeCount, ArrayList data) {
    	this.inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	this.data = data;
    	if (typeCount > 0) typeCount = typeCount;
    }
    
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		return null;
	}

}
