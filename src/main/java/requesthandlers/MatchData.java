package requesthandlers;

public class MatchData {
    int id;
    String startedAt;
    String endedAt;
    int verdictId;

    public MatchData(int id, String startedAt, String endedAt, int verdictId) {
        this.id = id;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.verdictId = verdictId;
    }
}
