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

    /** Количество заработанных очков */
    private int mScore;
    private int mLose;
    public float mStopPoint;
    
    
    /** Увеличить количество очков игрока */
    public void incScore()
    {
        mScore++;
    }
    
    public int getScore()
    {
        return mScore;
    }

    /** Увеличить количество промахов */
    public void incLose()
    {
        mLose++;
    }
    
    public int getLose()
    {
        return mLose;
    }
    
    /** Направление движения */
    private int mDirection;
    
    /** Задание направления движения*/
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
        
        mDirection = DIR_NONE; // Направление по умолчанию - нет
        mScore = 0; // Очков пока не заработали
        mSpeed = DEFAULT_SPEED; // Задали скорость по умолчанию
    }

    /**
     * @see com.android.pingpong.objects.GameObject#updatePoint()
     */
    @Override
    protected void updatePoint()
    {
 
    	if ((int)mStopPoint==0 || !(Math.round(mStopPoint) >= getLeft() && Math.round(mStopPoint) <= getRight()))  
        	mPoint.x += mDirection * mSpeed; // двигаем ракетку по оси Ox в соответствующую сторону
    }
}
