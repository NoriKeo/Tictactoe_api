package game;

import board.Board;
//import game.KeepPlaying;
//import nowneed.Match;
import board.Field;
import board.Position;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static java.util.Arrays.stream;


public class Computer {


    private Computer() {

    }

    public static boolean draw = false;

    static ArrayList<Integer> numbers = new ArrayList<>();
    public static List<Field> rowStrategy(Board board) {
        /*default*/
        List<Field> rowStrategys = new ArrayList<>();
        /*default*/
        List<Field> toRemove = new ArrayList<>();
        Field[][] rowBlock = {{board.getField(new Position(1)), board.getField(new Position(2)), board.getField(new Position(3))},
                {board.getField(new Position(4)), board.getField(new Position(5)), board.getField(new Position(6))},
                {board.getField(new Position(7)), board.getField(new Position(8)), board.getField(new Position(9))},};
        for (Field field : rowStrategys) {
            if (!field.isEmpty()) {
                toRemove.add(field);
            }
        }
        if (!toRemove.isEmpty()) {
            rowStrategys.removeAll(toRemove);
        }
        if (rowStrategys.isEmpty()) {
            for (Field[] field : rowBlock) {
                stream(field).forEach(field2 -> stream(field).forEach(field3 -> stream(field).forEach(field4 -> {
                    if (!field2.equals(field3) && field4.isEmpty() && field2.isPlayer() && field3.isPlayer() && rowStrategys.size() < 3) {
                        rowStrategys.add(field4);
                    }


                })));
            }


        }
        return rowStrategys;
    }


    public static List<Field> columnStrategy(Board board) {
        /*default*/
        List<Field> columnStrategys = new ArrayList<>();
        /*default*/
        List<Field> toRemove = new ArrayList<>();
        Field[][] columnBlock = {{board.getField(new Position(1)), board.getField(new Position(4)), board.getField(new Position(7))},
                {board.getField(new Position(2)), board.getField(new Position(5)), board.getField(new Position(8))},
                {board.getField(new Position(3)), board.getField(new Position(6)), board.getField(new Position(9))},};
        for (Field field : columnStrategys) {
            if (!field.isEmpty()) {
                toRemove.add(field);
            }
        }
        if (!toRemove.isEmpty()) {
            columnStrategys.removeAll(toRemove);
        }
        if (columnStrategys.isEmpty()) {
            for (Field[] field : columnBlock) {
                stream(field).forEach(field2 -> stream(field).forEach(field3 -> stream(field).forEach(field4 -> {
                    if (!field2.equals(field3) && field4.isEmpty() && field2.isPlayer() && field3.isPlayer() && columnStrategys.size() < 3) {
                        columnStrategys.add(field4);
                    }
                })));


            }
        }

        return columnStrategys;
    }

    public static List<Field> diagonalStrategy(Board board) {
        /*default*/
        List<Field> diagonalStrategys = new ArrayList<>();
        /*default*/
        List<Field> toRemove = new ArrayList<>();
        Field[][] diagonalBlock = {{board.getField(new Position(1)), board.getField(new Position(5)), board.getField(new Position(9))},
                {board.getField(new Position(3)), board.getField(new Position(5)), board.getField(new Position(7))},};


        for (Field field : diagonalStrategys) {
            if (!field.isEmpty()) {
                toRemove.add(field);
            }
        }
        if (!toRemove.isEmpty()) {
            diagonalStrategys.removeAll(toRemove);
        }

        if (diagonalStrategys.isEmpty()) {
            for (Field[] field : diagonalBlock) {
                stream(field).forEach(field2 -> stream(field).forEach(field3 -> stream(field).forEach(field4 -> {
                    if (!field3.equals(field2) && field4.isEmpty() && field2.isPlayer() && field3.isPlayer() && diagonalStrategys.size() < 3) {
                        diagonalStrategys.add(field4);
                    }
                })));

            }

        }
        return diagonalStrategys;
    }


    public static List<Field> winsStrategy(Board board) {
        /*default*/
        List<Field> winStrategy = new ArrayList<>();
        /*default*/
        List<Field> toRemove = new ArrayList<>();
        Field[][] winStrategys = {{board.getField(new Position(1)), board.getField(new Position(2)), board.getField(new Position(3))},
                {board.getField(new Position(4)), board.getField(new Position(5)), board.getField(new Position(6))},
                {board.getField(new Position(7)), board.getField(new Position(8)), board.getField(new Position(9))},
                {board.getField(new Position(1)), board.getField(new Position(5)), board.getField(new Position(9))},
                {board.getField(new Position(3)), board.getField(new Position(5)), board.getField(new Position(7))},
                {board.getField(new Position(1)), board.getField(new Position(4)), board.getField(new Position(7))},
                {board.getField(new Position(2)), board.getField(new Position(5)), board.getField(new Position(8))},
                {board.getField(new Position(3)), board.getField(new Position(6)), board.getField(new Position(9))},
        };


        for (Field field : winStrategy) {
            if (!field.isEmpty()) {
                toRemove.add(field);
            }
        }
        if (!toRemove.isEmpty()) {
            winStrategy.removeAll(toRemove);
        }


        if (winStrategy.isEmpty()) {
            for (Field[] field : winStrategys) {
                stream(field).forEach(field2 -> stream(field).forEach(field3 -> stream(field).forEach(field4 -> {
                    if (!field4.equals(field2) && !field3.equals(field4) && !field2.equals(field3) && winStrategy.size() < 3 && field2.isEmpty() && field3.isEmpty() && field4.isEmpty()) {
                        winStrategy.add(field2);
                        winStrategy.add(field3);
                        winStrategy.add(field4);
                    }
                })));


            }
        }
        return winStrategy;
    }

    public static List<Field> computerWin(Board board) {
        /*default*/
        List<Field> computerStrategy = new ArrayList<>();
        /*default*/
        List<Field> toRemove = new ArrayList<>();
        Field[][] computerStrategys = {{board.getField(new Position(1)), board.getField(new Position(2)), board.getField(new Position(3))},
                {board.getField(new Position(4)), board.getField(new Position(5)), board.getField(new Position(6))},
                {board.getField(new Position(7)), board.getField(new Position(8)), board.getField(new Position(9))},
                {board.getField(new Position(1)), board.getField(new Position(5)), board.getField(new Position(9))},
                {board.getField(new Position(3)), board.getField(new Position(5)), board.getField(new Position(7))},
                {board.getField(new Position(1)), board.getField(new Position(4)), board.getField(new Position(7))},
                {board.getField(new Position(2)), board.getField(new Position(5)), board.getField(new Position(8))},
                {board.getField(new Position(3)), board.getField(new Position(6)), board.getField(new Position(9))},
        };

        for (Field field : computerStrategy) {
            if (!field.isEmpty()) {
                toRemove.add(field);
            }
        }
        if (!toRemove.isEmpty()) {
            computerStrategy.removeAll(toRemove);
        }

        if (computerStrategy.isEmpty()) {
            for (Field[] field : computerStrategys) {
                stream(field).forEach(field2 -> stream(field).forEach(field3 -> stream(field).forEach(field4 -> {
                    if (!field4.equals(field2) && !field3.equals(field4) && !field2.equals(field3) && computerStrategy.size() <= 1 && field2.isEmpty() && field3.isComputer() && field4.isComputer()) {
                        computerStrategy.add(field2);

                    }
                })));

            }
        }
        return computerStrategy;

    }


   public static Position getComputerMovement(Board board, int matchCounter , int movecounter) {
        Random random = new Random();

        columnStrategy(board);
        diagonalStrategy(board);
        winsStrategy(board);
        computerWin(board);
        if (movecounter < 7 ) {
            for (int i = 0; i <= 9; i++) {
                numbers.add(i);
            }
        }

        if (board.getField(new Position(5)).isEmpty()) {
            return new Position(5);
        }


        if (!computerWin(board).isEmpty()) {

            Position computerRandomMove = computerRandomMove(board,numbers,matchCounter,random,10);
            if (computerRandomMove == null){
                Field freeField5 = computerWin(board).getFirst();
                System.out.println("computer will gewinnen");
                return freeField5.getPosition();
            }
            return computerRandomMove;

        }

        if (!rowStrategy(board).isEmpty()) {

            Position computerRandomMove = computerRandomMove(board,numbers,matchCounter,random,24);
            if (computerRandomMove == null){
                Field freeField3 = rowStrategy(board).getFirst();
                System.out.println("row");
                return freeField3.getPosition();
            }
            return computerRandomMove;

        }
        if (!diagonalStrategy(board).isEmpty()) {
            Position computerRandomMove = computerRandomMove(board,numbers,matchCounter,random,30);
            if (computerRandomMove == null){
                Field freeField1 = diagonalStrategy(board).getFirst();
                System.out.println("diagonal");
                return freeField1.getPosition();
            }
            return computerRandomMove;
        }
        if (!columnStrategy(board).isEmpty()) {
            //int index = random.nextInt(columnStrateg.size());
            Position computerRandomMove = computerRandomMove(board,numbers,matchCounter,random,18);
            if (computerRandomMove == null){
                Field freeField2 = columnStrategy(board).getFirst();
                Position position = freeField2.getPosition();
                System.out.println("column");
                return position;
            }
            return computerRandomMove;

        }
        if (winsStrategy(board).isEmpty()) {
            draw = true;

        }

       Position computerRandomMove = computerRandomMove(board,numbers,matchCounter,random,8);
        if (computerRandomMove == null){
            //int index2 = random.nextInt(winsstrateg.size());
            Field freeField = winsStrategy(board).getLast();

            System.out.println("winverfolgung");
            return freeField.getPosition();
        }
       return computerRandomMove;

    }

    public static Position computerRandomMove(Board board, List<Integer> numbers, int matchCounter, Random random, int aggravationPhase) {
            if (matchCounter < aggravationPhase) {
                if (numbers.isEmpty()) {
                    int freeField;
                    do {
                        freeField = random.nextInt(9) +1;
                    } while (!board.getField(new Position(freeField)).isEmpty());

                    return new Position(freeField);
                }

                Collections.shuffle(numbers, random);
                for (int freeField : numbers) {
                    if (board.getField(new Position(freeField)).isEmpty()) {
                        return new Position(freeField);
                    }
                }
            }
            return null;
    }



}




