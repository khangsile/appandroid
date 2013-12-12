package com.llc.bumpr.lib;

import android.graphics.Color;

public class Colors {

	public static int[] colors = { Color.argb(255, 255, 102, 102),
		Color.argb(255, 255, 178, 102),  Color.argb(255, 204, 0, 102), 
		Color.argb(255, 102, 255, 102),	Color.argb(255, 229, 204, 255),
		Color.argb(255, 0, 255, 255), Color.argb(255, 255, 0, 255),
		Color.argb(255, 255, 204, 229), Color.argb(255, 204, 229, 255),
		Color.argb(255, 0, 128, 255), Color.argb(255, 153, 0, 153)
	};
	
	public static int getColor(int pos) {
		if (pos > colors.length || pos < 0) return 0;
		return colors[pos];
	}
	
}
