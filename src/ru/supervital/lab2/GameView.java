package ru.supervital.lab2;

import ru.supervital.lab2.objects.Brick;
import ru.supervital.lab2.objects.GameObject;

import android.R.integer;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements SurfaceHolder.Callback
{
    /**
     * ������� ���������
     */
    private SurfaceHolder mSurfaceHolder;
    
    /**
     * �����, �������� � �������
     */
    private GameManager mGameManager;
    
    /**
     * �����������
     * @param context
     * @param attrs
     */
    public GameView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        // ������������� �� ������� Surface
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        
        // �������� ��������� ������� �������� 
        mGameManager = new GameManager(mSurfaceHolder, context);
    }

    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        // ���� �� ������ ������� � ����, �� ����
        if (y < mGameManager.mField.top || y > mGameManager.mField.bottom) return false;
        
//        if (y > (mGameManager.mField.bottom - mGameManager.mUs.getHeight()*5)) {
        	// ������ ������� ����� �������
	        int moveTo;
	        if (x >= mGameManager.mUs.getLeft() && x<=mGameManager.mUs.getRight())
	        	moveTo = GameObject.DIR_NONE;
	        else if (x >= mGameManager.mUs.getLeft()) 
	        	moveTo = GameObject.DIR_RIGHT; 
	        else
	        	moveTo = GameObject.DIR_LEFT;
	        
	        mGameManager.mUs.mStopPoint = x;	        	        
	        mGameManager.mUs.setDirection(moveTo);
/*	        
        } else {
        	// ������ ������� ���� �������, �� � ���� - �����
        	Pause();
        }
*/        
        
        return super.onTouchEvent(event);    	
    };
    
    public void Pause(){
    	mGameManager.mPaused = !mGameManager.mPaused;
    }
    
    @Override
    /**
     * ��������� ������� ���������
     */
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
        mGameManager.initPositions(height, width);
    }

    @Override
    /**
     * �������� ������� ���������
     */
    public void surfaceCreated(SurfaceHolder holder)
    {
        mGameManager.setRunning(true);
        mGameManager.start();
    }

    @Override
    /**
     * ����������� ������� ���������
     */
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        boolean retry = true;
        mGameManager.setRunning(false);
        while (retry) 
        {
            try 
            {
                // �������� ���������� ������
                mGameManager.join(); 
                retry = false;
            } 
            catch (InterruptedException e) { }
        }
    }
    
}
