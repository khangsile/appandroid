package com.llc.bumpr.popups;

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

public class MinPeoplePopUp extends PopupWindow {

	private Context context;
	private OnSubmitListener listener;
	private Button btnApply;
	private TextView instructions;
	private TextView tvCount;
	private int count = 2;
	
	public MinPeoplePopUp(Context context, ViewGroup root, OnSubmitListener listener) {
		super(context);
		
		this.context = context;
		this.listener = listener;
		
		setContentView(LayoutInflater.from(context).inflate(R.layout.min_people_popup, root));
		View v = getContentView();

		Resources r = context.getResources();
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250, r.getDisplayMetrics());
		
		setHeight((int)px);
		setWidth(WindowManager.LayoutParams.MATCH_PARENT);
		setFocusable(true);
		setBackgroundDrawable(new ColorDrawable());
		
		tvCount = (TextView) v.findViewById(R.id.tv_people_count);
		instructions = (TextView) v.findViewById(R.id.tv_instructions);
		
		/** Set up buttons */
		Button up = (Button) v.findViewById(R.id.btn_up);
		up.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				upPressed(v);
			}
			
		});
		
		Button down = (Button) v.findViewById(R.id.btn_down);
		down.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				downPressed(v);
			}
			
		});
		
		btnApply = (Button) v.findViewById(R.id.btn_apply);
		btnApply.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				submit(v);
			}
			
		});
		
	}
	
	/************************* SETTERS ***************************/
	
	public void setInstructions(String instruction) {
		instructions.setText(instruction);
	}
	
	public void setBtnApplyText(String action) {
		btnApply.setText(action);
	}
	
	/************************* BUTTON METHODS ********************/
	
	private void submit(View v) {
		listener.valueChanged(count);
		dismiss();
	}
	
	private void upPressed(View v) {
		setCount(++count);
	}
	
	private void downPressed(View v) {
		if (count <= 2)
		setCount(count--);
	}
	
	public void setCount(int count) {
		if (count > 0) {
			this.count = count;
			if (count > 1)
				tvCount.setText(count + " Passengers");
			else 
				tvCount.setText(count + " Passenger");
		}
	}
	
	public int getCount() {
		return count;
	}
	
	/************************* ON SUBMIT INTERFACE ********************/
	
	public interface OnSubmitListener {
		public void valueChanged(int value);
	}

}
