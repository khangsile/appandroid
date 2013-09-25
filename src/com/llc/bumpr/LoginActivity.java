package com.llc.bumpr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.example.bumpr.R;

public class LoginActivity extends Activity {

	//Test Git Push
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        final View activityRootView = findViewById(R.id.root);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                if (heightDiff > 100) { // if more than 100 pixels, its probably a keyboard...
                	activityRootView.setOnTouchListener(new OnTouchListener() {
						@Override
						// set OnTouchListener to entire screen to grab touch events
						public boolean onTouch(View arg0, MotionEvent arg1) {
							// close keyboard 
					        final InputMethodManager imm = (InputMethodManager)getSystemService(
					                Context.INPUT_METHOD_SERVICE);
					            imm.hideSoftInputFromWindow(activityRootView.getWindowToken(), 0);
							return false;
						}
                	});
                }
             }
        });
    }
   
    
    /* Button Methods */
    
    private void login(View v) {
    	String email = ((EditText)findViewById(R.id.et_email)).toString().trim();
    	String password = ((EditText)findViewById(R.id.et_password)).toString().trim();
    }
    
    private void toRegistration(View v) {
    	Intent i = new Intent(this, RegistrationActivity.class);
    	startActivity(i);
    }
    
    private void loginWithFacebook(View v) {
    
    }
}
