package game;

import java.sql.Timestamp;

public class GameTime {

    public static Timestamp getStart;
    public static Timestamp getEnd;


    public static Timestamp start() {
        Timestamp start;
        start = new Timestamp(System.currentTimeMillis());
        return start;
    }

    public static Timestamp end() {
        Timestamp end;
        end = new Timestamp(System.currentTimeMillis());
        return end;
    }
}
