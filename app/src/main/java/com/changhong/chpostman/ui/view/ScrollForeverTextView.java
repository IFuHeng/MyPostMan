package com.changhong.chpostman.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

@SuppressLint("AppCompatCustomView")
public class ScrollForeverTextView extends TextView {
    private boolean mIsFocused = true;

    public ScrollForeverTextView(Context context) {
        super(context);
    }

    public ScrollForeverTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollForeverTextView(Context context, AttributeSet attrs,
                                 int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean isFocused() {
        return this.mIsFocused;
    }

    public void setFocused(boolean isFocused) {
        this.mIsFocused = isFocused;
    }
}

