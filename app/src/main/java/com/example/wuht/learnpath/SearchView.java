package com.example.wuht.learnpath;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by wuht on 2016/9/22.
 */
public class SearchView extends View {

    private Paint
            mPaint = new Paint();
    private int mWidth, mHeight;
    private Path mCirclePath, mSearchPath, mSearchPathReverse;
    private PathMeasure mPathMeasure;
    private ValueAnimator mValueAnimator;
    private float currentAnimatorValue;
    private long defaultduration = 2000;
    private SearchState mState = SearchState.NONE;


    public SearchView(Context context) {
        this(context, null);
        init();
    }

    public SearchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }

    public SearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        initPaint(mPaint, Color.RED, true, Paint.Style.STROKE);
        mPaint.setDither(true);
        mPaint.setStrokeWidth(8);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        initPath();
        initValueAnimator();

    }

    private void initValueAnimator() {
        mValueAnimator = ValueAnimator.ofFloat(0, 1f).setDuration(defaultduration);
        mValueAnimator.addUpdateListener(updateListener);
        mValueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {//妈蛋，只监听一次啊？
                if (mState == SearchState.START) {
                    setState(SearchState.SEARCHING);
                } else if (mState == SearchState.END) {
                    setState(SearchState.NONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private ValueAnimator.AnimatorUpdateListener updateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            currentAnimatorValue = (float) animation.getAnimatedValue();
            invalidate();
        }
    };

    private void initPath() {
        mPathMeasure = new PathMeasure();
        mCirclePath = new Path();
        mSearchPath = new Path();
        mSearchPathReverse = new Path();

        RectF searchF = new RectF(-50, -50, 50, 50);
        RectF circleF = new RectF(-100, -100, 100, 100);
        //注意不要是360度，否则系统自动优化
        mCirclePath.addArc(circleF, 45, 359.99f);
        mSearchPath.addArc(searchF, 45, 359.99f);

        float[] pos1 = new float[2], pos2 = new float[2];
        mPathMeasure.setPath(mCirclePath, false);
        mPathMeasure.getPosTan(0, pos1, null);
        mPathMeasure.setPath(mSearchPath, false);
        mPathMeasure.getPosTan(0, pos2, null);
        mSearchPathReverse.moveTo(pos1[0], pos1[1]);
        mSearchPathReverse.lineTo(pos2[0], pos2[1]);
        mSearchPathReverse.addArc(searchF, 0, 359.999f);

        mPathMeasure.setPath(mCirclePath, false);
        float[] pos = new float[2];
        mPathMeasure.getPosTan(0, pos, null);
        mSearchPath.lineTo(pos[0], pos[1]);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawPath(canvas);
        //canvas.translate(mWidth / 2, mHeight / 2);
        //canvas.drawPath(mSearchPathReverse,mPaint);
    }

    private void drawPath(Canvas canvas) {

        canvas.translate(mWidth / 2, mHeight / 2);


        switch (mState) {
            case SEARCHING:
                if (currentAnimatorValue > 0.99) {
                    mPathMeasure.setPath(mCirclePath, false);
                    Path path = new Path();
                    mPathMeasure.getSegment(0, 20, path, true);//如果是点的话，可以通过这个来解决闪一下的问题，这个是个线要解决有些麻烦
                    //canvas.drawPath(path,mPaint);
                }
                mPathMeasure.setPath(mCirclePath, false);
                Path circlePath = new Path();
                mPathMeasure.getSegment(mPathMeasure.getLength() * currentAnimatorValue - 30, mPathMeasure.getLength() * currentAnimatorValue, circlePath, true);
                canvas.drawPath(circlePath, mPaint);
                if (currentAnimatorValue == 1) {
                    setState(SearchState.SEARCHING);
                }
                break;
            case NONE:
                canvas.drawPath(mSearchPath, mPaint);
                break;
            case START:
                mPathMeasure.setPath(mSearchPath, false);
                Path dismissPath = new Path();
                mPathMeasure.getSegment(mPathMeasure.getLength() * currentAnimatorValue, mPathMeasure.getLength(), dismissPath, true);
                canvas.drawPath(dismissPath, mPaint);
                break;
            case END:
                mPathMeasure.setPath(mSearchPath, false);
                Path showPath = new Path();
                mPathMeasure.getSegment(mPathMeasure.getLength() * (1 - currentAnimatorValue), mPathMeasure.getLength(), showPath, true);///需要好好理解啊，他说得是
                //填入的是开始的距离和结束的距离
                canvas.drawPath(showPath, mPaint);
                break;

        }
        //canvas.drawPath(mSearchPath, mPaint);
        //Path path = new Path();
        mPathMeasure.setPath(mSearchPath, false);
        //mPathMeasure.getSegment(mPathMeasure.getLength() * currentAnimatorValue, mPathMeasure.getLength(), path, true);//消失
        //mPathMeasure.getSegment(0, mPathMeasure.getLength() * currentAnimatorValue, path, true);//开始画
        //canvas.drawPath(path, mPaint);
    }


    private void initPaint(Paint paint, int red, boolean isAntiAlias, Paint.Style style) {
        paint.setColor(red);
        paint.setAntiAlias(isAntiAlias);
        paint.setStyle(style);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    public void setState(SearchState state) {
        mState = state;
        mValueAnimator.start();
        invalidate();
    }

    public enum SearchState {
        SEARCHING, START, END, NONE;
    }
}
