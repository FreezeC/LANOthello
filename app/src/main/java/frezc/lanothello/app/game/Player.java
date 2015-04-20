package frezc.lanothello.app.game;

/**
 * Created by freeze on 2015/4/16.
 */
public class Player {
    private int playerNO;
    private Chess othello;

    public boolean putPiece(int x, int y){
        return othello.putPiece(x,y,playerNO);
    }

    public boolean playOthello(Chess othello){
        this.othello = othello;
        playerNO = othello.addPlayer(this);
        return playerNO != Chess.FULL_PLAYER;
    }

    public Chess getOthello() {
        return othello;
    }

    public int getPlayerNO() {
        if(othello == null){
            return -1;
        }
        return playerNO;
    }
}
