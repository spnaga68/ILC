package realmstudy.lib.customViews;

/**
 * Created by developer on 17/2/17.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

public class NormalTextView extends TextView {


    public NormalTextView(Context context) {
        super(context);
        this.setTextSize(16);
        //Typeface face = Typeface.createFromAsset(context.getAssets(), "Helvetica_Neue.ttf");
       // this.setTypeface(face);
    }

    public NormalTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTextSize(14);
      //  Typeface face = Typeface.createFromAsset(context.getAssets(), "Helvetica_Neue.ttf");
       // this.setTypeface(face);
    }

    public NormalTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setTextSize(14);
       // Typeface face = Typeface.createFromAsset(context.getAssets(), "Helvetica_Neue.ttf");
        //this.setTypeface(face);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


    }

}