package com.llc.bumpr.fragments;

import java.util.List;

import com.actionbarsherlock.app.SherlockFragment;
import com.llc.bumpr.sdk.models.Trip;

public abstract class SearchTabFragment extends SherlockFragment {

	public abstract void listChanged(List<Trip> trips);
	
}
