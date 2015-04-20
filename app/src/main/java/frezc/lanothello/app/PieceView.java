package frezc.lanothello.app;

import android.content.Context;
import android.graphics.*;
import android.view.View;
import frezc.lanothello.app.game.Othello;

/**
 * Created by freeze on 2015/4/20.
 */
public class PieceView extends View {
    private int playerNO;
    private int width;
    private RectF dst;

    public static Bitmap BLACKPIECE,WHITEPIECE;
    public static Rect SRC = new Rect(0,0,50,50);
    static {
        BLACKPIECE = Bitmap.createBitmap(50,50, Bitmap.Config.ARGB_8888);
        WHITEPIECE = Bitmap.createBitmap(50,50, Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xff000000);
        Canvas canvas = new Canvas(BLACKPIECE);
        canvas.drawCircle(25,25,25,paint);
        canvas = new Canvas(WHITEPIECE);
        canvas.drawCircle(25,25,25,paint);
    }

    public PieceView(Context context, int x, int y, int width, int playerNO) {
        super(context);
        this.width = width;
        this.playerNO = playerNO;
        dst = new RectF(0,0,width,width);
        setX(x);
        setY(y);
    }

    public int getPlayerNO() {
        return playerNO;
    }

    public void flip(){
        playerNO = playerNO == Othello.PLAYERONE ? Othello.PLAYERTWO : Othello.PLAYERONE;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(playerNO == Othello.PLAYERONE){
            canvas.drawBitmap(BLACKPIECE, SRC, dst, null);
        }else {
            canvas.drawBitmap(WHITEPIECE, SRC, dst, null);
        }
    }
}
