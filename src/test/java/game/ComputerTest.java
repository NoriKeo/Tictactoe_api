package game;

import board.Board;
import board.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

class ComputerTest {

    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board();

        Computer.numbers.clear();
        Computer.draw = false;
    }
    int matchCounter = 0 ;
    int moveCounter = 0;

    @Test
    void computerTestGameStart() {
        Position computerMovement = Computer.getComputerMovement(board,matchCounter,moveCounter);
        assertEquals(5, computerMovement.getIndex());

    }

    @Test
    void computerTestwinMove() {
        Board board = new Board();
        moveCounter = 0;
        matchCounter = 6;
        board.getField(new Position(5)).setGameCharacter('¤');
        board.getField(new Position(4)).setGameCharacter('¤');
        Position computerMovement = Computer.getComputerMovement(board,matchCounter,moveCounter);

        assertEquals(6, computerMovement.getIndex());

    }

    @Test
    void randomPositionTest() {
        for (int i = 1; i <= 9; i++) {
            if (i != 8 && i != 9) {
                board.getField(new Position(i)).setGameCharacter('♡');
            }
        }
        moveCounter = 2;
        matchCounter = 0;
        Position computerMovement = Computer.getComputerMovement(board,matchCounter,moveCounter);
        assertTrue(computerMovement.getIndex() == 9 || computerMovement.getIndex() == 8);
    }



    @Test
    void computerMoveTestDiagonal() {
        board.getField(new Position(1)).setGameCharacter('¤');
        board.getField(new Position(9)).setGameCharacter('¤');
        moveCounter = 1;
        matchCounter = 0;
        Position computerMovement = Computer.getComputerMovement(board,matchCounter,moveCounter);
        assertEquals(5, computerMovement.getIndex());
    }

}