package frezc.lanothello.app.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by freeze on 2015/4/16.
 */
public class Othello implements Chess{
    private int[][] chessboard = new int[8][8];
    private int[] playersPieces;
    private Player[] players;
    private int nowPlayerNO = 0;
    private int playerNumber = 0;
    private List<Location> flipPieces;
    private OnFlipListener onFlipListener = null;

    private boolean gameStarted = false;

    public static final Dir DIR_LEFTUP = new Dir(-1,-1);
    public static final Dir DIR_UP = new Dir(0,-1);
    public static final Dir DIR_RIGHTUP = new Dir(1,-1);
    public static final Dir DIR_RIGHT = new Dir(1,0);
    public static final Dir DIR_RIGHTDOWN = new Dir(1,1);
    public static final Dir DIR_DOWN = new Dir(0,1);
    public static final Dir DIR_LEFTDOWN = new Dir(-1,1);
    public static final Dir DIR_LEFT = new Dir(-1,0);

    public static final int PLAYERONE = 0;
    public static final int PLAYERTWO = 1;

    public static final Dir[] SAMPLE_DIRS = new Dir[]{
            DIR_LEFTUP,DIR_UP,DIR_RIGHTUP,DIR_RIGHT,DIR_RIGHTDOWN,DIR_DOWN,DIR_LEFTDOWN,DIR_LEFT
    };

    public interface OnFlipListener{
        void onFlip(List<Location> flipPieces);
    }

    public void setOnFlipListener(OnFlipListener onFlipListener){
        this.onFlipListener = onFlipListener;
    }

    public static class Location{
        public int x,y;

        public Location(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public static class Dir{
        int xoffset;
        int yoffset;

        Dir(int xoffset, int yoffset){
            this.xoffset = xoffset;
            this.yoffset = yoffset;
        }
    }

    public Othello(){
        players = new Player[2];
        playersPieces = new int[2];
        flipPieces = new ArrayList<Location>(32);
    }

    public int getMaxPlayers(){
        return players.length;
    }

    public void startNewGame(){
        if(playerNumber < 2){
            return;
        }
        resetChessboard();
        chessboard[3][3] = 0;
        chessboard[3][4] = 1;
        chessboard[4][3] = 1;
        chessboard[4][4] = 0;
        playersPieces[0] = 2;
        playersPieces[1] = 2;
        nowPlayerNO = 0;
        gameStarted = true;
    }

    public boolean putPiece(int x, int y, int playerNO){
        if(this.nowPlayerNO == playerNO && gameStarted){
            List dirs = checkAvailable(x,y,playerNO);
            if(dirs.isEmpty()){
                return false;
            }else {
                chessboard[x][y] = playerNO;
                flipPieces.clear();
                for(int i=0; i<dirs.size(); i++){
                    Dir dir = (Dir) dirs.get(i);
                    int ox = x + dir.xoffset;
                    int oy = y + dir.yoffset;
                    while(chessboard[ox][oy] != playerNO){
                        flipPieces.add(new Location(ox,oy));
                        chessboard[ox][oy] = flip(chessboard[ox][oy]);
                        ox += dir.xoffset;
                        oy += dir.yoffset;
                    }
                }
                if(onFlipListener != null){
                    onFlipListener.onFlip(flipPieces);
                }
                playersPieces[playerNO] += flipPieces.size()+1;
                this.nowPlayerNO = playerNO == PLAYERONE ? PLAYERTWO : PLAYERONE;
                playersPieces[playerNO] -= flipPieces.size();
                return true;
            }
        }else {
            return false;
        }
    }

    @Override
    public boolean isGameOver() {
        if(playersPieces[0]+playersPieces[1] >= 64){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public int getResult() {
        return playersPieces[PLAYERONE]>playersPieces[PLAYERTWO] ? PLAYERONE:PLAYERTWO;
    }

    @Override
    public int addPlayer(Player player) {
        if(playerNumber < players.length){
            players[playerNumber] = player;
            return playerNumber++;
        }else {
            return FULL_PLAYER;
        }
    }

    private List checkAvailable(int x, int y, int playerNO) {
        List<Dir> dirs = new ArrayList<>(8);
        if(chessboard[x][y] == -1){
            for(int i=0; i<9; i++){
                addAvailableDir(dirs, x, y, SAMPLE_DIRS[i], playerNO);
            }
        }
        return dirs;
    }

    private void addAvailableDir(List<Dir> list, int x, int y, Dir dir, int playerNO){
        if(insideBoard(x+dir.xoffset,y+dir.yoffset) &&
                chessboard[x+dir.xoffset][y+dir.yoffset] != -1 &&
                chessboard[x+dir.xoffset][y+dir.yoffset] != playerNO){
            if(checkDir(x+dir.xoffset,y+dir.yoffset, dir,playerNO)){
                list.add(dir);
            }
        }
    }

    private void resetChessboard(){
        for (int i=0; i<chessboard.length; i++){
            Arrays.fill(chessboard[i], -1);
        }
    }

    private boolean insideBoard(int x, int y){
        if(x >= 0 && x < 8 && y >= 0 && y < 8){
            return true;
        }else {
            return false;
        }
    }

    /**
     * check whether there is same piece
     */
    private boolean checkDir(int x, int y, Dir offset, int playerNO){
        while(insideBoard(x+offset.xoffset,y+offset.yoffset)){
            x += offset.xoffset;
            y += offset.yoffset;
            if(chessboard[x][y] == playerNO){
                return true;
            }
        }
        return false;
    }

    private int flip(int now){
        return now == 0 ? 1 : 0;
    }

    @Override
    public int[][] getChessboard() {
        return chessboard;
    }
}