package game;

import board.Board;
import board.Field;
import board.Position;
import board.RowFromBoard;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Player {

    private static Player instance;
    public static Player getInstance() {
        if (instance == null) {
            instance = new Player();
        }
        return instance;
    }




    public  boolean freeField(Board board, int input2) {
        List<Position> freeFields = new ArrayList<>();
        for (RowFromBoard row : board.getRows()) {
            for (Field field : row.getFields()) {
                if (field.isEmpty()) {
                    freeFields.add(field.getPosition());
                }
            }
        }
        for (Position field : freeFields) {
            if (field.getIndex() == input2) {
                return true;
            }
        }
        return false;
    }




}







