package Board;

import gamesInfo.Position;

import java.util.ArrayList;
import java.util.List;



public class Board {

    /*default*/ List<RowFromBoard> rows = new ArrayList<>();

    public Board() {

        for (int row = 0; row < 3; row++) {

            rows.add(new RowFromBoard(row));
        }
    }

    public void print() {
        System.out.println("✮✮✮✮✮✮✮✮✮✮✮✮✮✮✮✮✮✮✮✮✮✮✮✮✮✮✮✮✮✮✮");
        for (RowFromBoard row : rows) {
            row.print();

        }
        System.out.println("✮✮✮✮✮✮✮✮✮✮✮✮✮✮✮✮✮✮✮✮✮✮✮✮✮✮✮✮✮✮✮");


    }

    public List<RowFromBoard> getRows() {
        return rows;
    }

    public boolean isFull() {
        for (RowFromBoard row : rows) {
            if (!row.isFull()) {
                return false;
            }
        }
        return true;
    }

    public Field getField(Position position) {
        return rows.get(position.getRow()).getFields().get(position.getColumn());
    }


}

