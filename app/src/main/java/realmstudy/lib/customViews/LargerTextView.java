package realmstudy.lib.customViews;

/**
 * Created by developer on 17/2/17.
 */
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

public class LargerTextView extends TextView {


    public LargerTextView(Context context) {
        super(context);
        this.setTextSize(24);
        //Typeface face = Typeface.createFromAsset(context.getAssets(), "Helvetica_Neue.ttf");
        // this.setTypeface(face);
    }

    public LargerTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTextSize(24);
        //  Typeface face = Typeface.createFromAsset(context.getAssets(), "Helvetica_Neue.ttf");
        // this.setTypeface(face);
    }

    public LargerTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setTextSize(24);
        // Typeface face = Typeface.createFromAsset(context.getAssets(), "Helvetica_Neue.ttf");
        //this.setTypeface(face);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


    }

}
