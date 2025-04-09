package game;

import board.Board;
import board.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    private Player player;
    private Board board;

    @BeforeEach
    void setUp() {
        player = Player.getInstance();
        board = new Board();

    }

    @Test
    public void freeFieldFalse(){
        for (int i = 1; i < 9; i++) {
            Position position = new Position(i);
            board.getRows().get(position.getRow()).getFields().get(position.getColumn()).setGameCharacter('¤');
        }

        assertFalse(player.freeField(board,1));
    }

    @Test
    public void freeFieldTrue(){
        for (int i = 2; i < 9; i++) {
            Position position = new Position(i);
            board.getRows().get(position.getRow()).getFields().get(position.getColumn()).setGameCharacter('¤');
        }
       assertTrue(player.freeField(board,1));
    }



}