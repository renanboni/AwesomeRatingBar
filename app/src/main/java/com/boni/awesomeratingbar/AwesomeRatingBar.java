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

    // Default values
    private int starSize = 32;
    private int maxStars = 5;
    private int spaceBetween = 0;
    private int progressedStars = 0;
    private boolean isIndicator = false;

    public AwesomeRatingBar(Context context) {
        this(context, null);
    }

    public AwesomeRatingBar(final Context context, final AttributeSet attrs) {
        super(context, attrs);

        if(attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.AwesomeRatingBar);

            starSize = typedArray.getDimensionPixelSize(R.styleable.AwesomeRatingBar_starSize, starSize);
            maxStars = typedArray.getInt(R.styleable.AwesomeRatingBar_maxStars, maxStars);
            spaceBetween = typedArray.getDimensionPixelSize(R.styleable.AwesomeRatingBar_spaceBetween, spaceBetween);
            progressedStars = typedArray.getInt(R.styleable.AwesomeRatingBar_progressedStars, progressedStars);
            isIndicator = typedArray.getBoolean(R.styleable.AwesomeRatingBar_indicator, isIndicator);

            Drawable filled = typedArray.getDrawable(R.styleable.AwesomeRatingBar_filled);
            Drawable outlined = typedArray.getDrawable(R.styleable.AwesomeRatingBar_outlined);

            if(filled != null) {
                filledStar = filled;
            }

            if(outlined != null) {
                emptyStar = outlined;
            }

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

    /*
        In this case, match_parent and wrap_content won't make difference,
            always will be set wrap_content for the width,
            for the height, it should be always wrap content
     */
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

    /*
        return how many stars was clicked
     */
    public int getRating() {
        return progressedStars;
    }

    /*
        Set if the stars are indicator or not (in other words, if they're clickable or not)
     */
    public void setIndicator(boolean isIndicator) {
        this.isIndicator = isIndicator;
    }

    public boolean isIndicator() {
        return isIndicator;
    }

    /*
        Draw star on screen (if not indicador) accordly on how many stars was touched,
            Maybe (actually, I'm pretty sure) is possible to improve the code below
     */
    private final OnTouchListener onTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            if(!isIndicator) {
                float x = (int) event.getX();

                if( x >= 0 && x < getWidth() ) {
                    int a = 0;
                    int b = starSize;

                    for(int i = 0; i < maxStars; i++) {
                        if(x > a && x < b) {
                            progressedStars = i + 1;
                            performClick();
                            break;
                        }

                        a += (starSize + spaceBetween);
                        b = a + starSize;
                    }
                }
            }

            /*
                Mark the view to be re-draw in a close future
             */
            invalidate();
            return true;
        }
    };
}
