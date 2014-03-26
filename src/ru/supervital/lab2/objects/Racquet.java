/**
 * 
 */
package ru.supervital.lab2.objects;

import android.graphics.drawable.Drawable;

/**
 * @author daria
 *
 */
public class Racquet extends GameObject
{
    private static int DEFAULT_SPEED = 30;

    /** ���������� ������������ ����� */
    private int mScore;
    private int mLose;
    public float mStopPoint;
    
    
    /** ��������� ���������� ����� ������ */
    public void incScore()
    {
        mScore++;
    }
    
    public int getScore()
    {
        return mScore;
    }

    /** ��������� ���������� �������� */
    public void incLose()
    {
        mLose++;
    }
    
    public int getLose()
    {
        return mLose;
    }
    
    /** ����������� �������� */
    private int mDirection;
    
    /** ������� ����������� ��������*/
    public void setDirection(int direction)
    {
        mDirection = direction;
    }

    /** 
     * @see com.android.pingpong.objects.GameObject#GameObject(Drawable) 
     */
    public Racquet(Drawable image)
    {
        super(image);
        
        mDirection = DIR_NONE; // ����������� �� ��������� - ���
        mScore = 0; // ����� ���� �� ����������
        mSpeed = DEFAULT_SPEED; // ������ �������� �� ���������
    }

    /**
     * @see com.android.pingpong.objects.GameObject#updatePoint()
     */
    @Override
    protected void updatePoint()
    {
 
    	if ((int)mStopPoint==0 || !(Math.round(mStopPoint) >= getLeft() && Math.round(mStopPoint) <= getRight()))  
        	mPoint.x += mDirection * mSpeed; // ������� ������� �� ��� Ox � ��������������� �������
    }
}
