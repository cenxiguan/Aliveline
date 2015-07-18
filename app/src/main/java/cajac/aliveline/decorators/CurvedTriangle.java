package cajac.aliveline.decorators;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

/**
 * Created by Chungyuk Takahashi on 6/30/2015.
 */
public class CurvedTriangle extends Drawable {

    private Paint paint;
    private int width;
    private int length;
    private int color;
    private int selectedColor;

    public CurvedTriangle(int width, int length, int color, int selectedColor) {
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
        canvas.drawRect(width / 2, 0, width, length / 2, paint);
        paint.setColor(selectedColor);
        RectF oval = new RectF(0, 0, width, length);
        canvas.drawOval(oval, paint);

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
