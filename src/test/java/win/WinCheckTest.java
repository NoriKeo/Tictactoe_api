package win;

import board.Board;
import board.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WinCheckTest {
    private Board board;

    @BeforeEach
    public void setUp() {
        board = new Board();
    }

    @Test
    public void testWinCheckRow() {
        board.getRows().get(0).getFields().get(0).setGameCharacter('♡');
        board.getRows().get(0).getFields().get(1).setGameCharacter('♡');
        board.getRows().get(0).getFields().get(2).setGameCharacter('♡');

        GamePlayMove playMove = new GamePlayMove(new Position(1), '♡');
        assertTrue(WinCheck.isWin(board, playMove));

    }

    @Test
    public void testWinCheckColumn() {
        board.getRows().get(0).getFields().get(0).setGameCharacter('♡');
        board.getRows().get(1).getFields().get(1).setGameCharacter('♡');
        board.getRows().get(2).getFields().get(2).setGameCharacter('♡');

        GamePlayMove playerMove = new GamePlayMove(new Position(1), '♡');
        assertTrue(WinCheck.isWin(board, playerMove));
    }

    @Test
    public void testWinCheckDiagonal() {
        board.getRows().get(0).getFields().get(0).setGameCharacter('♡');
        board.getRows().get(1).getFields().get(1).setGameCharacter('♡');
        board.getRows().get(2).getFields().get(2).setGameCharacter('♡');

        GamePlayMove playerMove = new GamePlayMove(new Position(1), '♡');
        assertTrue(WinCheck.isWin(board, playerMove));
    }

    @Test
    public void testWinCheckDiagonal2() {
        board.getRows().get(0).getFields().get(2).setGameCharacter('¤');
        board.getRows().get(1).getFields().get(1).setGameCharacter('¤');
        board.getRows().get(2).getFields().get(0).setGameCharacter('¤');

        GamePlayMove computerMove = new GamePlayMove(new Position(3), '¤');
        assertTrue(WinCheck.isWin(board, computerMove));
    }

    @Test
    public void drawWinCheck() {
        board.getRows().get(0).getFields().get(0).setGameCharacter('♡');
        board.getRows().get(0).getFields().get(1).setGameCharacter('¤');
        board.getRows().get(0).getFields().get(2).setGameCharacter('♡');

        board.getRows().get(1).getFields().get(0).setGameCharacter('¤');
        board.getRows().get(1).getFields().get(1).setGameCharacter('♡');
        board.getRows().get(1).getFields().get(2).setGameCharacter('¤');

        board.getRows().get(2).getFields().get(0).setGameCharacter('¤');
        board.getRows().get(2).getFields().get(1).setGameCharacter('♡');
        board.getRows().get(2).getFields().get(2).setGameCharacter('¤');

        GamePlayMove playerMove = new GamePlayMove(new Position(1), '♡');
        GamePlayMove computerMove = new GamePlayMove(new Position(2), '¤');

        assertFalse(WinCheck.isWin(board, playerMove));
        assertFalse(WinCheck.isWin(board, computerMove));

    }

    @Test
    public void computerWinCheckBlock() {
        board.getRows().get(0).getFields().get(0).setGameCharacter('¤');
        board.getRows().get(1).getFields().get(1).setGameCharacter('¤');
        board.getRows().get(2).getFields().get(2).setGameCharacter('♡');

        GamePlayMove computerMove = new GamePlayMove(new Position(1), '¤');
        assertFalse(WinCheck.isWin(board, computerMove));

    }
}