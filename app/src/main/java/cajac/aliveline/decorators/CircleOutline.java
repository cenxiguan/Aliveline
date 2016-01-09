package cajac.aliveline.decorators;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

/**
 * Created by Chungyuk Takahashi on 8/21/2015.
 */
public class CircleOutline extends Drawable {

    private Paint paint;
    private int width;
    private int length;
    private int color;
    private int selectedColor;

    public CircleOutline(int width, int length, int color, int selectedColor) {
        this.width = width;
        this.length = length;
        this.color = color;
        this.selectedColor = selectedColor;
        paint = new Paint();
        paint.setAntiAlias(true);
    }


    @Override
    public void draw(Canvas canvas) {
        paint.setColor(color);
        RectF oval = new RectF(0, 0, width, length);
        canvas.drawOval(oval, paint);
        paint.setColor(selectedColor);
        RectF innerOval = new RectF((int) (width * 0.05), (int) (length * 0.05),
                (int) (width * 0.95), (int) (length * 0.95));
        canvas.drawOval(innerOval, paint);
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        paint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

}
