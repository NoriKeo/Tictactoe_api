package gamesInfo;

public class Position {
    /*default*/ int row;
    /*default*/ int column;
    /*default*/ int index;

    public Position(int row, int column) {
        this.row = row;
        this.column = column;
        this.index = row * 3 + column + 1;
    }

    public Position(int index) {
        this.index = index;
        this.row = (index - 1) / 3;
        this.column = (index - 1) % 3;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        Position position = (Position) object;
        return index == position.index;
    }

}
