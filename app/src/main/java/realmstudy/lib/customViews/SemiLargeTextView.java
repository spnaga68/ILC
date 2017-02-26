package realmstudy.lib.customViews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by developer on 18/2/17.
 */
public class SemiLargeTextView extends TextView {
    public SemiLargeTextView(Context context) {
        super(context);
        this.setTextSize(20);
    }

    public SemiLargeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTextSize(20);
    }

    public SemiLargeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setTextSize(20);
    }


}
