package frezc.lanothello.app;

import android.content.Context;
import android.graphics.*;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import frezc.lanothello.app.game.Othello;
import frezc.lanothello.app.game.Player;

import java.util.List;

/**
 * Created by freeze on 2015/4/17.
 */
public class OthelloView extends View implements View.OnTouchListener, Othello.OnFlipListener{
    private int width;
    private int cellWidth;
    private float strokeWidth = 1;
    private Bitmap background;
    private Paint paint = new Paint();
    private Player player;
    private PieceView[][] pieceViews;

    private Handler handler;

    public interface OnPutCompleteListener{
        void onPutComplete();
    }

    /**
     * flip animation here
     * @param flipPieces
     */
    @Override
    public void onFlip(List<Othello.Location> flipPieces) {
        for(Othello.Location loc : flipPieces) {
            pieceViews[loc.x][loc.y].flip();
            Log.i("test", "flip piece location: " + loc.x + "," + loc.y + " ");
        }
        invalidate();
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
        pieceViews = new PieceView[8][8];
        setOnTouchListener(this);
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    private Bitmap createBackground() {
        Bitmap bitmap = Bitmap.createBitmap(width,width, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        canvas.drawColor(0xffF7EED6);
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

    public void setPiece(int x, int y, int playerNO){
        if(pieceViews[x][y] == null){
            pieceViews[x][y] = new PieceView(getContext(), x, y, cellWidth-1, playerNO);
        }else {
            if(pieceViews[x][y].getPlayerNO() != playerNO){
                pieceViews[x][y].flip();
            }
        }
    }

    public void forceDraw(){
        if(player != null){
            int[][] chessboard = player.getOthello().getChessboard();
            for(int i=0; i<8; i++){
                for(int j=0; j<8; j++){
                    if(chessboard[i][j] == -1){
                        pieceViews[i][j] = null;
                    }else {
                        setPiece((int) (i * (cellWidth+strokeWidth)+strokeWidth),
                                (int) (j * (cellWidth+strokeWidth)+strokeWidth),
                                player.getPlayerNO());
                    }
                }
            }
            invalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        cellWidth = (int) ((width - strokeWidth * 9) / 8);
        background = createBackground();
        setMeasuredDimension(width, width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(background, 0, 0, paint);

        for(int i=0; i<8; i++){
            for(int j=0; j<8; j++) {
                if(pieceViews[i][j] != null) {
                    pieceViews[i][j].draw(canvas);
                }
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Othello.Location location = getLocation(event.getX(),event.getY());
        if(player != null){
            if(player.putPiece(location.x,location.y)){
                setPiece((int) (location.y * (cellWidth+strokeWidth)+strokeWidth),
                        (int) (location.x * (cellWidth+strokeWidth)+strokeWidth),
                        player.getPlayerNO());
                invalidate();
                Toast.makeText(getContext(),"opponent turn!",Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }

    private Othello.Location getLocation(float x, float y){
        int lx = (int) (x / (cellWidth+strokeWidth));
        lx = lx > 7 ? 7 : lx;
        int ly = (int) (y / (cellWidth+strokeWidth));
        ly = lx > 7 ? 7 : ly;
        return new Othello.Location(ly,lx);
    }
}
