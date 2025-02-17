package player;

import readAndWrite.MatchHistoryRead;

import java.util.ArrayList;
import java.util.List;

public class MatchHistorysplit {


    static int playerPlaces = MatchHistoryRead.playerPlays;
    static int computerPlaces = MatchHistoryRead.computerPlays;
    static List<Integer> computersPlaces = new ArrayList<>();
    static List<Integer> playersPlaces = new ArrayList<>();

    public static void split(){
        setComputerPlaces();
        setPlayerPlaces();
    }
    public static void setComputerPlaces() {
        while (computerPlaces > 0) {
            computersPlaces.add(0, computerPlaces % 10);
            computerPlaces /= 10;
        }
    }

    public static void setPlayerPlaces() {
        while (playerPlaces > 0) {
            playersPlaces.add(0, playerPlaces % 10);
            playerPlaces /= 10;
        }
    }


}


