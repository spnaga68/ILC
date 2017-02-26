package realmstudy.extras;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import realmstudy.R;

/**
 * Created by developer on 29/12/16.
 */
public class CanvasStudy extends Fragment {

    int co_ordinate;
    private Paint paint=new Paint();
    private Canvas canvas;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.canvas_study, container, false);
        LinearLayout squarelay = (LinearLayout) v.findViewById(R.id.squarelay);





        int x = 200;
        int y = 200;
        Bitmap b = Bitmap.createBitmap(x,y, Bitmap.Config.ARGB_8888);
         canvas = new Canvas(b);
        int radius;
        radius = x/2;
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawPaint(paint);
        // Use Color.parseColor to define HTML colors
        paint.setColor(Color.BLACK);
        canvas.drawCircle(x / 2, y / 2, radius, paint);

        canvas = new Canvas(b);
        int radiuss;
        radiuss = 55;
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#00000000"));
        canvas.drawPaint(paint);
        // Use Color.parseColor to define HTML colors
        paint.setColor(Color.GREEN);
        canvas.drawCircle(x / 2, y / 2, radiuss, paint);
       // canvas.drawCircle(x / 4, y / 4, radius, paint);
        BitmapDrawable ob = new BitmapDrawable(getResources(), b);
        squarelay.setBackground(ob);



        squarelay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                    case MotionEvent.ACTION_UP:
                }

                float xx = PixelConversion.convertPixelsToDp(x, getActivity());
                float yy = PixelConversion.convertPixelsToDp(y, getActivity());
                insideCircle(xx, yy, 100, 100, 100);
//top half
                if (yy <= 100) {
                    //top left half
                    if (xx < 100) {
                        if (insideTriangle(0, 0, 100, 100, 0, 100, (int) xx, (int) yy))
                            co_ordinate = 8;
                        else
                            co_ordinate = 1;
                    }
                    //top right half
                    else {
                        if (insideTriangle(100, 0, 100, 100, 200, 0, (int) xx, (int) yy))
                            co_ordinate = 2;
                        else
                            co_ordinate = 3;
                    }
                }
                //bottom half
                else {
                    //bottom left half
                    if (xx < 100) {
                        if (insideTriangle(0, 100, 0, 200, 100, 100, (int) xx, (int) yy))
                            co_ordinate = 7;
                        else
                            co_ordinate = 6;

                    }
                    //top right half
                    else {

                        if (insideTriangle(100, 100, 100, 200, 200, 200, (int) xx, (int) yy))
                            co_ordinate = 5;
                        else
                            co_ordinate = 4;
                    }
                }

                if (insideCircle(xx, yy, 100, 100, 40)) {
                   co_ordinate=16+co_ordinate;

                } else if (insideCircle(xx, yy, 100, 100, 100)) {
                    co_ordinate=8+co_ordinate;
                }
                System.out.println("NAGAAGAG"+co_ordinate);

                return false;
            }


        });
        return v;
    }

//http://stackoverflow.com/questions/9404736/determining-if-a-point-lies-within-an-ellipse-including-the-edge
    public boolean insideCircle(float x, float y, int center_x, int center_y, int radius) {

        double conditionA = (Math.pow((x - center_x), 2)) + (Math.pow((y - center_y), 2));
        double conditionB = Math.pow(radius, 2);
//        if ()
//            System.out.println("onnn");
        return (conditionA < conditionB || conditionA == conditionB) ? true : false;
//            System.out.println("inside");
//        else
//            System.out.println("outside");
    }


    public boolean insideTriangle(int x1, int y1, int x2, int y2, int x3, int y3, int x, int y) {
        System.out.println("PPP");
   /* Calculate area of triangle ABC */
        double A = area(x1, y1, x2, y2, x3, y3);
   /* Calculate area of triangle PBC */
        double A1 = area(x, y, x2, y2, x3, y3);
   /* Calculate area of triangle PAC */
        double A2 = area(x1, y1, x, y, x3, y3);
   /* Calculate area of triangle PAB */
        double A3 = area(x1, y1, x2, y2, x, y);
   /* Check if sum of A1, A2 and A3 is same as A */
        double sec = Math.abs(A1 + A2 + A3);
        double fir = A;
        return (fir - sec) == 0;
    }


    /* A utility function to calculate area of triangle formed by (x1, y1),
       (x2, y2) and (x3, y3) */
    double area(int x1, int y1, int x2, int y2, int x3, int y3) {

        return Math.abs((x1 * (y2 - y3) + x2 * (y3 - y1) + x3 * (y1 - y2)) / 2.0);
    }
}
