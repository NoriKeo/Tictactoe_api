package readAndWrite;

import java.sql.SQLException;
import java.sql.Timestamp;

public class MatchTime {
    static Timestamp start;
    static Timestamp end;
    static Timestamp getStart;
    static Timestamp getEnd;


    public static void start() throws SQLException {
         start = new Timestamp(System.currentTimeMillis());
    }

    public static void end() throws SQLException {
            end = new Timestamp(System.currentTimeMillis());
    }
}
