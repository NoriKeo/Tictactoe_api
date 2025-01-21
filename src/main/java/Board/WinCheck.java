package Board;

import game.GamePlayMove;

import java.util.ArrayList;
import java.util.List;

public class WinCheck {



    public static boolean isWin(Board board, GamePlayMove move) {


        return rowwin(board, move) || columnwin(board, move) || diagonalWin(board, move);
    }

    public static boolean rowwin(Board board, GamePlayMove move) {
        Position position;
        position = move.getPosition();

        List<Position> row = new ArrayList<>();
        for (Field field : board.getRows().get(position.getRow()).getFields()) {
            row.add(field.getPosition());
        }

        return gamecharactercheck(row, move.getGamecharacter(), board);
    }

    public static boolean columnwin(Board board, GamePlayMove move) {
        Position position = move.getPosition();
        List<Position> column = new ArrayList<>();

        for (RowFromBoard row : board.getRows()) {
            column.add(row.getFields().get(position.getColumn()).getPosition());
        }

        return gamecharactercheck(column, move.getGamecharacter(), board);

    }

    public static boolean diagonalWin(Board board, GamePlayMove move) {


        List<Position> diagonatopleft = List.of(new Position(1), new Position(5), new Position(9));
        List<Position> diagonatopright = List.of(new Position(3), new Position(5), new Position(7));


        return gamecharactercheck(diagonatopright, move.gamecharacter, board) || gamecharactercheck(diagonatopleft, move.gamecharacter, board);


    }

    public static boolean gamecharactercheck(List<Position> diagonatopleft, char gamecharacter, Board board) {
        for (Position fieldposition : diagonatopleft) {
            if (board.getRows().get(fieldposition.getRow()).getFields().get(fieldposition.getColumn()).getGameCharacter() != gamecharacter) {
                return false;
            }
        }
        return true;
    }


}
