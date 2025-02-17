package Board;

import gamesInfo.Position;

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

    public void print() {
        System.out.println("            " + this.fields.get(0).getGameCharacter() + this.fields.get(1).getGameCharacter() + this.fields.get(2).getGameCharacter());

    }

    public boolean isFull() {
        for (Field field : fields) {
            if (field.isEmpty()) {
                return false;
            }
        }
        return true;
    }


}
