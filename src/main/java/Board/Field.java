package Board;

import gamesInfo.Position;

public class Field {

    /*default*/ char gamecharacter;
    /*default*/ Position position;

    public Field(Position position) {
        this.position = position;
        this.gamecharacter = '.';
    }

    public char getGameCharacter() {
        return gamecharacter;
    }

    public void setGameCharacter(char gamecharacter) {
        this.gamecharacter = gamecharacter;
    }

    public boolean isEmpty() {
        return this.gamecharacter == '.';
    }

    public Position getPosition() {
        return position;
    }

    public boolean isPlayer() {
        return this.gamecharacter == '♡';
    }

    public boolean isComputer() {
        return this.gamecharacter == '¤';
    }
}
