package frezc.lanothello.app.game;

/**
 * Created by freeze on 2015/4/19.
 */
public interface Chess {
    int RESULT_TIE = -1;
    int FULL_PLAYER = -1;

    boolean putPiece(int x, int y, int playerNO);
    boolean isGameOver();
    int getResult();
    int addPlayer(Player player);
}
