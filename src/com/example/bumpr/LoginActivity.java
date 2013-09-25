package com.example.bumpr;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;

public class LoginActivity extends Activity {

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
						public boolean onTouch(View arg0, MotionEvent arg1) {
							// TODO Auto-generated method stub
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
    
}
