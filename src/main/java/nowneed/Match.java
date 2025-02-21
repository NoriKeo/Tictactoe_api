/*
package nowneed;

import Board.Board;
import game.GamePlayMove;
import game.KeepPlaying;
import gamesInfo.BoardhistoryArray;
import gamesInfo.Position;
import player.Computer;
import player.Player;
import readAndWrite.MatchHistoryWrite;
import Board.Print;
import java.sql.SQLException;
import java.util.ArrayList;
import win.WinCheck;
public class Match {
    */
/*default*//*
 static Board board;
    */
/*default*//*
public static boolean playerWin = false;
    */
/*default*//*
public static boolean computerWin = false;
    */
/*default*//*
public static int rounds;
    BoardhistoryArray scoreBoard = new BoardhistoryArray();
    public static int match;
    static Position position;
    public static int input;
    public static long t1;
    public static long t2;
    static Position computerPosition;
    static boolean breckBoard = false;
    static int roundprintsafe;

    public Match() {
        board = new Board();
    }

    public void start() {
        if (rounds == 0) {
            BoardhistoryArray.playerFieldsbreck = new ArrayList<>();
           BoardhistoryArray.computerFieldsbreck = new ArrayList<>();
        }
        if (Print.breckBoard() != null) {
            try {
                Print.initializeDatabase();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            Print.breckBoard();
            board = Print.boardBreck;
            board.print();

        } else {

            // }
            board = new Board();
            board.print();
        }
        do {
            t1 = System.currentTimeMillis();
            input = Player.getInstance().input;
            //input = player.Player.getInstance().test(board);
            position = new Position(input);
            board.getRows().get(position.getRow()).getFields().get(position.getColumn()).setGameCharacter('♡');
            board.print();
            GamePlayMove move = new GamePlayMove(position, '♡');
            if (!WinCheck.isWin(board, move)) {
                BoardhistoryArray.safeGamePlayPlayer();


            }
            if (WinCheck.isWin(board, move)) {
                BoardhistoryArray.safeGamePlayPlayer();
                playerWin = true;
                t2 = System.currentTimeMillis();
                if (!KeepPlaying.keepPlaying(board)) {
                    break;
                }

            }
            computerPosition = Computer.getComputerMovement(board);
            board.getRows().get(computerPosition.getRow()).getFields().get(computerPosition.getColumn()).setGameCharacter('¤');
            GamePlayMove computermove = new GamePlayMove(computerPosition, '¤');
            board.print();
            if (!WinCheck.isWin(board, computermove)) {
                //BoardhistoryArray.safeGamePlayComputer();


            }
            if (WinCheck.isWin(board, computermove)) {
                computerWin = true;
                //BoardhistoryArray.safeGamePlayComputer();

                t2 = System.currentTimeMillis();
                if (!KeepPlaying.keepPlaying(board)) {

                    break;
                }
            }


            if (board.isFull() && !KeepPlaying.keepPlaying(board)) {
                try {
                    MatchHistoryWrite.initializeDatabase();
                    MatchHistoryWrite.writer();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Game Over");
                break;
            }
            if (!WinCheck.isWin(board, move) && !WinCheck.isWin(board, computermove)) {
                if (rounds == 0) {
                    try {
                        MatchHistoryWrite.initializeDatabase();
                        MatchHistoryWrite.writer();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                } else {
                    try {
                        MatchHistoryWrite.initializeDatabase();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    MatchHistoryWrite.updater();

                }


            }
            //System.out.println(computerWin + " computer winni");
            //gamesInfo.BoardhistoryArray.fieldbrecks();
            if (WinCheck.isWin(board, computermove)) {
                computerWin = false;
                playerWin = false;
            }


            rounds++;
        } while (!board.isFull());


    }

    public static Board getBoard() {
        return board;
    }

    public static void setBoard(Board board) {
        Match.board = board;
    }
}
*/
