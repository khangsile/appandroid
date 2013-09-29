package com.llc.bumpr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.llc.bumpr.R;

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

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void authenticate() {
    	//Fill in authentication process
    	//Call toSearch if successful!
    }
    
    public void toRegistration(View v) {
    	Intent i = new Intent(this, RegistrationActivity.class);
    	startActivity(i);
    }
    
    public void toSearch(View v) {
    	Intent i = new Intent(this, SearchDrivers.class);
    	startActivity(i);
    }
    
    public void loginWithFacebook(View v) {
    
    }
}
