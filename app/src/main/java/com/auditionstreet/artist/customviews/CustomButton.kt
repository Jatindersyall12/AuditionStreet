package com.auditionstreet.artist.customviews

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton

class CustomButton: AppCompatButton {
    constructor(context: Context) : this(context, null) {
        setTypeface()
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {
        setTypeface()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setTypeface()
    }
    private fun setTypeface() {
        if (!isInEditMode) {
            super.setTypeface(Typeface.createFromAsset(context.assets, "Proxima Nova Bold.otf"))
        }
    }
}