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

    /** Область, на которой будем рисовать */
    public SurfaceHolder mSurfaceHolder;
    
    /** Состояние потока (выполняется или нет. Нужно, чтобы было удобнее прибивать поток, когда потребуется) */
    private boolean mRunning;
    
    /** Стили рисования */
    private Paint mPaint;
    private Paint mScorePaint;
    private Paint mPausePaint;
    
    /** Прямоугольник игрового поля */
    public Rect mField;
    
    /** Мячик */
    private Ball mBall;    
    
    /** Ракетка, управляемая игроком */
    public Racquet mUs;
    /** массив кирпичей */
    public Brick[] mBricks = new Brick[25]; 

    /** Фон */
    private Bitmap mBackground;
    
    /** Были ли инициализированы координаты игровых объектов */
    private boolean mInitialized;

    /** Стоит ли приложение на паузе */
    public boolean mPaused;

    /** Хелпер для перерисовки экрана */
    private DrawHelper mDrawScreen;
    
    /** Хелпер для рисования паузы*/
    public DrawHelper mDrawPause;
    
    /** Хелпер для рисования результата игры*/
    private DrawHelper mDrawGameover;
    
    private int mMaxLose = 3; // кол-во промахов
    
    /**
     * Конструктор
     * @param surfaceHolder Область рисования
     * @param context Контекст приложения
     */
    public GameManager(SurfaceHolder surfaceHolder, Context context)
    {
        mSurfaceHolder = surfaceHolder;
        Resources res = context.getResources();
        mRunning = false;
        mInitialized = false;

        // стили для рисования игрового поля
        mPaint = new Paint();
        mPaint.setColor(Color.BLUE);
        mPaint.setStrokeWidth(2);
        mPaint.setStyle(Style.STROKE);
        
        // стили для вывода счета
        mScorePaint = new Paint();
        mScorePaint.setTextSize(20);
        mScorePaint.setStrokeWidth(1);
        mScorePaint.setStyle(Style.FILL);
        mScorePaint.setTextAlign(Paint.Align.CENTER);
        mScorePaint.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Mini.ttf"));

        // стили для рисования паузы
        mPausePaint = new Paint();
        mPausePaint.setStyle(Style.FILL);
        mPausePaint.setColor(Color.argb(100, 50, 50, 80));

        // игровые объекты
        mField = new Rect();
        mBall = new Ball(res.getDrawable(R.drawable.ball));
        mUs = new Racquet(res.getDrawable(R.drawable.us));
        CreateBricks(res);

        // функция для рисования экрана
        mDrawScreen = new DrawHelper()
        {
            public void draw(Canvas canvas)
            {
                refreshCanvas(canvas);
            }
        };
    
        // функция для рисования паузы
        mDrawPause = new DrawHelper()
        {
            public void draw(Canvas canvas)
            {
                canvas.drawRect(mField, mPausePaint);                
            }
        };
        
        // функция для рисования результатов игры
        mDrawGameover = new DrawHelper()
        {
            public void draw(Canvas canvas)
            {
                // Вывели последнее состояние игры
                refreshCanvas(canvas);
                
                // выводим соответствующее сообщение
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
     * Рисование на экране
     * @param helper Класс для рисовальной функции 
     */
    private void draw(DrawHelper helper)
    {
        Canvas canvas = null;
        try
        {
            // подготовка Canvas-а
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
     * Задание состояния потока
     * @param running
     */
    public void setRunning(boolean running)
    {
        mRunning = running;
    }
    
    @Override
    /** Действия, выполняемые в потоке */
    public void run()
    {
        while (mRunning)
        {
            if (mPaused) continue;
            
            if (mInitialized)
            {
                updateObjects(); // обновляем объекты
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
    
    /** Обновление объектов на экране */
    public void refreshCanvas(Canvas canvas)
    {
        // вывод фонового изображения
        canvas.drawBitmap(mBackground, 0, 0, null);
        
        // рисуем игровое поле
        canvas.drawRect(mField, mPaint);
        
        // рисуем игровые объекты
        mBall.draw(canvas);
        mUs.draw(canvas);
        DrawBricks(canvas);
        

        // вывод счета
        mScorePaint.setColor(Color.RED);
        canvas.drawText(String.valueOf("Промахи: " + mUs.getLose()), mField.centerX(), mField.top - 10, mScorePaint);
        mScorePaint.setColor(Color.GREEN);
        canvas.drawText(String.valueOf(mUs.getScore() + " из " + mBricks.length), mField.centerX(), mField.bottom + 25, mScorePaint);

        // состояние игры
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
    
    /** Обновление состояния игровых объектов */
    private void updateObjects()
    {
        mBall.update();
        mUs.update();
        UpdateBricks();
        
        // чтобы ракетка не выходила за пределы поля
        placeInBounds(mUs);

        // проверка столкновения мячика со стенами
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


        // проверка столкновений мячика с ракетками
        if (GameObject.intersects(mBall, mUs))
        {
            mBall.setBottom(mUs.getBottom() - Math.abs(mUs.getBottom() - mBall.getBottom()));
            mBall.reflectHorizontal();
        }

        // проверка столкновений мячика с кирпичем        
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
        
        // проверка пропуска
        if (mBall.getTop() >= mField.bottom)
        {
            mUs.incLose();
            reset();
        }

        // проверка окончания игры
        if (mUs.getLose() >= mMaxLose || mUs.getScore() >= mBricks.length)
        {
            this.mRunning = false;
        }
         
    }
    
    /**
     * Инициализация положения объектов, в соответствии с размерами экрана
     * @param screenHeight Высота экрана
     * @param screenWidth Ширина экрана
     */
    public void initPositions(int screenHeight, int screenWidth)
    {
        mInitialized = false;

        int left = (screenWidth - FIELD_WIDTH) / 2;
        int top = (screenHeight - FIELD_HEIGHT) / 2;
        
        mField.set(left, top, left + FIELD_WIDTH, top + FIELD_HEIGHT);
        
        mBackground = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.RGB_565);
        
        // мячик ставится в центр поля
        mBall.setCenterX(mField.centerX());
        mBall.setCenterY(mField.centerY());
    
        // ракетка игрока - снизу по центру
        mUs.setCenterX(mField.centerX());
        mUs.setBottom(mField.bottom);
        // кирпичи
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
     * Если ракетка вышла за пределы игрового поля, вернуть ее обратно в пределы
     * @param r Ракетка
     */
    private void placeInBounds(Racquet r)
    {
        if (r.getLeft() < mField.left)
            r.setLeft(mField.left);
        else if (r.getRight() > mField.right)
            r.setRight(mField.right);
    }
    
    /**
     * Возвращает ракетки и мячик на исходные позиции, делает паузу LOSE_PAUSE
     */
    private void reset()
    {
        // ставим мячик в центр
        mBall.setCenterX(mField.centerX());
        mBall.setCenterY(mField.centerY());
        // задаем ему новый случайный угол
        mBall.resetAngle();
        
        // ставим ракетки в центр
        mUs.setCenterX(mField.centerX());
        
        // делаем паузу
        try
        {
            sleep(LOSE_PAUSE);
        }
        catch (InterruptedException iex)
        {
        }
    }
}
