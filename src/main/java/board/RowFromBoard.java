package board;

import java.util.ArrayList;
import java.util.List;

public class RowFromBoard {
    /*default*/ List<Field> fields = new ArrayList<>();

    public RowFromBoard(int row) {

        for (int column = 0; column < 3; column++) {
            Position position = new Position(row, column);
            fields.add(new Field(position));
        }
    }

    public List<Field> getFields() {
        return fields;
    }




}
