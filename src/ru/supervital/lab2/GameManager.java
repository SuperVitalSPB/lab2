package ru.supervital.lab2;

import ru.supervital.lab2.R;
import ru.supervital.lab2.objects.*;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Paint.Style;
import android.view.KeyEvent;
import android.view.SurfaceHolder;

public class GameManager extends Thread
{
    public interface DrawHelper
    {
        void draw(Canvas canvas);
    }

    private static final int LOSE_PAUSE = 2000;
    private static final int FIELD_WIDTH = 400;
    private static final int FIELD_HEIGHT = 450;

    /** �������, �� ������� ����� �������� */
    public SurfaceHolder mSurfaceHolder;
    
    /** ��������� ������ (����������� ��� ���. �����, ����� ���� ������� ��������� �����, ����� �����������) */
    private boolean mRunning;
    
    /** ����� ��������� */
    private Paint mPaint;
    private Paint mScorePaint;
    private Paint mPausePaint;
    
    /** ������������� �������� ���� */
    public Rect mField;
    
    /** ����� */
    private Ball mBall;    
    
    /** �������, ����������� ������� */
    public Racquet mUs;
    /** ������ �������� */
    public Brick[] mBricks = new Brick[25]; 

    /** ��� */
    private Bitmap mBackground;
    
    /** ���� �� ���������������� ���������� ������� �������� */
    private boolean mInitialized;

    /** ����� �� ���������� �� ����� */
    public boolean mPaused;

    /** ������ ��� ����������� ������ */
    private DrawHelper mDrawScreen;
    
    /** ������ ��� ��������� �����*/
    public DrawHelper mDrawPause;
    
    /** ������ ��� ��������� ���������� ����*/
    private DrawHelper mDrawGameover;
    
    private int mMaxLose = 3; // ���-�� ��������
    
    /**
     * �����������
     * @param surfaceHolder ������� ���������
     * @param context �������� ����������
     */
    public GameManager(SurfaceHolder surfaceHolder, Context context)
    {
        mSurfaceHolder = surfaceHolder;
        Resources res = context.getResources();
        mRunning = false;
        mInitialized = false;

        // ����� ��� ��������� �������� ����
        mPaint = new Paint();
        mPaint.setColor(Color.BLUE);
        mPaint.setStrokeWidth(2);
        mPaint.setStyle(Style.STROKE);
        
        // ����� ��� ������ �����
        mScorePaint = new Paint();
        mScorePaint.setTextSize(20);
        mScorePaint.setStrokeWidth(1);
        mScorePaint.setStyle(Style.FILL);
        mScorePaint.setTextAlign(Paint.Align.CENTER);
        mScorePaint.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Mini.ttf"));

        // ����� ��� ��������� �����
        mPausePaint = new Paint();
        mPausePaint.setStyle(Style.FILL);
        mPausePaint.setColor(Color.argb(100, 50, 50, 80));

        // ������� �������
        mField = new Rect();
        mBall = new Ball(res.getDrawable(R.drawable.ball));
        mUs = new Racquet(res.getDrawable(R.drawable.us));
        CreateBricks(res);

        // ������� ��� ��������� ������
        mDrawScreen = new DrawHelper()
        {
            public void draw(Canvas canvas)
            {
                refreshCanvas(canvas);
            }
        };
    
        // ������� ��� ��������� �����
        mDrawPause = new DrawHelper()
        {
            public void draw(Canvas canvas)
            {
                canvas.drawRect(mField, mPausePaint);                
            }
        };
        
        // ������� ��� ��������� ����������� ����
        mDrawGameover = new DrawHelper()
        {
            public void draw(Canvas canvas)
            {
                // ������ ��������� ��������� ����
                refreshCanvas(canvas);
                
                // ������� ��������������� ���������
                String message = "";

                if (mUs.getScore() >= mBricks.length)
                {
                    mScorePaint.setColor(Color.GREEN);
                    message = "You won";
                }
                else if (mUs.getLose() >= 3)
                {
                    mScorePaint.setColor(Color.RED);
                    message = "You lost";
                }
                mScorePaint.setTextSize(30);
                canvas.drawText(message, mField.centerX(), mField.centerY(), mScorePaint);
                
            }
        };
	}
    
    private void CreateBricks(Resources res){
    	for (int i=0; i<=24; i++){
    		mBricks[i] = new Brick(res.getDrawable(R.drawable.brick));
    	}
    }
    
    
    /** 
     * ��������� �� ������
     * @param helper ����� ��� ����������� ������� 
     */
    private void draw(DrawHelper helper)
    {
        Canvas canvas = null;
        try
        {
            // ���������� Canvas-�
            canvas = mSurfaceHolder.lockCanvas(); 
            synchronized (mSurfaceHolder)
            {
                if (mInitialized)
                {
                    helper.draw(canvas);
                }
            }
        }
        catch (Exception e) { }
        finally
        {
            if (canvas != null)
            {
                mSurfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    /**
     * ������� ��������� ������
     * @param running
     */
    public void setRunning(boolean running)
    {
        mRunning = running;
    }
    
    @Override
    /** ��������, ����������� � ������ */
    public void run()
    {
        while (mRunning)
        {
            if (mPaused) continue;
            
            if (mInitialized)
            {
                updateObjects(); // ��������� �������
                draw(mDrawScreen);
                try
                {
                    sleep(20);
                }
                catch (InterruptedException e) {};
            }
        }
        draw(mDrawGameover);
    }
    
    /** ���������� �������� �� ������ */
    public void refreshCanvas(Canvas canvas)
    {
        // ����� �������� �����������
        canvas.drawBitmap(mBackground, 0, 0, null);
        
        // ������ ������� ����
        canvas.drawRect(mField, mPaint);
        
        // ������ ������� �������
        mBall.draw(canvas);
        mUs.draw(canvas);
        DrawBricks(canvas);
        

        // ����� �����
        mScorePaint.setColor(Color.RED);
        canvas.drawText(String.valueOf("�������: " + mUs.getLose()), mField.centerX(), mField.top - 10, mScorePaint);
        mScorePaint.setColor(Color.GREEN);
        canvas.drawText(String.valueOf(mUs.getScore() + " �� " + mBricks.length), mField.centerX(), mField.bottom + 25, mScorePaint);

        // ��������� ����
        mScorePaint.setColor(Color.RED);
        if (mPaused) canvas.drawText("Paused", mField.centerX(), mField.centerY(), mScorePaint);
        	else canvas.drawText("", mField.centerX(), mField.centerY(), mScorePaint);

    }

    private void DrawBricks(Canvas canvas)
    {
		for (int i=0; i<=mBricks.length-1 ; i++)
			if (mBricks[i].isVisible)
					mBricks[i].draw(canvas);
    }
    

    private void UpdateBricks()
    {
    	for (int i=0; i<=mBricks.length-1; i++){
    		if (mBricks[i].isVisible) 
    				mBricks[i].update();	
    	}
    }
    
    /** ���������� ��������� ������� �������� */
    private void updateObjects()
    {
        mBall.update();
        mUs.update();
        UpdateBricks();
        
        // ����� ������� �� �������� �� ������� ����
        placeInBounds(mUs);

        // �������� ������������ ������ �� �������
        if (mBall.getLeft() <= mField.left)
        {
            mBall.setLeft(mField.left + Math.abs(mField.left - mBall.getLeft()));
            mBall.reflectVertical();
        }
        else if (mBall.getRight() >= mField.right)
        {
            mBall.setRight(mField.right - Math.abs(mField.right - mBall.getRight()));
            mBall.reflectVertical();
        } else if (mBall.getTop() <= mField.top)
        {
            mBall.setTop(mField.top - Math.abs(mField.top - mBall.getTop()));
            mBall.reflectHorizontal();
        }


        // �������� ������������ ������ � ���������
        if (GameObject.intersects(mBall, mUs))
        {
            mBall.setBottom(mUs.getBottom() - Math.abs(mUs.getBottom() - mBall.getBottom()));
            mBall.reflectHorizontal();
        }

        // �������� ������������ ������ � ��������        
    	for (int i=0; i<=mBricks.length-1; i++)
    		if (mBricks[i].isVisible) {
    	        if (GameObject.intersects(mBall, mBricks[i]))
    	        {
    	        	mBricks[i].isVisible = false;
    	        	mUs.incScore();
    	            mBall.setBottom(mUs.getBottom() - Math.abs(mUs.getBottom() - mBall.getBottom()));
    	            mBall.reflectHorizontal();
    	        }
    	}
        
        // �������� ��������
        if (mBall.getTop() >= mField.bottom)
        {
            mUs.incLose();
            reset();
        }

        // �������� ��������� ����
        if (mUs.getLose() >= mMaxLose || mUs.getScore() >= mBricks.length)
        {
            this.mRunning = false;
        }
         
    }
    
    /**
     * ������������� ��������� ��������, � ������������ � ��������� ������
     * @param screenHeight ������ ������
     * @param screenWidth ������ ������
     */
    public void initPositions(int screenHeight, int screenWidth)
    {
        mInitialized = false;

        int left = (screenWidth - FIELD_WIDTH) / 2;
        int top = (screenHeight - FIELD_HEIGHT) / 2;
        
        mField.set(left, top, left + FIELD_WIDTH, top + FIELD_HEIGHT);
        
        mBackground = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.RGB_565);
        
        // ����� �������� � ����� ����
        mBall.setCenterX(mField.centerX());
        mBall.setCenterY(mField.centerY());
    
        // ������� ������ - ����� �� ������
        mUs.setCenterX(mField.centerX());
        mUs.setBottom(mField.bottom);
        // �������
        InitBricks();
        
        mInitialized = true;
    }
    
    private void InitBricks()
    {
		int row = 0;
		int stcol = 0;
		
		mBricks[0].setTop(mField.top);    	
		mBricks[0].setLeft(mField.left);
		row = row + mBricks[0].getTop();
		int dcol = mBricks[0].getWidth(); 
				
		for (int i=1; i<=mBricks.length-1 ; i++) {
			if (i == 9 || i == 16 || i == 21 || i == 24 ) row = row + mBricks[0].getHeight();
			
    		if (i <= 8) stcol = i-1;
    		else {
    			dcol = 0;
    			if (i >= 9 && i<= 15) stcol = i - 8;
	    		else if (i >= 16 && i<= 20) stcol = i - 6;
	    		else if (i >= 21 && i<= 23) stcol = i - 4;
	    		else if (i == 24) stcol = i - 2;
    		}
			
    		mBricks[i].setTop(row);
    		mBricks[i].setLeft(mBricks[stcol].getLeft() + dcol);
		}		
    }
    
    /**
     * ���� ������� ����� �� ������� �������� ����, ������� �� ������� � �������
     * @param r �������
     */
    private void placeInBounds(Racquet r)
    {
        if (r.getLeft() < mField.left)
            r.setLeft(mField.left);
        else if (r.getRight() > mField.right)
            r.setRight(mField.right);
    }
    
    /**
     * ���������� ������� � ����� �� �������� �������, ������ ����� LOSE_PAUSE
     */
    private void reset()
    {
        // ������ ����� � �����
        mBall.setCenterX(mField.centerX());
        mBall.setCenterY(mField.centerY());
        // ������ ��� ����� ��������� ����
        mBall.resetAngle();
        
        // ������ ������� � �����
        mUs.setCenterX(mField.centerX());
        
        // ������ �����
        try
        {
            sleep(LOSE_PAUSE);
        }
        catch (InterruptedException iex)
        {
        }
    }
}
