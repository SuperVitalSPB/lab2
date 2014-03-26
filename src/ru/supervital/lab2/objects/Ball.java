package ru.supervital.lab2.objects;

import java.util.Random;

import android.graphics.drawable.Drawable;

public class Ball extends GameObject
{
    private static int DEFAULT_SPEED = 10;
    private static final int PI = 180;

    /** ����, ������� ���������� ����������� ������ ������ � ���� Ox */
    private int mAngle;

    /** 
     * @see com.android.pingpong.objects.GameObject#GameObject(Drawable) 
     */
    public Ball(Drawable image)
    {
        super(image);
        
        mSpeed = DEFAULT_SPEED; // ������ �������� �� ���������
        resetAngle();
        //mAngle = getRandomAngle(); // ������ ��������� ��������� ����
    }

    /** ������ ����� ��������� �������� ���� */
    public void resetAngle()
    {
        mAngle = getRandomAngle();
    }
    
    /** 
     * @see com.android.pingpong.objects.GameObject#updatePoint()
     */
    @Override
    protected void updatePoint()
    {

        double angle = Math.toRadians(mAngle);
        
        mPoint.x += (int)Math.round(mSpeed * Math.cos(angle));
        mPoint.y -= (int)Math.round(mSpeed * Math.sin(angle));
    }
    
    /** ��������� ���������� ���� � ���������� [95, 110]U[275,290] */
    private int getRandomAngle()
    {
        Random rnd = new Random(System.currentTimeMillis());
        
        return rnd.nextInt(1) * PI + PI / 2 + rnd.nextInt(45) + 20;
    }
        
    /** ��������� ������ �� ��������� */
    public void reflectVertical()
    {
        if (mAngle > 0 && mAngle < PI)
            mAngle = PI - mAngle;
        else
            mAngle = 3 * PI - mAngle;
    }
    
    /** ��������� ������ �� ����������� */
    public void reflectHorizontal()
    {
        mAngle = 2 * PI - mAngle;
    }
}
