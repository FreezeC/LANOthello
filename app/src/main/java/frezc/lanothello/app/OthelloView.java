package frezc.lanothello.app;

import android.content.Context;
import android.graphics.*;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import frezc.lanothello.app.game.Othello;

/**
 * Created by freeze on 2015/4/17.
 */
public class OthelloView extends View implements View.OnTouchListener{
    private int width;
    private int cellWidth;
    private float strokeWidth = 1;
    private Bitmap background;
    private Paint paint = new Paint();
    private OnPutPieceListener onPutPieceListener;
    private Othello othello;

    private Handler handler;

    /**
     * run on UI thread
     */
    public interface OnPutPieceListener{
        void onPut(int x, int y);
    }

    public OthelloView(Context context) {
        super(context);
        init(context);
    }

    public OthelloView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        handler = new Handler(context.getMainLooper());
        setOnTouchListener(this);
    }

    public void setOnPutPieceListener(OnPutPieceListener onPutPieceListener) {
        this.onPutPieceListener = onPutPieceListener;
    }

    public void setOthello(Othello othello) {
        this.othello = othello;
    }

    private Bitmap createBackground() {
        Bitmap bitmap = Bitmap.createBitmap(width,width, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        canvas.drawRect(new RectF(0, 0, width-1, width-1), paint);
        for(int i=1; i<=7; i++){
            canvas.drawLine((strokeWidth+cellWidth)*i,0,(strokeWidth+cellWidth)*i,width-1,paint);
        }
        for(int i=1; i<=7; i++){
            canvas.drawLine(0,(strokeWidth+cellWidth)*i,width-1,(strokeWidth+cellWidth)*i,paint);
        }

        return bitmap;
    }




    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        cellWidth = (int) ((width - strokeWidth * 9) / 8);
        background = createBackground();
        setMeasuredDimension(width,width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(background, 0, 0, paint);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final Othello.Location location = getLocation(event.getX(),event.getY());
        if(onPutPieceListener != null){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onPutPieceListener.onPut(location.x,location.y);
                }
            });
        }
        return false;
    }

    private Othello.Location getLocation(float x, float y){
        int lx = (int) (x / (cellWidth+strokeWidth));
        lx = lx > 7 ? 7 : lx;
        int ly = (int) (y / (cellWidth+strokeWidth));
        ly = lx > 7 ? 7 : ly;
        return new Othello.Location(lx,ly);
    }
}
