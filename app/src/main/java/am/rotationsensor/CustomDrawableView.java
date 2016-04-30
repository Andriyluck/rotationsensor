package am.rotationsensor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by andriy on 4/26/16.
 */
public class CustomDrawableView extends View {
    private Paint paint = new Paint();
    private Paint paintX = new Paint();
    private Paint paintY = new Paint();
    private Paint paintZ = new Paint();

    float x, xval, y, yval, z, zval;
    String text = "";

    public CustomDrawableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setColor(0xff555555);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setTextSize(40);
        paint.setAntiAlias(true);

        paintX.setColor(0xff00ff00); // green
        paintX.setStyle(Paint.Style.STROKE);
        paintX.setStrokeWidth(2);
        paintX.setTextSize(40);
        paintX.setAntiAlias(true);

        paintY.setColor(0xff0000ff); // blue
        paintY.setStyle(Paint.Style.STROKE);
        paintY.setStrokeWidth(2);
        paintY.setTextSize(40);
        paintY.setAntiAlias(true);

        paintZ.setColor(0xffff0000); // red
        paintZ.setStyle(Paint.Style.STROKE);
        paintZ.setStrokeWidth(2);
        paintZ.setTextSize(40);
        paintZ.setAntiAlias(true);
    }

    public CustomDrawableView(Context context) {
        this(context, null);
    }

    public void update(float x, float xval, float y, float yval, float z, float zval, String text) {
        this.x = x;
        this.y = y;
        this.z = z;

        this.xval = xval;
        this.yval = yval;
        this.zval = zval;

        this.text = text;

        this.invalidate();
    }

    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        int centerx = width / 2;
        int centery = height / 2;
        canvas.drawLine(centerx, 0, centerx, height, paint);
        canvas.drawLine(0, centery, width, centery, paint);

        if(!Float.isNaN(x))
        {
            canvas.rotate(x, centerx, centery);
            canvas.drawLine(centerx, -1000, centerx, +1000, paintX);
            canvas.rotate(-1 * x, centerx, centery);
        }

        if(!Float.isNaN(y))
        {
            canvas.rotate(y, centerx, centery);
            canvas.drawLine(centerx, -1000, centerx, +1000, paintY);
            canvas.rotate(-1 * y, centerx, centery);
        }

        if(!Float.isNaN(z))
        {
            canvas.rotate(z, centerx, centery);
            canvas.drawLine(centerx, -1000, centerx, +1000, paintZ);
            canvas.rotate(-1 * z, centerx, centery);
        }

        canvas.drawText(String.format("X %4.1f%s    (%.4f)", x, (char) 0x00B0, xval), centerx + 50, centery + 50, paintX);
        canvas.drawText(String.format("Y %4.1f%s    (%.4f)", y, (char) 0x00B0, yval), centerx + 50, centery + 100, paintY);
        canvas.drawText(String.format("Z %4.1f%s    (%.4f)", z, (char) 0x00B0, zval), centerx + 50, centery + 150, paintZ);
        canvas.drawText(text, centerx + 50, centery + 200, paint);

    }
}
