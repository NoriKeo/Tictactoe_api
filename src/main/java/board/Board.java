package board;

import java.util.ArrayList;
import java.util.List;



public class Board {

    /*default*/ List<RowFromBoard> rows = new ArrayList<>();

    public Board() {

        for (int row = 0; row < 3; row++) {

            rows.add(new RowFromBoard(row));
        }
    }



    public List<RowFromBoard> getRows() {
        return rows;
    }



    public Field getField(Position position) {
        return rows.get(position.getRow()).getFields().get(position.getColumn());
    }


}

