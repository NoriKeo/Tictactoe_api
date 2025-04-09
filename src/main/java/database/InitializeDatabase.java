package database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class InitializeDatabase {



    public static void initializeTables  () {
        try {
            initializeTableaccounts();
            initializeTableMatch();
            initializeTableMove();
            initializeTableVerdict();
            initializeTableScore();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static void initializeTableMatch() throws SQLException {
        try (Connection connection = ConnectionHandler.getConnection()) {
            Statement stmt = connection.createStatement();
            String createTableSQL = "CREATE TABLE IF NOT EXISTS match (" +
                    "id SERIAL PRIMARY KEY, " +
                    "player_id INT NOT NULL, " +
                    "started_at timestamp , " +
                    "ended timestamp, " +
                    "verdict_id int ," +
                    "FOREIGN KEY (player_id) REFERENCES accounts(player_id)," +
                    "FOREIGN KEY (verdict_id) REFERENCES verdict(id)" +
                    ");";
            stmt.execute(createTableSQL);

        }
    }



    public static void initializeTableMove() throws SQLException {
        try (Connection connection = ConnectionHandler.getConnection()) {
            Statement stmt = connection.createStatement();
            String createTableSQL = "CREATE TABLE IF NOT EXISTS move (" +
                    "id SERIAL PRIMARY KEY, " +
                    "match_id INT NOT NULL, " +
                    "is_player boolean NOT NULL, " +
                    "position int NOT NULL, " +
                    "created_at int NOT NULL," +
                    "move_nr SERIAL, " +
                    "FOREIGN KEY (match_id) REFERENCES match(id)" +
                    ");";
            stmt.execute(createTableSQL);
        }
    }


    public static void initializeTableScore() throws SQLException {
        try (Connection connection = ConnectionHandler.getConnection()) {
            Statement stmt = connection.createStatement();
            String createTableSQL = "CREATE TABLE IF NOT EXISTS score (" +
                    "score_id SERIAL PRIMARY KEY, " +
                    "player_id INT NOT NULL, " +
                    "computer_score INT, " +
                    "player_score INT, " +
                    "draw_score INT," +
                    "FOREIGN KEY (player_id) REFERENCES accounts(player_id)" +
                    ");";
            stmt.execute(createTableSQL);
        }
    }

    public static void initializeTableaccounts() throws SQLException {
        try (Connection connection = ConnectionHandler.getConnection()) {
            Statement stmt = connection.createStatement();
            String createTableSQL = "CREATE TABLE IF NOT EXISTS accounts (" +
                    "player_id SERIAL PRIMARY KEY, " +
                    "player_name varchar(255), " +
                    "passwort varchar(255), " +
                    "security_question varchar(255)" +
                    ");";
            stmt.execute(createTableSQL);


        }
    }

    public static void initializeTableVerdict() throws SQLException {
        try (Connection connection = ConnectionHandler.getConnection()) {
            Statement stmt = connection.createStatement();
            String createTableSQL = "CREATE TABLE IF NOT EXISTS verdict (" +
                    "id SERIAL PRIMARY KEY, " +
                    "reason varchar(255) " +
                    ");";
            stmt.execute(createTableSQL);
        }
    }
}
