package com.zzj.xiaomiwidgettest.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.zzj.xiaomiwidgettest.R;

/**
 * 自定义的View类,为两个球的碰撞模拟
 */
public class EyesView extends View {
    // 设置角度值，同时也就眼睛的横坐标长度
    int angle = 250;
    // 因为眨眼采用的sin()函数组成，所以其自然有幅值这一个属性。
    int amplitude = 40;
    // 创建一个用于保存幅值的变化的变量，采用amplitude的缩写ampl
    int ampl = 0;
    // 判断是否到了幅值的临界值，由线程维护
    boolean flag = true;
    // 定义中心点坐标
    int centerX = 1024 / 2;
    int centerY = 768 / 2;
    // 创建统一的颜色背景
    int color = Color.GRAY;
    // 灰眼球的半径初始值
    int blackBallSemi = 25;

    Paint mPaint = new Paint();
    Handler mhandler;
    boolean first = true;

    private int eyeDis = (int) (angle/2*1.5);//两眼距离

    int degree = 0;//眼球转动角度
    int leftX = centerX+eyeDis;//左眼初始化坐标
    int RightX = centerX-eyeDis;//右眼初始化坐标
    private Point pointLeft = new Point(),
            pointRight = new Point(),
            centerLeft = new Point(),//左右中心坐标
            centerRight = new Point(),//右眼中心坐标
            tempLeft = new Point(),//左眼球坐标
            tempRight = new Point();//有眼球坐标

    public EyesView(Context context) {
        super(context);
        init(context);
    }

    public EyesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    private void init(Context context){
        startRun();
        mhandler = new Handler(Looper.getMainLooper());
//        color = ContextCompat.getColor(context, R.color.skin);
        color = ContextCompat.getColor(context, android.R.color.holo_red_dark);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //重新初始化下面参数
        centerX = this.getMeasuredWidth()/2;
        centerY = this.getMeasuredHeight()/3;
        angle = this.getMeasuredWidth()/4;
        eyeDis = (int) (angle/2*1.5);
        amplitude = angle/6;
        blackBallSemi = amplitude / 2 - 6;
        leftX = centerX+eyeDis;
        RightX = centerX-eyeDis;

        pointLeft.x = leftX - blackBallSemi / 2 / 2;
        pointLeft.y = centerY - blackBallSemi / 2 / 2;
        centerLeft.x = leftX - blackBallSemi;
        centerLeft.y = centerY;

        pointRight.x = RightX + blackBallSemi / 2 / 2;
        pointRight.y =  centerY - blackBallSemi / 2 / 2;
        centerRight.x = RightX + blackBallSemi;
        centerRight.y = centerY;
    }

    private Runnable mRunnable = new Runnable() {
        // 界面的主线程
        @Override
        public void run() {
            EyesView.this.invalidate();
        }
    };

    //眼球转动
    private Runnable mRunnable1 = new Runnable() {
        // 界面的主线程
        @Override
        public void run() {
            degree = 0;
            new Thread() {
                @Override
                public void run() {
                    while (true) {
                        degree++;
                        if (degree == 360) {//转一圈360度
                            degree = 0;
                            break;
                        }
                        calcNewPoint(tempLeft,pointLeft,centerLeft,degree);//计算左眼转动坐标
                        calcNewPoint(tempRight,pointRight,centerRight,degree);//计算右眼转动坐标
                        mhandler.post(mRunnable);
                        try {
                            Thread.sleep(3);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                };

            }.start();
        }
    };


    private static void calcNewPoint(Point p1,Point p, Point pCenter, float angle) {
        // calc arc
        float l = (float) ((angle * Math.PI) / 180);

        //sin/cos value
        float cosv = (float) Math.cos(l);
        float sinv = (float) Math.sin(l);

        // calc new point
        float newX = (float) ((p.x - pCenter.x) * cosv - (p.y - pCenter.y) * sinv + pCenter.x);
        float newY = (float) ((p.x - pCenter.x) * sinv + (p.y - pCenter.y) * cosv + pCenter.y);
        p1.x = (int) newX;
        p1.y = (int) newY;
        //return new Point((int) newX, (int) newY);
    }

    /**
     * 用线程维护眼睛的眨眼效果，线程结构如下： 1、该线程使用while(true)维护动态效果 2、ampl用于表示当前的眨眼效果的幅值
     */
    public void startRun() {
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    if (flag) {
                        ampl++;
                        if (ampl >= amplitude) {//眨眼幅度
                            flag = false;
                        }
                        if (!first) {
                            first = true;
                        }
                    } else {
                        ampl--;
                        if (ampl <= 0) {
                            flag = true;
                        }
                        if (first) {
                            mhandler.post(mRunnable1);
                            try {
                                Thread.sleep(3000);//3秒眨一次眼
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            first = false;
                        }

                    }
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mhandler.post(mRunnable);
                }

            };

        }.start();
    }

    @Override
    public void onDraw(Canvas canvas) {
        drawLeft(canvas);//左眼
        drawRight(canvas);//右眼
    }

    private void drawLeft(Canvas canvas) {
        // 以centerX,centerY为中心，在眼睛所在的地方绘制一个白色的背景底色，
        // 长度为angle，宽为amplitude*2

        mPaint.setColor(Color.WHITE);
        for (int i = 0; i < angle; i++) {
            canvas.drawLine(leftX - angle / 2 + i, centerY, leftX - angle
                    / 2 + i, centerY
                    - (int) (Math.sin(Math.PI * i / angle) * amplitude), mPaint);
            canvas.drawLine(leftX - angle / 2 + i, centerY, leftX - angle
                    / 2 + i, centerY
                    + (int) (Math.sin(Math.PI * i / angle) * amplitude), mPaint);
        }
        // 以centerX,centerY为中心，绘制一个灰色的眼球
        // 半径为blackBallSemi*2
        mPaint.setColor(Color.DKGRAY);
        canvas.drawCircle(leftX - blackBallSemi, centerY, blackBallSemi * 2,
                mPaint);
        // 以centerX,centerY为中心，绘制一个白色的瞳孔
        // 半径为blackBallSemi/2
        mPaint.setColor(Color.WHITE);
        canvas.drawCircle(tempLeft.x, tempLeft.y, blackBallSemi / 2, mPaint);
        // 使用和窗口一样的背景色将眼睛外框颜色去掉，这里使用sin()函数来完成
        // 在这里体现的方法其实就是画直线，把不需要的地方都画成与背景色相同的颜色
        mPaint.setColor(color);
        for (int i = 0; i < angle; i++) {
            canvas.drawLine(leftX - angle / 2 + i, centerY - amplitude,
                    leftX - angle / 2 + i,
                    centerY - (int) (Math.sin(Math.PI * i / angle) * ampl),
                    mPaint);
            canvas.drawLine(leftX - angle / 2 + i, centerY + amplitude,
                    leftX - angle / 2 + i,
                    centerY + (int) (Math.sin(Math.PI * i / angle) * ampl),
                    mPaint);
        }
    }

    private void drawRight(Canvas canvas) {
        // 长度为angle，宽为amplitude*2

        mPaint.setColor(Color.WHITE);
        for (int i = 0; i < angle; i++) {
            canvas.drawLine(RightX - angle / 2 + i, centerY, RightX - angle
                    / 2 + i, centerY
                    - (int) (Math.sin(Math.PI * i / angle) * amplitude), mPaint);
            canvas.drawLine(RightX - angle / 2 + i, centerY, RightX - angle
                    / 2 + i, centerY
                    + (int) (Math.sin(Math.PI * i / angle) * amplitude), mPaint);
        }
        // 以centerX,centerY为中心，绘制一个灰色的眼球
        // 半径为blackBallSemi*2
        mPaint.setColor(Color.DKGRAY);
        canvas.drawCircle(RightX + blackBallSemi, centerY, blackBallSemi * 2,
                mPaint);
        // 以centerX,centerY为中心，绘制一个白色的瞳孔
        // 半径为blackBallSemi/2
        mPaint.setColor(Color.WHITE);
		/*canvas.drawCircle(RightX + blackBallSemi / 2 / 2, centerY
				- blackBallSemi / 2 / 2, blackBallSemi / 2, mPaint);*/
        canvas.drawCircle(tempRight.x, tempRight.y, blackBallSemi / 2, mPaint);
        // 使用和窗口一样的背景色将眼睛外框颜色去掉，这里使用sin()函数来完成
        // 在这里体现的方法其实就是画直线，把不需要的地方都画成与背景色相同的颜色
        mPaint.setColor(color);
        for (int i = 0; i < angle; i++) {
            canvas.drawLine(RightX - angle / 2 + i, centerY - amplitude,
                    RightX - angle / 2 + i,
                    centerY - (int) (Math.sin(Math.PI * i / angle) * ampl),
                    mPaint);
            canvas.drawLine(RightX - angle / 2 + i, centerY + amplitude,
                    RightX - angle / 2 + i,
                    centerY + (int) (Math.sin(Math.PI * i / angle) * ampl),
                    mPaint);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mhandler != null){
            mhandler.removeCallbacks(mRunnable);
            mhandler.removeCallbacks(mRunnable1);
        }
        super.onDetachedFromWindow();
    }
}
