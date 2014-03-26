package ru.supervital.lab2.objects;

import android.graphics.drawable.Drawable;

public class Brick extends GameObject {
	public boolean isVisible;

	public Brick(Drawable image) {
		super(image);
		isVisible = true;
	}

	@Override
	protected void updatePoint() {
        mPoint.x += 0; 
	}
	
	

}
