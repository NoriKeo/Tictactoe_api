package game;

import board.Board;
//import game.KeepPlaying;
//import nowneed.Match;
import board.Field;
import board.Position;

import java.util.ArrayList;
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
        if (movecounter == 0 ) {
            for (int i = 0; i <= 9; i++) {
                numbers.add(i);
            }
        }

        if (board.getField(new Position(5)).isEmpty()) {
            return new Position(5);
        }


        if (!computerWin(board).isEmpty()) {
            if (matchCounter < 2) {

                for (int i = 0; i <= numbers.size(); i++) {
                    int freeField9 = random.nextInt(numbers.size()) + 1;
                    if (freeField9 > 9) {
                        freeField9 = freeField9 - 1;
                    }

                    if (board.getField(new Position(freeField9)).isEmpty()) {
                        return new Position(freeField9);
                    }
                }
            }
            Field freeField5 = computerWin(board).getFirst();
            System.out.println("computer will gewinnen");
            return freeField5.getPosition();
        }

        if (!rowStrategy(board).isEmpty()) {
            if (matchCounter < 2) {
                while (true) {
                    int freeField3 = random.nextInt(numbers.size()) + 1;
                    if (freeField3 > 9) {
                        freeField3 = freeField3 - 1;
                    }

                    if (board.getField(new Position(freeField3)).isEmpty()) {
                        return new Position(freeField3);
                    }
                }
            }
            Field freeField3 = rowStrategy(board).getFirst();
            System.out.println("row");
            return freeField3.getPosition();

        }
        if (!diagonalStrategy(board).isEmpty()) {
            if (matchCounter < 3) {
                for (int i = 0; i <= numbers.size(); i++) {
                    int freeField1 = random.nextInt(numbers.size()) + 1;
                    if (freeField1 > 9) {
                        freeField1 = freeField1 - 1;
                    }

                    if (board.getField(new Position(freeField1)).isEmpty()) {
                        return new Position(freeField1);
                    }
                }
            }
            Field freeField1 = diagonalStrategy(board).getFirst();
            System.out.println("diagonal");
            return freeField1.getPosition();


        }
        if (!columnStrategy(board).isEmpty()) {
            //int index = random.nextInt(columnStrateg.size());
            if (matchCounter < 4) {
                for (int i = 0; i <= numbers.size(); i++) {
                    int freeField2 = random.nextInt(numbers.size()) + 1;
                    if (freeField2 > 9) {
                        freeField2 = freeField2 - 1;
                    }

                    if (board.getField(new Position(freeField2)).isEmpty()) {
                        return new Position(freeField2);
                    }
                }
            }
            Field freeField2 = columnStrategy(board).getFirst();
            Position position = freeField2.getPosition();
            System.out.println("column");
            return position;

        }
        if (winsStrategy(board).isEmpty()) {


            draw = true;
            /*if (!KeepPlaying.keepPlaying(board)) {
                System.out.println("Bye Bye");
            }*/
        }
        if (matchCounter < 2) {
            for (int i = 0; i <= numbers.size(); i++) {
                int freeField = random.nextInt(numbers.size()) + 1;
                if (freeField > 9) {
                    freeField = freeField - 1;
                }

                if (board.getField(new Position(freeField)).isEmpty()) {
                    return new Position(freeField);
                }
            }
        }
        //int index2 = random.nextInt(winsstrateg.size());
        Field freeField = winsStrategy(board).getLast();

        System.out.println("winverfolgung");
        return freeField.getPosition();

    }


}




