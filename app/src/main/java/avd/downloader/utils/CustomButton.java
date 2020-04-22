

package avd.downloader.utils;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import avd.downloader.R;

public class CustomButton extends android.support.v7.widget.AppCompatTextView {
    public CustomButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setBackground(getResources().getDrawable(R.drawable.rounded_button));
        setTextColor(Color.BLACK);
    }
}
