package player;

import java.util.ArrayList;
import java.util.List;

import readAndWrite.MatchHistoryRead;

public class MatchHistorysplit {

    int playerPlaces = MatchHistoryRead.playerPlays;
    int computerPlaces = MatchHistoryRead.computerPlays;
    List<Integer> computersPlaces = new ArrayList<>();
    List<Integer> playersPlaces = new ArrayList<>();

    public void split() {
        setComputerPlaces();
        setPlayerPlaces();
    }

    public void setComputerPlaces() {
        while (computerPlaces > 0) {
            computersPlaces.add(0, computerPlaces % 10);
            computerPlaces /= 10;
        }
    }

    public void setPlayerPlaces() {
        while (playerPlaces > 0) {
            playersPlaces.add(0, playerPlaces % 10);
            playerPlaces /= 10;
        }
    }

}


