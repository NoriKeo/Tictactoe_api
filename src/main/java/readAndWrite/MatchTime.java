package readAndWrite;

import java.sql.SQLException;
import java.sql.Timestamp;

public class MatchTime {

    static Timestamp getStart;
    static Timestamp getEnd;


    public static Timestamp  start()  {
        Timestamp start;
         start = new Timestamp(System.currentTimeMillis());
         return start;
    }

    public static Timestamp end()  {
            Timestamp end;
            end = new Timestamp(System.currentTimeMillis());
            return end;
    }
}
