package ru.supervital.lab2.objects;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public abstract class GameObject
{
    // ��������� ��� �����������
    public static final int DIR_LEFT = -1;
    public static final int DIR_RIGHT = 1;
    public static final int DIR_NONE = 0;
    
    /** ���������� ������� ����� */
    protected Point mPoint;
    
    /** ������ ����������� */
    protected int mHeight;
    
    /** ������ ����������� */
    protected int mWidth;
    
    /** ����������� */
    private Drawable mImage;
    
    /** �������� */
    protected int mSpeed;

    /**
     * �����������
     * @param image �����������, ������� ����� ���������� ������ ������ 
     */
    public GameObject(Drawable image)
    {
        mImage = image;
        mPoint = new Point(0, 0);
        mWidth = image.getIntrinsicWidth();
        mHeight = image.getIntrinsicHeight();
    }

    /** ����������� ������� ����� */
    protected abstract void updatePoint();

    /** ����������� ������� */
    public void update()
    {
        updatePoint();
        mImage.setBounds(mPoint.x, mPoint.y, mPoint.x + mWidth, mPoint.y + mHeight);
    }
    
    /** ��������� ������� */
    public void draw(Canvas canvas)
    {
        mImage.draw(canvas);
    }
    
    /** ������ ����� ������� ������� */
    public void setLeft(int value)
    {
        mPoint.x = value;
    }

    /** ������ ������ ������� ������� */
    public void setRight(int value)
    {
        mPoint.x = value - mWidth;
    }
    
    /** ������ ������� ������� ������� */
    public void setTop(int value)
    {
        mPoint.y = value;
    }

    /** ������ ������ ������� ������� */
    public void setBottom(int value)
    {
        mPoint.y = value - mHeight;
    }
    
    /** ������ �������� ������ ������� */
    public void setCenterX(int value)
    {
        mPoint.x = value - mWidth / 2;
    }
    
    /** ������ ����� �������� ������ ������� */
    public void setCenterY(int value)
    {
        mPoint.y = value - mHeight / 2;
    }
    
    /** ������� ������� ������� */
    public int getTop() { return mPoint.y; }
    
    /** ������ ������� ������� */
    public int getBottom() { return mPoint.y + mHeight; }
    
    /** ����� ������� ������� */
    public int getLeft() { return mPoint.x; }
    
    /** ������ ������� ������� */
    public int getRight() { return mPoint.x + mWidth; }
    
    /** ����������� ����� ������� */
    public Point getCenter() { return new Point(mPoint.x + mWidth / 2, mPoint.y + mHeight / 2); }

    /** ������ ������� */
    public int getHeight() { return mHeight; }
    
    /** ������ ������� */
    public int getWidth() { return mWidth; }

    /** @return �������������, �������������� ������ */
    public Rect getRect() { return mImage.getBounds(); }
    
    /** ���������, ������������ �� ��� ������� ������� */
    public static boolean intersects(GameObject obj1, GameObject obj2)
    {
        return Rect.intersects(obj1.getRect(), obj2.getRect());
    }
}