package com.game.slapkai;

import java.util.List;
import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Character {
	private GameView gameView;
	//left, right, up, down bitmap
    private Bitmap bmp[];
    //0, 1, 2, 3 == l, r, u, d
    private int direction;
    private int x = 0;
    private int y = 0;
    private int xSpeed;
    private int ySpeed;
    private int width;
    private int height;
    private int MAX_SPEED = 10;
    private boolean isKai = false;
    private int life = 200;
    private String name;
    
    public Character(GameView gameView, Bitmap bmp[], boolean isKai, String name) {
        this.width = bmp[0].getWidth();
        this.height = bmp[0].getHeight();
        this.gameView = gameView;
        this.bmp = bmp;
        this.isKai = isKai;
        this.direction = 3;
        this.name = name;
        
        Random rnd = new Random();
        x = rnd.nextInt(gameView.getWidth() - width);
        y = rnd.nextInt(gameView.getHeight() - height);
        xSpeed = rnd.nextInt(MAX_SPEED * 2) - MAX_SPEED;
        ySpeed = rnd.nextInt(MAX_SPEED * 2) - MAX_SPEED;
    }
    
    private void update() {
        if (x >= gameView.getWidth() - width - xSpeed || x + xSpeed <= 0) {
               xSpeed = -xSpeed;
        }
        x = x + xSpeed;
        if (y >= gameView.getHeight() - height - ySpeed || y + ySpeed <= 0) {
               ySpeed = -ySpeed;
        }
        y = y + ySpeed;
        
        if(Math.abs(xSpeed) > Math.abs(ySpeed)) {
        	if(xSpeed >= 0) {
        		direction = 1;
        	} else {
        		direction = 0;
        	}
        } else {
        	direction = 3;
        }
    }
    
    public boolean isKai() {
    	return isKai;
    }
    
    public String getName() {
    	return this.name;
    }
  
    public void onDraw(Canvas canvas, List<Character> allChars, List<BloodSpill> allTemps, Bitmap angel, Bitmap devil, GameView gv) {
        update();
        canvas.drawBitmap(bmp[direction], x, y, null);
        if (--life < 1) {
            allChars.remove(this);
            if(!isKai) {
            	gv.score += 1;
            	allTemps.add(new BloodSpill(gv, x, y, angel, false));
            } else {
            	gv.score -= 10;
            	gv.kaiCount--;
            	allTemps.add(new BloodSpill(gv, x, y, devil, false));
            }
    	}
    }
    
    public boolean isCollision(float x2, float y2) {
		return x2 > x && x2 < x + width && y2 > y && y2 < y + height;
	}
    
}
