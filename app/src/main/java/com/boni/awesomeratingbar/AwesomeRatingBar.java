package com.boni.awesomeratingbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

public class AwesomeRatingBar extends View {

    private Drawable filledStar = getResources().getDrawable(R.drawable.star_filled);
    private Drawable emptyStar = getResources().getDrawable(R.drawable.star_outlined);

    private int starSize;
    private int maxStars;
    private int spaceBetween;
    private int progressedStars;

    public AwesomeRatingBar(Context context) {
        this(context, null);
    }

    public AwesomeRatingBar(final Context context, final AttributeSet attrs) {
        super(context, attrs);

        if(attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.AwesomeRatingBar);

            starSize = typedArray.getDimensionPixelSize(R.styleable.AwesomeRatingBar_starSize, 0);
            maxStars = typedArray.getInt(R.styleable.AwesomeRatingBar_maxStars, 0);
            spaceBetween = typedArray.getDimensionPixelSize(R.styleable.AwesomeRatingBar_spaceBetween, 0);
            progressedStars = typedArray.getInt(R.styleable.AwesomeRatingBar_progressedStars, 0);

            typedArray.recycle();
        }

        setFilledStar();
        setEmptyStars();

        setOnTouchListener(onTouchListener);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for(int i = 0; i < maxStars; i++) {

            if(i < progressedStars) {
                filledStar.draw(canvas);
            } else {
                emptyStar.draw(canvas);
            }

            if(i < maxStars - 1) {
                canvas.translate(starSize + spaceBetween, 0f);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = maxStars * filledStar.getBounds().width() + (spaceBetween * (maxStars - 1));
        int height = filledStar.getBounds().height();


        setMeasuredDimension(width, height);
    }

    /*
            Define the star size (in pixel)
         */
    public void setStarSize(int size) {
        if(size < 0) {
            return;
        }

        starSize = dpToPixel(size);
    }

    /*
        Define the max amount of stars allowed
     */
    public void setMaxStars(int max) {
        if(max < 0) {
            return;
        }

        maxStars = max;
    }

    /*
        Define how many stars are actually progressed (e.g how many is "active")
     */
    public void setProgressedStars(int progressed) {
        if(progressed > maxStars) {
            return;
        }

        progressedStars = progressed;

        // This causes the width to change, so re-layout
        requestLayout();
    }

    /*
        Simple function that converts DP to pixel units
     */
    private int dpToPixel(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private void setFilledStar() {
        if(filledStar != null) {
            filledStar.setBounds(0, 0, starSize, starSize);
        }
    }

    private void setEmptyStars() {
        if(emptyStar != null) {
            emptyStar.setBounds(0, 0, starSize, starSize);
        }
    }


    private final OnTouchListener onTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            float x = (int) event.getX();

            if( x >= 0 && x < getWidth() ) {
                int starClicked = (int) (x / starSize);
                progressedStars = starClicked + 1;
            }

            invalidate();
            return true;
        }
    };
}
