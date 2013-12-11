package com.llc.bumpr.popups;

import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.llc.bumpr.R;
import com.squareup.timessquare.CalendarPickerView;
import com.squareup.timessquare.CalendarPickerView.OnDateSelectedListener;

public class CalendarPopUp extends PopupWindow {

	private Context context;
	private OnSubmitListener listener;
	private TextView instructions;
	private Button btnApply;
	private CalendarPickerView calendar;
	private Date date;
	
	public CalendarPopUp(Context context, ViewGroup root, OnSubmitListener slistener) {
		super(context);
		
		this.context = context;
		this.listener = slistener;
		
		setContentView(LayoutInflater.from(context).inflate(R.layout.calendar_popup, root));		
		View v = getContentView();

		Calendar nextYear = Calendar.getInstance();
		nextYear.add(Calendar.YEAR, 1);

		calendar = (CalendarPickerView) v.findViewById(R.id.calendar_view);
		date = new Date();
		calendar.init(date, nextYear.getTime())
		    .withSelectedDate(date);
		
		calendar.setOnDateSelectedListener(new OnDateSelectedListener() {

			@Override
			public void onDateSelected(Date newDate) {
				date = newDate;
			}

			@Override
			public void onDateUnselected(Date newDate) {
			}
			
		});
		
		Resources r = context.getResources();
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 365, r.getDisplayMetrics());
		
		setHeight((int)px);
		setWidth(WindowManager.LayoutParams.MATCH_PARENT);
		setFocusable(true);
		setBackgroundDrawable(new ColorDrawable());
		
		instructions = (TextView) v.findViewById(R.id.tv_instructions);
		
		/** Set up buttons */
		btnApply = (Button) v.findViewById(R.id.btn_apply);
		btnApply.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				listener.valueChanged(date);
				dismiss();
			}
			
		});
		
	}
	
	public void setInstructions(String instruction) {
		instructions.setText(instruction);
	}
	
	public void setBtnApplyText(String action) {
		btnApply.setText(action);
	}
	
	/************************* ON SUBMIT INTERFACE ********************/
	
	public interface OnSubmitListener {
		public void valueChanged(Date date);
	}
	
}
