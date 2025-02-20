package win;

import Board.Board;
import ControllerandConnection.ConnectionHandler;
import game.GamePlayMove;
import gamesInfo.Position;
import player.Computer;
import player.MatchServer;
import readAndWrite.MatchHistoryReader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Wintry {

    static Position position;
    static Board board;
    public static boolean computerWin = false;
    public static boolean playerWin = false;
    public static boolean draw = false;
    static Position computerPosition;
    public static int matchendReason;


    public Wintry() {

    }

    public void winTryCheck(){
        if (!winTryPlayer()){

        }
    }

    public boolean winTryPlayer(){
        for(int i : MatchServer.playerPlaysList){
        position = new Position(i);
        board.getRows().get(position.getRow()).getFields().get(position.getColumn()).setGameCharacter('♡');

        board.print();
        GamePlayMove move = new GamePlayMove(position, '♡');

        if (WinCheck.isWin(board, move)) {
           playerWin = true;
            try (Connection connection = ConnectionHandler.getConnection()) {
                PreparedStatement winUpate = connection.prepareStatement(
                        "UPDATE match_history SET win = true,winPlayer = true,winComputer = false,draw = false WHERE match_id = ?  ");
                winUpate.setInt(1, MatchHistoryReader.matchid);
                winUpate.executeUpdate();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            //to do
        }else if(Computer.draw) {
            draw = true;
            try (Connection connection = ConnectionHandler.getConnection()) {
                PreparedStatement winUpate = connection.prepareStatement(
                        "UPDATE match_history SET win = false,winPlayer = false,winComputer = false,draw = true WHERE match_id = ?  ");
                winUpate.setInt(1, MatchHistoryReader.matchid);
                winUpate.executeUpdate();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }


        }
        }
        return playerWin;
    }

    public boolean winTryComputer(){
        for (int i : MatchServer.computerPlaysList ){
            computerPosition = new Position(i);
            board.getRows().get(computerPosition.getRow()).getFields().get(computerPosition.getColumn()).setGameCharacter('¤');
            GamePlayMove computermove = new GamePlayMove(computerPosition, '¤');
            board.print();
            if (WinCheck.isWin(board, computermove)) {
                computerWin = true;
                try (Connection connection = ConnectionHandler.getConnection()) {
                    PreparedStatement winUpate = connection.prepareStatement(
                            "UPDATE match_history SET win = true,winPlayer = false,winComputer = true,draw = false WHERE match_id = ?  ");
                    winUpate.setInt(1, MatchHistoryReader.matchid);
                    winUpate.executeUpdate();

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                //to do
            }else if (Computer.draw) {
                draw = true;
                try (Connection connection = ConnectionHandler.getConnection()) {
                    PreparedStatement winUpate = connection.prepareStatement(
                            "UPDATE match_history SET win = false,winPlayer = false,winComputer = false,draw = true WHERE match_id = ?  ");
                    winUpate.setInt(1, MatchHistoryReader.matchid);
                    winUpate.executeUpdate();

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }


            }
        }
        return computerWin;
    }


}
