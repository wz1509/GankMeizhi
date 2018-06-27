package com.wazing.gankmeizhi.ui.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;

public class ScaleImageView extends AppCompatImageView {

    private static final String TAG = "zz";

    public ScaleImageView(Context context) {
        this(context, null);
    }

    public ScaleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScaleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final Drawable drawable = getDrawable();
        if (drawable == null) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            int intrinsicWidth = drawable.getIntrinsicWidth();
            int intrinsicHeight = drawable.getIntrinsicHeight();
            Log.d(TAG, "onMeasure: intrinsicWidth = " + intrinsicWidth + "\t\tintrinsicHeight = " + intrinsicHeight);
            if (intrinsicWidth == 0 || intrinsicHeight == 0) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                return;
            }
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = (int) Math.ceil((float) width * (float) intrinsicHeight / (float) intrinsicWidth);
            Log.d(TAG, "onMeasure: width::" + width + "\t\theight::" + height);
            setMeasuredDimension(width, height);
        }
    }
}
