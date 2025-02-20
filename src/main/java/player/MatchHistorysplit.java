package player;

import readAndWrite.MatchHistoryReader;

import java.util.ArrayList;
import java.util.List;

public class MatchHistorysplit {


     int playerPlaces = MatchHistoryReader.playerPlays;
     int computerPlaces = MatchHistoryReader.computerPlays;
      List<Integer> computersPlaces = new ArrayList<>();
      List<Integer> playersPlaces = new ArrayList<>();

    public  void split(){
        setComputerPlaces();
        setPlayerPlaces();
    }
    public  void setComputerPlaces() {
        while (computerPlaces > 0) {
            computersPlaces.add(0, computerPlaces % 10);
            computerPlaces /= 10;
        }
    }

    public  void setPlayerPlaces() {
        while (playerPlaces > 0) {
            playersPlaces.add(0, playerPlaces % 10);
            playerPlaces /= 10;
        }
    }


}


