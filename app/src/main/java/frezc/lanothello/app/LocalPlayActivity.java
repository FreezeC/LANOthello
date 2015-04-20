package frezc.lanothello.app;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.RelativeLayout;
import frezc.lanothello.app.game.Othello;
import frezc.lanothello.app.game.Player;

/**
 * Created by freeze on 2015/4/18.
 */
public class LocalPlayActivity extends ActionBarActivity {
    private RelativeLayout chessboard,layoutLoading;
    private OthelloView othelloView;

    private int playerNO = 0;
    private Player[] players = new Player[2];
    private Othello othello = new Othello();

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_game);

        othelloView = (OthelloView) findViewById(R.id.othelloview);
        chessboard = (RelativeLayout) findViewById(R.id.chessboard);
        layoutLoading = (RelativeLayout) findViewById(R.id.layout_loading);

        chessboard.setVisibility(View.VISIBLE);
        layoutLoading.setVisibility(View.GONE);

        players[0] = new Player();
        players[1] = new Player();
        players[Othello.PLAYERONE].playOthello(othello);
        players[Othello.PLAYERTWO].playOthello(othello);

        othello.startNewGame();
    }
}
