package com.game.slapkai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.quantcast.measurement.service.QuantcastClient;


public class GameView extends SurfaceView {
	private GameLoopThread gameLoopThread;
    private long lastClick;
    private Bitmap bmpBlood;
    private Bitmap angel, devil;
    private List<Character> allChars = Collections.synchronizedList(new ArrayList<Character>());
    private List<BloodSpill> allTemps = Collections.synchronizedList(new ArrayList<BloodSpill>());
    public int kaiCount = 0;
    public int score = 0;
    
    private int bmpArray[][] = {
    		{R.drawable.kai,R.drawable.kai,R.drawable.kai,R.drawable.kai},
    		{R.drawable.garrett_left,R.drawable.garrett_right,R.drawable.garrett_up,R.drawable.garrett_down},
    		{R.drawable.christina_left,R.drawable.christina_right,R.drawable.christina_up,R.drawable.christina_down},
    		{R.drawable.yena_left,R.drawable.yena_right,R.drawable.yena_up,R.drawable.yena_down},
    		{R.drawable.ek_left,R.drawable.ek_right,R.drawable.ek_up,R.drawable.ek_down},
    		{R.drawable.stephen_left,R.drawable.stephen_right,R.drawable.stephen_up,R.drawable.stephen_down}
    };
    
    private String names[] = {
    		"kai", "garrett", "christina", "yena", "ek", "stephen"
    };
    

    public GameView(Context context) {
          super(context);
          gameLoopThread = new GameLoopThread(this);
          getHolder().addCallback(new SurfaceHolder.Callback() {

                 @Override
                 public void surfaceDestroyed(SurfaceHolder holder) {
                        boolean retry = true;
                        gameLoopThread.setRunning(false);
                        gameLoopThread.destroy();
                        while (retry) {
                               try {
                                     gameLoopThread.join();
                                     retry = false;
                               } catch (InterruptedException e) {}
                        }
                 }

                 @Override
                 public void surfaceCreated(SurfaceHolder holder) {
                        createSprites();
                        gameLoopThread.setRunning(true);
                        gameLoopThread.start();
                 }

                 @Override
                 public void surfaceChanged(SurfaceHolder holder, int format,
                               int width, int height) {
                 }
          });
          bmpBlood = BitmapFactory.decodeResource(getResources(), R.drawable.blood1);
          angel = BitmapFactory.decodeResource(getResources(), R.drawable.angel);
          devil = BitmapFactory.decodeResource(getResources(), R.drawable.devil);
    }
    
    private void createSprites() {
    	if(kaiCount<=2) {
    		allChars.add(createCharacter(bmpArray[0], true, 0));
    		kaiCount++;
    	}
    	
    	if(allChars.size()>=18) {
    		return;
    	}
    	
    	int numNew = (int) (Math.random()*3);
    	int rand = (int) (Math.random()*6);
    	
    	for(int n=0; n<=numNew; n++) {
    		allChars.add(createCharacter(bmpArray[rand], rand==0, rand));
        	if(rand == 0) {
        		kaiCount++;
        	}
    	}
    	
    }
    
    
    private Character createCharacter(int bmpArray[], boolean isKai, int charIndex) {
          Bitmap bmp_l = BitmapFactory.decodeResource(getResources(), bmpArray[0]);
          Bitmap bmp_r = BitmapFactory.decodeResource(getResources(), bmpArray[1]);
          Bitmap bmp_u = BitmapFactory.decodeResource(getResources(), bmpArray[2]);
          Bitmap bmp_d = BitmapFactory.decodeResource(getResources(), bmpArray[3]);
          Bitmap bmp_all[] = {bmp_l, bmp_r, bmp_u, bmp_d};
          String name = names[charIndex];
          return new Character(this, bmp_all, isKai, name);
    }

    @Override
    protected void onDraw(Canvas canvas) {
    	  Paint paint = new Paint();
    	  paint.setColor(Color.WHITE);
    	  paint.setTextSize(25);
          paint.setStyle(Style.FILL);
    	  canvas.drawColor(Color.BLACK);
          
          String scoreText = "Score: " + score ;
          canvas.drawText(scoreText, 10, 25, paint);
    	  for (int j=allChars.size()-1; j>=0; j--) {
        	  allChars.get(j).onDraw(canvas, allChars, allTemps, angel, devil, this);
          }
    	  for(int i=allTemps.size()-1; i>=0; i--) {
    		  allTemps.get(i).onDraw(canvas, allTemps);
    	  }
          if( (int) (Math.random()*45) == 1) {
        	  createSprites();
          }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
          if (System.currentTimeMillis() - lastClick > 300) {
        	  	QuantcastClient.logEvent("slapped something");
                 lastClick = System.currentTimeMillis();
                 float x = event.getX();
                 float y = event.getY();
                 boolean targetHit = false;
                 synchronized (getHolder()) {
                        for (Character character : allChars) {
                        	if(character.isCollision(x, y)) {
                        		QuantcastClient.logEvent("slapped " + character.getName());
                            	QuantcastClient.logEvent("slapped character");
                        		targetHit = true;
                        		allChars.remove(character);
                    			allTemps.add(new BloodSpill(this, x, y, bmpBlood));
                    			if(character.isKai()) {
                    				score+=1;
                    				kaiCount--;
                    			} else {
                    				score-=5;
                    			}
                        		break;
                        	}
                        }
                        if(!targetHit) {
                        	QuantcastClient.logEvent("slapped miss");
                        }
                 }
          }
          return true;
    }

}
