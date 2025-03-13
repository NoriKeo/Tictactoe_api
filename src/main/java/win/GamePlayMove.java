package win;

import board.Position;

public class GamePlayMove {
    /*default*/ Position position;
    /*default*/ char gamecharacter;

    public GamePlayMove(Position position, char gamecharacter) {
        this.position = position;
        this.gamecharacter = gamecharacter;
    }

    public Position getPosition() {
        return position;
    }


    public char getGamecharacter() {
        return gamecharacter;
    }


}
