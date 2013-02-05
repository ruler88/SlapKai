package com.game.slapkai;

import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class BloodSpill {
	private float x;
    private float y;
    private Bitmap bmp;
    private int life = 15;
    
    public BloodSpill(GameView gameView, float x,
            float y, Bitmap bmp) {
    	this.x = Math.min(Math.max(x - bmp.getWidth() / 2, 0),
                gameView.getWidth() - bmp.getWidth());
    	this.y = Math.min(Math.max(y - bmp.getHeight() / 2, 0),
	                gameView.getHeight() - bmp.getHeight());
    	this.bmp = bmp;	
    }
    
    public BloodSpill(GameView gameView, float x,
            float y, Bitmap bmp, boolean dummy) {
    	this.x = Math.min(x,
                gameView.getWidth() - bmp.getWidth());
    	this.y = Math.min(y,
	                gameView.getHeight() - bmp.getHeight());
    	this.bmp = bmp;	
    }
    
    public void onDraw(Canvas canvas, List<BloodSpill> allBloods) {
		if (--life < 1) {
            allBloods.remove(this);
    	}
        canvas.drawBitmap(bmp, x, y, null);
    }
    
		
}
