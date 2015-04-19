package frezc.lanothello.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by freeze on 2015/4/20.
 */
public class PieceView extends View {
    private boolean isBlack = true;
    private int width;

    public static Bitmap BLACKPIECE,WHITEPIECE;
    static {
        BLACKPIECE = Bitmap.createBitmap(50,50, Bitmap.Config.ARGB_8888);
        WHITEPIECE = Bitmap.createBitmap(50,50, Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xff000000);
        Canvas canvas = new Canvas(BLACKPIECE);
        canvas.drawCircle();
    }

    public PieceView(Context context, int width, boolean isBlack) {
        super(context);
        this.width = width;
        this.isBlack = isBlack;
    }

    @Override
    protected void onDraw(Canvas canvas) {

    }
}
